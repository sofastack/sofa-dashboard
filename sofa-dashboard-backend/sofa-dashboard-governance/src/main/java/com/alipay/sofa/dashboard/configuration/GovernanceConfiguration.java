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
package com.alipay.sofa.dashboard.configuration;

import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.cache.SofaRegistryDataCacheImpl;
import com.alipay.sofa.dashboard.cache.ZookeeperRegistryDataCacheImpl;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.listener.ApplicationStartedListener;
import com.alipay.sofa.dashboard.registry.SofaAdminRegistry;
import com.alipay.sofa.dashboard.registry.ZookeeperAdminRegistry;
import com.alipay.sofa.dashboard.sync.RegistryDataSync;
import com.alipay.sofa.dashboard.sync.SofaRegistryDataSync;
import com.alipay.sofa.dashboard.sync.ZookeeperRegistryDataSync;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/6 11:32 AM
 * @since:
 **/
@Configuration
public class GovernanceConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "registrySyncMap")
    public Map<String, RegistryDataSync> configureRegistrySyncMap() {
        Map<String, RegistryDataSync> map = new HashMap<>();
        map.put(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER, zookeeperRegistryDataSync());
        map.put(SofaBootRpcConfigConstants.DEFAULT_REGISTRY, sofaRegistryDataSync());
        return map;
    }

    @Bean
    public ApplicationStartedListener applicationStartedListener() {
        return new ApplicationStartedListener();
    }

    @Bean
    public SofaAdminRegistry sofaAdminRegistry() {
        return new SofaAdminRegistry();
    }

    @Bean
    public ZookeeperAdminRegistry zookeeperAdminRegistry() {
        return new ZookeeperAdminRegistry();
    }

    @Bean
    public ZookeeperRegistryDataSync zookeeperRegistryDataSync() {
        return new ZookeeperRegistryDataSync();
    }

    @Bean
    public SofaRegistryDataSync sofaRegistryDataSync() {
        return new SofaRegistryDataSync();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RegistryDataCache registryDataCache() {
        if (environment.getProperty(SofaDashboardConstants.KEY).contains(
            SofaDashboardConstants.SOFA_PREFIX)) {
            return new SofaRegistryDataCacheImpl();
        } else {
            return new ZookeeperRegistryDataCacheImpl();
        }
    }
}
