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
import com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/5 10:26 AM
 * @since:
 **/
public class SofaAdminRegistry implements AdminRegistry {

    private static final Logger                LOGGER       = LoggerFactory
                                                                .getLogger(SofaAdminRegistry.class);

    private static ScheduledThreadPoolExecutor executor     = new ScheduledThreadPoolExecutor(1);

    private static AtomicInteger               checkSumCode = new AtomicInteger(0);

    @Autowired
    private SofaRegistryRestClient             restTemplateClient;

    @Override
    public boolean start(RegistryConfig registryConfig) {
        try {
            restTemplateClient.init(registryConfig);
            CheckSumTask checkSumTask = new CheckSumTask();
            // 60s
            executor.scheduleWithFixedDelay(checkSumTask, 0, 60, TimeUnit.SECONDS);
        } catch (Throwable t) {
            LOGGER.error("Failed to start sofa registry.", t);
        }
        return true;
    }

    @Override
    public void subscribe(String group, RegistryDataChangeListener listener) {
        // do nothing
    }

    private class CheckSumTask implements Runnable {
        @Override
        public void run() {
            try {
                Integer newCheckVal = restTemplateClient.checkSum();
                if (checkSumCode.get() == newCheckVal) {
                    return;
                }
                restTemplateClient.syncAllSessionData();
                // update checkSumCode
                checkSumCode.compareAndSet(checkSumCode.get(), newCheckVal);
            } catch (Throwable t) {
                // catch the exception ,avoid scheduler task interrupt
                LOGGER.error("Filed to execute CheckSumTask.", t);
            }
        }
    }

}
