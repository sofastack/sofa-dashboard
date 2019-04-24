/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.dashboard.listener.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bystander
 * @version $Id: ServiceNodeChangeListener.java, v 0.1 2018年12月12日 11:25 bystander Exp $
 */
@Component
public class ServiceNodeChangeListener implements PathChildrenCacheListener {

    private static final Logger        LOGGER        = LoggerFactory
                                                         .getLogger(ServiceNodeChangeListener.class);
    private static final String        PROVIDERS_KEY = "providers";
    private static final String        CONSUMERS_KEY = "consumers";

    @Autowired
    private ProviderNodeChangeListener providerNodeChangeListener;

    @Autowired
    private ConsumerNodeChangeListener consumerNodeChangeListener;

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

        switch (event.getType()) {
        //加了一个provider
            case CHILD_ADDED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("service event type ={},event={}", event.getType(),
                        event.getData());
                }
                String path = event.getData().getPath();

                if (path.endsWith(PROVIDERS_KEY)) {
                    PathChildrenCache cache2 = new PathChildrenCache(client, path, true);
                    cache2.getListenable().addListener(providerNodeChangeListener);
                    cache2.start();
                } else if (path.endsWith(CONSUMERS_KEY)) {
                    PathChildrenCache cache2 = new PathChildrenCache(client, path, true);
                    cache2.getListenable().addListener(consumerNodeChangeListener);
                    cache2.start();
                }
                break;
            //删了一个provider
            case CHILD_REMOVED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("event type ={},event={}", event.getType(), event.getData());
                }
                break;
            // 更新一个Provider
            case CHILD_UPDATED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("event type ={},event={}", event.getType(), event.getData());
                }
                break;
            default:
                break;
        }
    }
}