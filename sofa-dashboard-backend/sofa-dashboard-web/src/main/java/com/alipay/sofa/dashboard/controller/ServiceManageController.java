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

import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.dashboard.model.ServiceAppModel;
import com.alipay.sofa.dashboard.model.ServiceConfigModel;
import com.alipay.sofa.dashboard.model.ServiceModel;
import com.alipay.sofa.dashboard.service.TelnetClient;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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
    private RegistryDataCache registryDataCache;
    public String dataId;

    @GetMapping("/all-service")
    public List<ServiceModel> queryServiceListByService(@RequestParam("query") String query) {
        List<ServiceModel> data = new ArrayList<>();
        Map<String, RpcService> rpcServices = registryDataCache.fetchService();
        for (Map.Entry<String, RpcService> rpcServiceEntry : rpcServices.entrySet()) {
            final String serviceName = rpcServiceEntry.getKey();
            ServiceModel model = fetchServiceModel(serviceName);
            if (model != null && (serviceName.contains(query) || StringUtils.isBlank(query))) {
                data.add(model);
            }
        }
        return data;
    }

    /**
     * 获取服务列表-应用维度
     *
     * @return
     */
    @GetMapping("/all-app")
    public List<Map<String, String>> queryServiceListByApp(@RequestParam("query") String query) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, RpcService> rpcServices = registryDataCache.fetchService();
        for (Map.Entry<String, RpcService> rpcServiceEntry : rpcServices.entrySet()) {
            final String serviceName = rpcServiceEntry.getKey();
            List<RpcProvider> providers = registryDataCache.fetchProvidersByService(serviceName);
            List<RpcConsumer> consumers = registryDataCache.fetchConsumersByService(serviceName);
            if (providers != null && providers.size() > 0) {
                providers.forEach((provider) -> {
                    if (provider.getAppName() != null && !data.contains(provider.getAppName())) {
                        Map<String, String> item = new HashMap<>();
                        item.put("appName", provider.getAppName());
                        if (provider.getAppName().contains(query) || StringUtils.isBlank(query)) {
                            data.add(item);
                        }
                    }
                });
            }
            if (consumers != null && consumers.size() > 0) {
                consumers.forEach((consumer) -> {
                    if (consumer.getAppName() != null && !data.contains(consumer.getAppName())) {
                        Map<String, String> item = new HashMap<>();
                        item.put("appName", consumer.getAppName());
                        if (consumer.getAppName().contains(query) || StringUtils.isBlank(query)) {
                            data.add(item);
                        }
                    }
                });
            }
        }
        return data;
    }

    /**
     * 查询应用的服务提供和服务消费详情
     *
     * @param appName
     * @return
     */
    @GetMapping("service-app")
    public ServiceAppModel queryServiceByAppName(@RequestParam("appName") String appName) {
//        System.out.println(appName);
        List<String> providersData = new ArrayList<>();
        List<String> consumersData = new ArrayList<>();
        ServiceAppModel result = new ServiceAppModel();
        Map<String, RpcService> rpcServices = registryDataCache.fetchService();
        for (Map.Entry<String, RpcService> rpcServiceEntry : rpcServices.entrySet()) {
            final String serviceName = rpcServiceEntry.getKey();
            List<RpcProvider> providers = registryDataCache.fetchProvidersByService(serviceName);
            List<RpcConsumer> consumers = registryDataCache.fetchConsumersByService(serviceName);
            if (providers != null && providers.size() > 0) {
                providers.forEach((provider) -> {
                    if (provider.getAppName() != null && appName.equals(provider.getAppName())
                        && !providersData.contains(serviceName)) {
                        providersData.add(serviceName);
                    }
                });
            }
            if (consumers != null && consumers.size() > 0) {
                consumers.forEach((consumer) -> {
                    if (consumer.getAppName() != null && appName.contains(consumer.getAppName())) {
                        if (consumer.getAppName() != null && appName.equals(consumer.getAppName())
                            && !consumersData.contains(serviceName)) {
                            consumersData.add(serviceName);
                        }
                    }
                });
            }
        }
        result.setConsumers(consumersData);
        result.setProviders(providersData);
        return result;
    }

    private List<RpcProvider> fetchProviderData(String serviceName) {
        return registryDataCache.fetchProvidersByService(serviceName);
    }

    private List<RpcConsumer> fetchConsumerData(String serviceName) {
        return registryDataCache.fetchConsumersByService(serviceName);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @GetMapping("query/providers")
    public List<RpcProvider> queryServiceProviders(@RequestParam("dataid") String serviceName) {
        dataId = URLDecoder.decode(serviceName);
        return fetchProviderData(dataId);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @GetMapping("query/consumers")
    public List<RpcConsumer> queryServiceConsumers(@RequestParam("dataid") String serviceName) {
        dataId = URLDecoder.decode(serviceName);
        return fetchConsumerData(dataId);
    }

    /**
     * 获取某个服务的所有提供方
     *
     * @return
     */
    @GetMapping("query/services")
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
     * 查询配置信息
     *
     * @param address
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("query/config")
    public ServiceConfigModel queryConfig(@RequestParam("address") String address) throws UnsupportedEncodingException {
        String EMPTY_BUFFER = new String(new byte[0], 0, 0);
        ServiceConfigModel result = new ServiceConfigModel();
        List<String> providerConfig = new ArrayList<>();
        List<String> consumerConfig = new ArrayList<>();
        TelnetClient ws = new TelnetClient(address, 1234);
        String str1 = ws.sendCommand("service "+dataId);
        str1 = new String(str1.getBytes("ISO-8859-1"), "GBK");
        String str2 = ws.sendCommand("list");
        str2 = new String(str2.getBytes("ISO-8859-1"), "GBK");
        providerConfig.add(str2);

        String str3 = ws.sendCommand("reference "+dataId);
        str3 = new String(str3.getBytes("ISO-8859-1"), "GBK");
        String str4 = ws.sendCommand("list");
        str4 = new String(str4.getBytes("ISO-8859-1"), "GBK");
        consumerConfig.add(str4);

        result.setProviders(providerConfig);
        result.setConsumers(consumerConfig);
        ws.disconnect();
        return result;
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
        // 服务提供方和服务消费方都没有，则不展示
        if (model.getServiceConsumerAppNum().equals("0")
            && model.getServiceProviderAppNum().equals("0")) {
            return null;
        }
        return model;
    }
}
