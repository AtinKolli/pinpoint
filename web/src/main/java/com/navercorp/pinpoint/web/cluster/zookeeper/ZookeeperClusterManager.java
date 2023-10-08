/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.cluster.zookeeper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.navercorp.pinpoint.rpc.util.TimerFactory;
import com.navercorp.pinpoint.web.cluster.ClusterManager;
import com.navercorp.pinpoint.web.cluster.CollectorClusterInfoRepository;
import com.navercorp.pinpoint.web.cluster.zookeeper.exception.NoNodeException;

/**
 * @author koo.taejin
 */
public class ZookeeperClusterManager implements ClusterManager, Watcher {

    static final long DEFAULT_RECONNECT_DELAY_WHEN_SESSION_EXPIRED = 30000;

    private static final String PINPOINT_CLUSTER_PATH = "/pinpoint-cluster";
    private static final String PINPOINT_WEB_CLUSTER_PATh = PINPOINT_CLUSTER_PATH + "/web";
    private static final String PINPOINT_COLLECTOR_CLUSTER_PATH = PINPOINT_CLUSTER_PATH + "/collector";

    private static final long SYNC_INTERVAL_TIME_MILLIS = 15 * 1000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ZookeeperClient client;
    private final ZookeeperClusterManagerHelper zookeeperClusterManagerHelper;

    private final Object initilizeLock = new Object();
    
    private final int retryInterval;

    private final Timer timer;

    private final AtomicReference<PushWebClusterJob> job = new AtomicReference<ZookeeperClusterManager.PushWebClusterJob>();

    private final CollectorClusterInfoRepository collectorClusterInfo = new CollectorClusterInfoRepository();

    public ZookeeperClusterManager(String zookeeperAddress, int sessionTimeout, int retryInterval) throws KeeperException, IOException, InterruptedException {
        synchronized(initilizeLock) {
            this.client = new ZookeeperClient(zookeeperAddress, sessionTimeout, this, DEFAULT_RECONNECT_DELAY_WHEN_SESSION_EXPIRED);
            this.retryInterval = retryInterval;
            // it could be better to create upon failure
            this.timer = createTimer();
            this.zookeeperClusterManagerHelper = new ZookeeperClusterManagerHelper();
        }
    }

    // Retry upon failure (1 min retry period)
    // not too much overhead, just logging
    @Override
    public boolean registerWebCluster(String zNodeName, byte[] contents) {
        String zNodePath = zookeeperClusterManagerHelper.bindingPathAndZnode(PINPOINT_WEB_CLUSTER_PATh, zNodeName);

        logger.info("Create Web Cluster Zookeeper UniqPath = {}", zNodePath);

        PushWebClusterJob job = new PushWebClusterJob(zNodePath, contents, retryInterval);
        if (!this.job.compareAndSet(null, job)) {
            logger.warn("Already Register Web Cluster Node.");
            return false;
        }

        // successful even for schedular registration completion
        if (!isConnected()) {
            logger.info("Zookeeper is Disconnected.");
            return true;
        }

        if (!zookeeperClusterManagerHelper.pushWebClusterResource(client, job)) {
            timer.newTimeout(job, job.getRetryInterval(), TimeUnit.MILLISECONDS);
        }

        return true;
    }

    @Override
    public void process(WatchedEvent event) {
        synchronized (initilizeLock) {
            // wait for client variable to be assigned.
        }

        logger.info("Zookeepr Event({}) ocurred.", event);
        
        KeeperState state = event.getState();
        EventType eventType = event.getType();
        String path = event.getPath();

        // when this happens, ephemeral node disappears
        // reconnects automatically, and process gets notified for all events
        boolean result = false;
        if (ZookeeperUtils.isDisconnectedEvent(event)) {
            result = handleDisconnected();
            if (state == KeeperState.Expired) {
                client.reconnectWhenSessionExpired();
            }
        } else if (state == KeeperState.SyncConnected || state == KeeperState.NoSyncConnected) {
            if (eventType == EventType.None) {
                result = handleConnected();
            } else if (eventType == EventType.NodeChildrenChanged) {
                result = handleNodeChildrenChanged(path);
            } else if (eventType == EventType.NodeDeleted) {
                result = handleNodeDeleted(path);
            } else if (eventType == EventType.NodeDataChanged) {
                result = handleNodeDataChanged(path);
            }
        }

        if (result) {
            logger.info("Zookeeper Event({}) successed.", event);
        } else {
            logger.info("Zookeeper Event({}) failed.", event);
        }
    }

    private boolean handleDisconnected() {
        connected.compareAndSet(true, false);
        collectorClusterInfo.clear();
        return true;
    }

