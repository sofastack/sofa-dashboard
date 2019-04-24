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

import com.alipay.sofa.rpc.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bystander
 * @version $Id: RegistryDataSync.java, v 0.1 2018年12月10日 23:39 bystander Exp $
 */

@Service
public class RegistryDataSyncManagerImpl implements RegistryDataSyncManager {

    private final static Logger           LOGGER      = LoggerFactory
                                                          .getLogger(RegistryDataSyncManagerImpl.class);

    @Resource(name = "registrySyncMap")
    private Map<String, RegistryDataSync> syncManager = new HashMap<>();

    @Override
    public boolean start(RegistryConfig registryConfig) {

        RegistryDataSync registryDataSync = syncManager.get(registryConfig.getProtocol());

        if (registryDataSync != null) {
            registryDataSync.start(registryConfig);
            return true;
        } else {
            LOGGER.error("registryConfig is {}, not exist in our support list", registryConfig);
            return false;
        }
    }
}