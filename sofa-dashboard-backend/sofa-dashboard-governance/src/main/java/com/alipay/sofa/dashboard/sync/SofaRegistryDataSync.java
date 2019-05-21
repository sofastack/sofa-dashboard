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
package com.alipay.sofa.dashboard.sync;

import com.alipay.sofa.dashboard.registry.SofaAdminRegistry;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/6 11:13 AM
 * @since:
 **/
public class SofaRegistryDataSync implements RegistryDataSync {

    private final static Logger LOGGER = LoggerFactory.getLogger(SofaRegistryDataSync.class);

    @Autowired
    private SofaAdminRegistry   sofaAdminRegistry;

    @Override
    public boolean start(RegistryConfig registryConfig) {

        LOGGER.info("start sofa registry data sync,config is {}", registryConfig.getAddress());
        sofaAdminRegistry.start(registryConfig);
        return true;
    }
}
