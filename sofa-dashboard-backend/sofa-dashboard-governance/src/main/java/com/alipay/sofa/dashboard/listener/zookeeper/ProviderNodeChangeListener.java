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

import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.rpc.client.ProviderHelper;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.client.ProviderInfoAttrs;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bystander
 * @version $Id: ServiceNodeChangeListener.java, v 0.1 2018年12月12日 11:25 bystander Exp $
 */
@Component
public class ProviderNodeChangeListener implements PathChildrenCacheListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderNodeChangeListener.class);

    @Autowired
    private RegistryDataCache   registryDataCache;

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

        // 解决自动重连情况下出现的空指针问题
        ChildData data = event.getData();
        if (data == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("event type : {}", event.getType());
            }
            return;
        }

        final String path = data.getPath();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("provider : {}", path);
        }
        switch (event.getType()) {

            case CHILD_ADDED:
                String providerData = StringUtil.substringAfterLast(path, "/");
                String serviceName = StringUtil.substringBetween(path, "/sofa-rpc/", "/providers/");
                List<RpcProvider> providerDataList = new ArrayList<>();
                providerDataList.add(convert2Provider(serviceName, providerData));
                registryDataCache.addProviders(serviceName, providerDataList);
                break;
            case CHILD_REMOVED:

                List<RpcProvider> removeProviders = new ArrayList<>();
                String removeProviderData = StringUtil.substringAfterLast(path, "/");
                String removeServiceName = StringUtil.substringBetween(path, "/sofa-rpc/",
                    "/providers/");
                removeProviders.add(convert2Provider(removeServiceName, removeProviderData));
                registryDataCache.removeProviders(removeServiceName, removeProviders);
                break;
            case CHILD_UPDATED:

                List<RpcProvider> updateProviders = new ArrayList<>();
                String updateProviderData = StringUtil.substringAfterLast(path, "/");
                String updateServiceName = StringUtil.substringBetween(path, "/sofa-rpc/",
                    "/providers/");
                updateProviders.add(convert2Provider(updateServiceName, updateProviderData));
                registryDataCache.updateProviders(updateServiceName, updateProviders);
                break;

            default:
                break;
        }

    }

    private RpcProvider convert2Provider(String serviceName, String providerData) {

        try {
            providerData = URLDecoder.decode(providerData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        RpcProvider rpcProvider = new RpcProvider();
        ProviderInfo providerInfo = ProviderHelper.toProviderInfo(providerData);

        rpcProvider.setServiceName(serviceName);
        rpcProvider.setAddress(providerInfo.getHost());
        rpcProvider.setPort(providerInfo.getPort());
        String appName = providerInfo.getStaticAttr(ProviderInfoAttrs.ATTR_APP_NAME);
        rpcProvider.setAppName(appName);
        rpcProvider.setWeight(providerInfo.getWeight());
        return rpcProvider;
    }
}