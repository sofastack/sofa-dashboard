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
package com.alipay.sofa.dashboard.controller;

import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient;
import com.alipay.sofa.dashboard.model.ServiceModel;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.dashboard.registry.SofaAdminRegistry;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务治理
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/7 下午5:05
 * @since:
 **/
@RestController
@RequestMapping("/api/service")
public class ServiceManageController {

    @Autowired
    private RegistryDataCache      registryDataCache;

    @Autowired
    private Environment            environment;

    @Autowired
    private SofaRegistryRestClient sofaRegistryRestClient;

    /**
     * 获取服务列表
     *
     * @return
     */
    @GetMapping("all")
    public List<ServiceModel> queryServiceList() {
        doRefreshCache();
        List<ServiceModel> data = new ArrayList<>();
        Map<String, RpcService> rpcServices = registryDataCache.fetchService();
        for (Map.Entry<String, RpcService> rpcServiceEntry : rpcServices.entrySet()) {
            final String serviceName = rpcServiceEntry.getKey();
            ServiceModel model = fetchServiceModel(serviceName);
            data.add(model);
        }

        return data;
    }

    private List<RpcProvider> fetchProviderData(String serviceName) {
        doRefreshCache();
        return registryDataCache.fetchProvidersByService(serviceName);
    }

    private List<RpcConsumer> fetchConsumerData(String serviceName) {
        doRefreshCache();
        return registryDataCache.fetchConsumersByService(serviceName);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @RequestMapping("query/providers")
    public List<RpcProvider> queryServiceProviders(@RequestParam("dataid") String serviceName) {
        return fetchProviderData(serviceName);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @RequestMapping("query/consumers")
    public List<RpcConsumer> queryServiceConsumers(@RequestParam("dataid") String serviceName) {
        return fetchConsumerData(serviceName);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @RequestMapping("query/services")
    public List<ServiceModel> queryService(@RequestParam("serviceName") String serviceName) {
        List<ServiceModel> data = new ArrayList<>();

        Map<String, RpcService> rpcServices = registryDataCache.fetchService();
        for (Map.Entry<String, RpcService> rpcServiceEntry : rpcServices.entrySet()) {
            final String currentServiceName = rpcServiceEntry.getKey();
            if (StringUtil.contains(currentServiceName, serviceName)) {
                ServiceModel model = fetchServiceModel(currentServiceName);
                data.add(model);
            }
        }

        return data;
    }

    /**
     * 模型转换
     *
     * @param serviceName
     * @return
     */
    private ServiceModel fetchServiceModel(String serviceName) {
        ServiceModel model = new ServiceModel();
        model.setServiceId(serviceName);
        String consumerNum = "0";
        String providerNum = "0";
        List<RpcConsumer> consumerSize = registryDataCache.fetchConsumersByService(serviceName);
        if (consumerSize != null) {
            consumerNum = String.valueOf(consumerSize.size());
        }
        model.setServiceConsumerAppNum(consumerNum);
        List<RpcProvider> providerSize = registryDataCache.fetchProvidersByService(serviceName);
        if (providerSize != null) {
            providerNum = String.valueOf(providerSize.size());
            Set<String> appSet = new HashSet<>();
            for (RpcProvider provider : providerSize) {
                appSet.add(provider.getAppName());
            }
            //接口本身没有app信息，所以从服务端取
            model
                .setServiceProviderAppName(StringUtils.joinWithComma(appSet.toArray(new String[0])));
        }
        model.setServiceProviderAppNum(providerNum);
        return model;
    }

    /**
     * 注册中心是 SOFARegistry 的情况下，服务信息的获取走 rest; 页面刷新时需刷新 DataCache
     */
    public void doRefreshCache() {
        if (SofaBootRpcConfigConstants.DEFAULT_REGISTRY.equals(environment
            .getProperty(SofaDashboardConstants.REGISTRY_TYPE))) {
            sofaRegistryRestClient.refreshAllSessionDataByDataInfoIds(SofaAdminRegistry
                .getCachedAllDataInfoIds());
        }
    }
}
