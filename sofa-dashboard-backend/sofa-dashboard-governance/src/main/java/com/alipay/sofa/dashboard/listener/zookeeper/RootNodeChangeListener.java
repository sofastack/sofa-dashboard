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

import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bystander
 * @version $Id: ServiceNodeChangeListener.java, v 0.1 2018年12月12日 11:25 bystander Exp $
 */
@Component
public class RootNodeChangeListener implements PathChildrenCacheListener {

    private static final Logger       LOGGER = LoggerFactory
                                                 .getLogger(RootNodeChangeListener.class);

    @Autowired
    private RegistryDataCache         registryDataCache;

    @Autowired
    private ServiceNodeChangeListener serviceNodeChangeListener;

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        String serviceName;
        switch (event.getType()) {
        //加了一个provider
            case CHILD_ADDED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("event type ={},event={}", event.getType(), event.getData());
                }
                String addPath = event.getData().getPath();

                List<RpcService> services = new ArrayList<>();
                RpcService service = new RpcService();

                serviceName = StringUtils.substringAfter(addPath,
                    SofaDashboardConstants.SEPARATOR + SofaDashboardConstants.DEFAULT_GROUP
                            + SofaDashboardConstants.SEPARATOR);
                service.setServiceName(serviceName);
                services.add(service);
                registryDataCache.addService(services);

                PathChildrenCache pathChildrenCache = new PathChildrenCache(client, addPath, true);
                pathChildrenCache.getListenable().addListener(serviceNodeChangeListener);
                pathChildrenCache.start();
                break;
            //删了一个provider
            case CHILD_REMOVED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("event type ={},event={}", event.getType(), event.getData());
                }

                String removePath = event.getData().getPath();
                List<RpcService> rpcServices = new ArrayList<>();
                RpcService removeServices = new RpcService();
                serviceName = StringUtils.substringAfter(removePath,
                    SofaDashboardConstants.SEPARATOR + SofaDashboardConstants.DEFAULT_GROUP
                            + SofaDashboardConstants.SEPARATOR);

                removeServices.setServiceName(serviceName);
                rpcServices.add(removeServices);
                registryDataCache.removeService(rpcServices);

                break;
            // 更新一个Provider
            case CHILD_UPDATED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("event type ={},event={}", event.getType(), event.getData());
                }
                String updatePath = event.getData().getPath();
                RpcService updateServices = new RpcService();
                serviceName = StringUtils.substringAfter(updatePath,
                    SofaDashboardConstants.SEPARATOR + SofaDashboardConstants.DEFAULT_GROUP
                            + SofaDashboardConstants.SEPARATOR);
                updateServices.setServiceName(serviceName);
                registryDataCache.updateService(updateServices);

                break;
            default:
                break;
        }
    }
}