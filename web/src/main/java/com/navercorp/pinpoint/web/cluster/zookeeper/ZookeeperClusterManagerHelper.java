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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.navercorp.pinpoint.web.cluster.zookeeper.ZookeeperClusterManager.PushWebClusterJob;

/**
 * @author Taejin Koo
 */
public class ZookeeperClusterManagerHelper {

    private static final String PATH_SEPERATOR = "/";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ZookeeperClusterManagerHelper() {
    }

    Map<String, byte[]> getCollectorData(ZookeeperClient client, String path) {
        try {
            List<String> collectorList = client.getChildren(path, true);

            Map<String, byte[]> map = new HashMap<String, byte[]>();

            for (String collector : collectorList) {
                String node = bindingPathAndZnode(path, collector);

                byte[] data = client.getData(node, true);
                map.put(node, data);
            }

            return map;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        return null;
    }

    String bindingPathAndZnode(String path, String znodeName) {
        StringBuilder fullPath = new StringBuilder();

        fullPath.append(path);
        if (!path.endsWith(PATH_SEPERATOR)) {
            fullPath.append(PATH_SEPERATOR);
        }
        fullPath.append(znodeName);

        return fullPath.toString();
    }

    String extractCollectorClusterId(String path, String collectorClusterPath) {
        int index = path.indexOf(collectorClusterPath);

        int startPosition = index + collectorClusterPath.length() + 1;

        if (path.length() > startPosition) {
            String id = path.substring(startPosition);
            return id;
        }

        return null;
    }

    boolean pushWebClusterResource(ZookeeperClient client, PushWebClusterJob job) {
        if (job == null) {
            return false;
        }
        
        String zNodePath = job.getZnodePath();
        byte[] contents = job.getContents();

        try {
            if (!client.exists(zNodePath)) {
                client.createPath(zNodePath);
            }

            // ip:port zNode naming scheme
            String nodeName = client.createNode(zNodePath, contents, CreateMode.EPHEMERAL);
            logger.info("Register Web Cluster Zookeeper UniqPath = {}.", zNodePath);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return false;
    }

    Map<String, byte[]> syncPullCollectorCluster(ZookeeperClient client, String path) {
        Map<String, byte[]> map = getCollectorData(client, path);

        if (map == null) {
            return Collections.emptyMap();
        }

        Map<String, byte[]> result = new HashMap<String, byte[]>();
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            String key = entry.getKey();
            byte[] value = entry.getValue();

            String id = extractCollectorClusterId(key, path);
            if (id == null) {
                logger.error("Illegal Collector Path({}) finded.", key);
                continue;
            }
            result.put(id, value);
        }

        return result;
    }

}
