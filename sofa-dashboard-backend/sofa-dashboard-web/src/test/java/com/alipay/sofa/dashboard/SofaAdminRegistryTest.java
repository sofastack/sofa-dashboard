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
package com.alipay.sofa.dashboard;

import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient;
import com.alipay.sofa.dashboard.registry.SofaAdminRegistry;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/15 1:27 PM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaAdminServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test-sofa.properties")
public class SofaAdminRegistryTest {

    private static TestingServer server;

    @Autowired
    private SofaAdminRegistry    sofaAdminRegistry;

    @Autowired
    private RegistryDataCache    registryDataCache;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new TestingServer(2181, true);
        server.start();

    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        server.stop();
    }

    @Test
    public void testCacheData() {
        String serviceName = "test1";
        List<RpcConsumer> consumers = new ArrayList<>();
        RpcConsumer consumer = new RpcConsumer();
        consumer.setServiceName(serviceName);
        consumers.add(consumer);
        registryDataCache.addConsumers(serviceName, consumers);
        List<RpcConsumer> consumersResult = registryDataCache.fetchConsumersByService(serviceName);
        Assert.assertTrue(consumersResult.size() == 1);

        List<RpcProvider> providers = new ArrayList<>();
        RpcProvider provider = new RpcProvider();
        provider.setServiceName(serviceName);
        providers.add(provider);
        registryDataCache.addProviders(serviceName, providers);
        List<RpcProvider> providersResult = registryDataCache.fetchProvidersByService(serviceName);
        Assert.assertTrue(providersResult.size() == 1);

        String serviceName1 = "test2";
        registryDataCache.addConsumers(serviceName1, null);
        List<RpcConsumer> consumers1 = registryDataCache.fetchConsumersByService(serviceName1);
        Assert.assertTrue(consumers1.size() == 0);

        registryDataCache.addProviders(serviceName1, null);
        List<RpcProvider> providers1 = registryDataCache.fetchProvidersByService(serviceName1);
        Assert.assertTrue(providers1.size() == 0);
    }

    @Test
    public void testSofaAdminRegistry() {
        Assert.assertTrue(sofaAdminRegistry != null);
        Map<String, RpcService> serviceMap = registryDataCache.fetchService();
        Assert.assertTrue(serviceMap.size() == 0);
        List<RpcService> services = new ArrayList<>();
        RpcService rpcService = new RpcService();
        rpcService.setServiceName("test1");
        services.add(rpcService);
        registryDataCache.addService(services);
        Map<String, RpcService> serviceMap1 = registryDataCache.fetchService();
        Assert.assertTrue(serviceMap1.size() == 1);
    }

    @Test
    public void testConvertRpcProviderFromMap() throws Exception {
        Class<?> classClient = Class
            .forName("com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient");
        SofaRegistryRestClient client = (SofaRegistryRestClient) classClient.newInstance();
        Method method = classClient.getDeclaredMethod("convertRpcProviderFromMap", Map.class);
        method.setAccessible(true);
        Map map = new HashMap();
        map.put(SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY, "127.0.0.1:9603");
        Object invoke = method.invoke(client, map);
        Assert.assertTrue((invoke instanceof RpcProvider));
        RpcProvider provider1 = (RpcProvider) invoke;
        Assert.assertTrue(provider1.getAddress().equals("127.0.0.1"));

        map.remove(SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY);
        Map sourceAddress = new HashMap();
        sourceAddress.put(SofaDashboardConstants.REGISTRY_IP_KEY, "127.0.0.1");
        sourceAddress.put(SofaDashboardConstants.PORT, 9603);
        map.put(SofaDashboardConstants.REGISTRY_SOURCE_ADDRESS_KEY, sourceAddress);
        invoke = method.invoke(client, map);
        Assert.assertTrue((invoke instanceof RpcProvider));
        RpcProvider provider2 = (RpcProvider) invoke;
        Assert.assertTrue(provider2.getAddress().equals("127.0.0.1"));

    }

    @Test
    public void testConvertRpcConsumerFromMap() throws Exception {
        Class<?> classClient = Class
            .forName("com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient");
        SofaRegistryRestClient client = (SofaRegistryRestClient) classClient.newInstance();
        Method method = classClient.getDeclaredMethod("convertRpcConsumerFromMap", Map.class);
        method.setAccessible(true);
        Map map = new HashMap();
        map.put(SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY, "127.0.0.1:9603");
        Object invoke = method.invoke(client, map);
        Assert.assertTrue((invoke instanceof RpcConsumer));
        RpcConsumer consumer1 = (RpcConsumer) invoke;
        Assert.assertTrue(consumer1.getAddress().equals("127.0.0.1"));

        map.remove(SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY);
        Map sourceAddress = new HashMap();
        sourceAddress.put(SofaDashboardConstants.REGISTRY_IP_KEY, "127.0.0.1");
        sourceAddress.put(SofaDashboardConstants.PORT, 9603);
        map.put(SofaDashboardConstants.REGISTRY_SOURCE_ADDRESS_KEY, sourceAddress);
        invoke = method.invoke(client, map);
        Assert.assertTrue((invoke instanceof RpcConsumer));
        RpcConsumer consumer2 = (RpcConsumer) invoke;
        Assert.assertTrue(consumer2.getAddress().equals("127.0.0.1"));

    }
}