    private boolean handleConnected() {
        boolean result = true;

        // is it ok to keep this since previous condition was possibly RUN
        boolean changed = connected.compareAndSet(false, true);
        if (changed) {
            PushWebClusterJob job = this.job.get();
            if (job != null) {
                if (!zookeeperClusterManagerHelper.pushWebClusterResource(client, job)) {
                    timer.newTimeout(job, job.getRetryInterval(), TimeUnit.MILLISECONDS);
                    result = false;
                }
            }

            if (!syncPullCollectorCluster()) {
                timer.newTimeout(new PullCollectorClusterJob(), SYNC_INTERVAL_TIME_MILLIS, TimeUnit.MILLISECONDS);
                result = false;
            }
        } else {
            result = false;
        }

        return result;
    }

    private boolean handleNodeChildrenChanged(String path) {
        if (PINPOINT_COLLECTOR_CLUSTER_PATH.equals(path)) {
            if (syncPullCollectorCluster()) {
                return true;
            }
            timer.newTimeout(new PullCollectorClusterJob(), SYNC_INTERVAL_TIME_MILLIS, TimeUnit.MILLISECONDS);
        }

        return false;
    }

    private boolean handleNodeDeleted(String path) {
        if (path != null) {
            String id = zookeeperClusterManagerHelper.extractCollectorClusterId(path, PINPOINT_COLLECTOR_CLUSTER_PATH);
            if (id != null) {
                collectorClusterInfo.remove(id);
                return true;
            }
        }
        return false;
    }

    private boolean handleNodeDataChanged(String path) {
        if (path != null) {
            String id = zookeeperClusterManagerHelper.extractCollectorClusterId(path, PINPOINT_COLLECTOR_CLUSTER_PATH);
            if (id != null) {
                if (pushCollectorClusterData(id)) {
                    return true;
                }
                timer.newTimeout(new PullCollectorClusterJob(), SYNC_INTERVAL_TIME_MILLIS, TimeUnit.MILLISECONDS);
            }
        }

        return false;
    }

    @Override
    public void close() {
        if (timer != null) {
            timer.stop();
        }

        if (client != null) {
            this.client.close();
        }
    }

    @Override
    public List<String> getRegisteredAgentList(String applicationName, String agentId, long startTimeStamp) {
        return collectorClusterInfo.get(applicationName, agentId, startTimeStamp);
    }

    private Timer createTimer() {
        HashedWheelTimer timer = TimerFactory.createHashedWheelTimer("Pinpoint-Web-Cluster-Timer", 100, TimeUnit.MILLISECONDS, 512);
        timer.start();
        return timer;
    }

    public boolean isConnected() {
        return connected.get();
    }

    private boolean syncPullCollectorCluster() {
        synchronized (this) {
            Map<String, byte[]> map = zookeeperClusterManagerHelper.syncPullCollectorCluster(client, PINPOINT_COLLECTOR_CLUSTER_PATH);
            if (Collections.EMPTY_MAP == map) {
                return false;
            }
            
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                collectorClusterInfo.put(entry.getKey(), entry.getValue());
            }
            return true;
        }
    }

    private boolean pushCollectorClusterData(String id) {
        String path = zookeeperClusterManagerHelper.bindingPathAndZnode(PINPOINT_COLLECTOR_CLUSTER_PATH, id);
        synchronized (this) {
            try {
                byte[] data = client.getData(path, true);

                collectorClusterInfo.put(id, data);
                return true;
            } catch(NoNodeException e) {
                logger.warn("No node path({}).", path);
                collectorClusterInfo.remove(id);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }

            return false;
        }
    }


    class PushWebClusterJob implements TimerTask {
        private final String znodeName;
        private final byte[] contents;
        private final int retryInterval;

        public PushWebClusterJob(String znodeName, byte[] contents, int retryInterval) {
            this.znodeName = znodeName;
            this.contents = contents;
            this.retryInterval = retryInterval;
        }

        public String getZnodePath() {
            return znodeName;
        }

        public byte[] getContents() {
            return contents;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        @Override
        public String toString() {
            StringBuilder toString = new StringBuilder();
            toString.append(this.getClass().getSimpleName());
            toString.append(", Znode=").append(getZnodePath());

            return toString.toString();
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            logger.info("Reservation Job({}) started.", this.getClass().getSimpleName());

            if (!isConnected()) {
                return;
            }

            if (!zookeeperClusterManagerHelper.pushWebClusterResource(client, this)) {
                timer.newTimeout(this, getRetryInterval(), TimeUnit.MILLISECONDS);
            }
        }
    }

    class PullCollectorClusterJob implements TimerTask {

        @Override
        public void run(Timeout timeout) throws Exception {
            logger.info("Reservation Job({}) started.", this.getClass().getSimpleName());

            if (!isConnected()) {
                return;
            }

            if (!syncPullCollectorCluster()) {
                timer.newTimeout(new PullCollectorClusterJob(), SYNC_INTERVAL_TIME_MILLIS, TimeUnit.MILLISECONDS);
            }
        }
    }

}