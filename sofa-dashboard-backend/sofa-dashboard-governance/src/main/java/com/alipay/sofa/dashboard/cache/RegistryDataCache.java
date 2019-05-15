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

import java.util.List;
import java.util.Map;

/**
 * @author bystander
 * @version $Id: RegistryDataCache.java, v 0.1 2018年12月10日 23:57 bystander Exp $
 */
public interface RegistryDataCache {

    /**
     * 添加 providers
     *
     * @param serviceName  服务名
     * @param providerList provider 列表
     */
    void addProviders(String serviceName, List<RpcProvider> providerList);

    /**
     * 添加 consumers
     *
     * @param serviceName 服务名
     * @param consumers   consumer 列表
     */
    void addConsumers(String serviceName, List<RpcConsumer> consumers);

    /**
     * 移除 providers
     *
     * @param serviceName  服务名
     * @param providerList provider 列表
     */
    void removeProviders(String serviceName, List<RpcProvider> providerList);

    /**
     * 移除 consumers
     *
     * @param serviceName 服务名
     * @param consumers   consumer 列表
     */
    void removeConsumers(String serviceName, List<RpcConsumer> consumers);

    /**
     * 更新 providers
     *
     * @param serviceName  服务名
     * @param providerList provider 列表
     */
    void updateProviders(String serviceName, List<RpcProvider> providerList);

    /**
     * 更新 consumers
     *
     * @param serviceName 服务名
     * @param consumers   consumer 列表
     */
    void updateConsumers(String serviceName, List<RpcConsumer> consumers);

    /**
     * 添加 RpcService
     *
     * @param providerList provider 列表
     */
    void addService(List<RpcService> providerList);

    /**
     * 移除 RpcService
     *
     * @param consumers consumer 列表
     */
    void removeService(List<RpcService> consumers);

    /**
     * 更新 RpcService
     *
     * @param rpcService
     */
    void updateService(RpcService rpcService);

    /**
     * 获取 RpcService
     *
     * @return
     */
    Map<String, RpcService> fetchService();

    /**
     * 获取 Providers
     *
     * @param serviceName 服务名
     * @return
     */
    List<RpcProvider> fetchProvidersByService(String serviceName);

    /**
     * 获取 Consumers
     *
     * @param serviceName 服务名
     * @return
     */
    List<RpcConsumer> fetchConsumersByService(String serviceName);

}