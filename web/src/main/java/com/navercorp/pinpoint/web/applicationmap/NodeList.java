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

package com.navercorp.pinpoint.web.applicationmap;

import com.navercorp.pinpoint.web.vo.Application;

import java.util.*;

/**
 * @author emeroad
 */
public class NodeList {

    private final Map<Application, Node> nodeMap = new HashMap<Application, Node>();

    public Collection<Node> getNodeList() {
        return this.nodeMap.values();
    }

    public Node findNode(Application application) {
        if (application == null) {
            throw new NullPointerException("application must not be null");
        }
        return this.nodeMap.get(application);
    }

    public boolean addNode(Node node) {
        if (node == null) {
            throw new NullPointerException("node must not be null");
        }
        final Application nodeId = node.getApplication();
        Node findNode = findNode(nodeId);
        if (findNode != null) {
            return false;
        }
        return nodeMap.put(nodeId, node) == null;
    }


    public void addNodeList(NodeList nodeList) {
        if (nodeList == null) {
            throw new NullPointerException("nodeList must not be null");
        }
        for (Node node : nodeList.getNodeList()) {
            addNode(node);
        }
    }

    public boolean containsNode(Application application) {
        if (application == null) {
            throw new NullPointerException("application must not be null");
        }
        return nodeMap.containsKey(application);
    }

    public int size() {
        return this.nodeMap.size();
    }
}