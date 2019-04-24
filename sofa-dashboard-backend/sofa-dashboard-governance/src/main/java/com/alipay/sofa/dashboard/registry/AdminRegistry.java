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
package com.alipay.sofa.dashboard.registry;

import com.alipay.sofa.dashboard.listener.RegistryDataChangeListener;
import com.alipay.sofa.rpc.config.RegistryConfig;

/**
 * @author bystander
 * @version $Id: AdminRegistry.java, v 0.1 2018年12月11日 17:32 bystander Exp $
 */
public interface AdminRegistry {

    /**
     * start
     *
     * @param registryConfig
     * @return
     */
    boolean start(RegistryConfig registryConfig);

    /**
     * for zk it is path
     *
     * @param group
     * @param listener
     * @return
     */
    void subscribe(String group, RegistryDataChangeListener listener);
}