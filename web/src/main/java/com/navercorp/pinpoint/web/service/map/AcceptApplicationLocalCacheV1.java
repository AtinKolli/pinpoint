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

package com.navercorp.pinpoint.web.service.map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * remove later
 * @author emeroad
 */
@Deprecated
public class AcceptApplicationLocalCacheV1 {

    private final Map<String, Set<AcceptApplication>> acceptApplicationLocalCacheV1 = new HashMap<String, Set<AcceptApplication>>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Set<AcceptApplication> get(String host) {
        final Set<AcceptApplication> hit = acceptApplicationLocalCacheV1.get(host);
        if (CollectionUtils.isNotEmpty(hit)) {
            logger.debug("acceptApplicationLocalCacheV1 hit");
            return hit;
        }

        return Collections.emptySet();
    }

    public void put(String host, Set<AcceptApplication> acceptApplicationSet) {

        if (CollectionUtils.isEmpty(acceptApplicationSet)) {
            // initialize for empty value
            Set<AcceptApplication> emptySet = Collections.emptySet();
            acceptApplicationLocalCacheV1.put(host, emptySet);
            return ;
        }
        // build cache
        for (AcceptApplication acceptApplication : acceptApplicationSet) {
            Set<AcceptApplication> findSet = acceptApplicationLocalCacheV1.get(acceptApplication.getHost());
            if (findSet == null) {
                findSet = new HashSet<AcceptApplication>();
                acceptApplicationLocalCacheV1.put(acceptApplication.getHost(), findSet);
            }
            findSet.add(acceptApplication);
        }
    }

}
