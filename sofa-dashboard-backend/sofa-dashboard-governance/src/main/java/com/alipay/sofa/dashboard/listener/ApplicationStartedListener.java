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
package com.alipay.sofa.dashboard.listener;

import com.alipay.sofa.dashboard.sync.RegistryDataSyncManager;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.ZookeeperConfigurator;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

/**
 * @author bystander
 * @version $Id: ApplicationStartedListener.java, v 0.1 2018年12月11日 17:15 bystander Exp $
 */
public class ApplicationStartedListener implements ApplicationListener {
    private static final String     KEY              = "com.alipay.sofa.dashboard.registry";
    private static final String     ZOOKEEPER_PREFIX = "zookeeper://";
    private static final String     SOFA_PREFIX      = "sofa://";

    @Autowired
    private RegistryDataSyncManager registryDataSyncManager;

    @Autowired
    private Environment             environment;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            String address = environment.getProperty(KEY);
            if (StringUtils.isNotBlank(address)) {
                RegistryConfig registryConfig = new RegistryConfig();
                if (address.startsWith(ZOOKEEPER_PREFIX)) {
                    RegistryConfigureProcessor processor = new ZookeeperConfigurator();
                    registryConfig = processor.buildFromAddress(address);
                } else if (address.startsWith(SOFA_PREFIX)) {
                    registryConfig.setAddress(address.substring(SOFA_PREFIX.length()));
                    registryConfig.setProtocol(SofaBootRpcConfigConstants.DEFAULT_REGISTRY);
                }
                registryDataSyncManager.start(registryConfig);
            }
        }
    }
}