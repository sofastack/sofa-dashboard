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

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.listener.RegistryDataChangeListener;
import com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient;
import com.alipay.sofa.registry.client.api.RegistryClientConfig;
import com.alipay.sofa.registry.client.provider.DefaultRegistryClient;
import com.alipay.sofa.registry.client.provider.DefaultRegistryClientConfigBuilder;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/5 10:26 AM
 * @since:
 **/
public class SofaAdminRegistry implements AdminRegistry {

    private static final Logger                LOGGER                   = LoggerFactory
                                                                            .getLogger(SofaAdminRegistry.class);

    private static ScheduledThreadPoolExecutor executor                 = new ScheduledThreadPoolExecutor(
                                                                            1);

    private DefaultRegistryClient              registryClient;

    public static List<String>                 dataInfoIds              = new CopyOnWriteArrayList<>();

    private static AtomicInteger               checkSumCode             = new AtomicInteger(0);

    private static final String                REGISTRY_QUERY_CHECK_SUM = "/checkSumDataInfoIdList";

    private static String                      sessionAddress;

    private static int                         port;

    @Autowired
    private RestTemplate                       restTemplate;

    @Autowired
    private SofaRegistryRestClient             restTemplateClient;

    @Override
    public boolean start(RegistryConfig registryConfig) {
        try {
            String endPointAddress = registryConfig.getAddress();
            if (!endPointAddress.contains(SofaDashboardConstants.COLON)) {
                throw new RuntimeException(
                    "Please check your session address.Illegal session address is ["
                            + endPointAddress + "]");
            }
            sessionAddress = endPointAddress.split(SofaDashboardConstants.COLON)[0];
            port = Integer.valueOf(endPointAddress.split(SofaDashboardConstants.COLON)[1]);
            RegistryClientConfig config = DefaultRegistryClientConfigBuilder.start()
                .setRegistryEndpoint(sessionAddress).setRegistryEndpointPort(port).build();
            registryClient = new DefaultRegistryClient(config);
            registryClient.init();
            // 开始时初始化一次 dataInfoIds 列表
            dataInfoIds = restTemplateClient.syncAllDataInfoIds(sessionAddress, port);
            // 避免初始化之后重新拉取一次
            checkSumCode.compareAndSet(0, checkSum());
            CheckSumTask checkSumTask = new CheckSumTask();
            // 30s
            executor.scheduleWithFixedDelay(checkSumTask, 30, 30, TimeUnit.SECONDS);
        } catch (Throwable t) {
            LOGGER.error("Failed to start sofa registry.", t);
        }
        return true;
    }

    @Override
    public void subscribe(String group, RegistryDataChangeListener listener) {
        // do nothing
    }

    private Integer checkSum() {
        String pubUrl = SofaRegistryRestClient.buildRequestUrl(sessionAddress, port,
            REGISTRY_QUERY_CHECK_SUM);
        ResponseEntity<Integer> checkSumResp = restTemplate.getForEntity(pubUrl, Integer.class);
        return checkSumResp.getBody();
    }

    private class CheckSumTask implements Runnable {
        @Override
        public void run() {
            Integer newCheckVal = checkSum();
            if (checkSumCode.get() == newCheckVal) {
                return;
            }
            restTemplateClient.syncAllDataInfoIds(sessionAddress, port);
            // update checkSumCode
            checkSumCode.compareAndSet(checkSumCode.get(), newCheckVal);
        }
    }
}
