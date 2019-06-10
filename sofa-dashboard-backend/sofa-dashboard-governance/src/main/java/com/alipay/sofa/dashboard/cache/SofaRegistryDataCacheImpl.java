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
package com.alipay.sofa.dashboard.cache;

import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.rpc.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/23 10:52 AM
 * @since:
 **/
public class SofaRegistryDataCacheImpl implements RegistryDataCache {

    private Map<String, RpcService>        serviceMap  = new ConcurrentHashMap<>();
    private Map<String, List<RpcConsumer>> consumerMap = new ConcurrentHashMap<>();
    private Map<String, List<RpcProvider>> providerMap = new ConcurrentHashMap<>();

    @Override
    public Map<String, RpcService> fetchService() {
        return serviceMap;
    }

    @Override
    public List<RpcProvider> fetchProvidersByService(String serviceName) {
        List<RpcProvider> result = null;
        if (StringUtils.isNotBlank(serviceName)) {
            result = providerMap.get(serviceName);
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<RpcConsumer> fetchConsumersByService(String serviceName) {
        List<RpcConsumer> result = null;
        if (StringUtils.isNotBlank(serviceName)) {
            result = consumerMap.get(serviceName);
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    @Override
    public void addProviders(String serviceName, List<RpcProvider> providerList) {
        if (StringUtils.isNotBlank(serviceName) && providerList != null) {
            providerMap.put(serviceName, providerList);
        }
    }

    @Override
    public void addConsumers(String serviceName, List<RpcConsumer> consumerList) {
        if (StringUtils.isNotBlank(serviceName) && consumerList != null) {
            consumerMap.put(serviceName, consumerList);
        }
    }

    @Override
    public void addService(List<RpcService> rpcServices) {
        for (RpcService rpcService : rpcServices) {
            serviceMap.put(rpcService.getServiceName(), rpcService);
        }
    }
}
