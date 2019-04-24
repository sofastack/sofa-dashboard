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
package com.alipay.sofa.dashboard.bridge;

import com.alipay.sofa.dashboard.sync.RegistryDataSync;
import com.alipay.sofa.dashboard.sync.ZookeeperRegistryDataSync;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bystander
 * @version $Id: CustomBeanProvider.java, v 0.1 2018年12月11日 18:13 bystander Exp $
 */
@Configuration
public class CustomBeanProvider {

    @Bean(name = "registrySyncMap")
    public Map<String, RegistryDataSync> configureRegistrySyncMap() {
        Map<String, RegistryDataSync> map = new HashMap<>(2);
        map.put(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_ZOOKEEPER, zookeeperRegistrySync());

        return map;
    }

    @Bean
    public RegistryDataSync zookeeperRegistrySync() {
        return new ZookeeperRegistryDataSync();
    }

}