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
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/15 1:27 PM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaAdminServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test-sofa.properties")
public class SofaAdminRegistryTest {

    private static TestingServer   server;

    @Autowired
    private SofaAdminRegistry      sofaAdminRegistry;

    @Autowired
    private RegistryDataCache      registryDataCache;

    @Autowired
    private SofaRegistryRestClient restTemplateClient;

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
    public void testSofaAdminRegistry() {
        Assert.assertTrue(sofaAdminRegistry != null);
        Map<String, RpcService> serviceMap = registryDataCache.fetchService();
        Assert.assertTrue(serviceMap.size() == 0);
    }
}
