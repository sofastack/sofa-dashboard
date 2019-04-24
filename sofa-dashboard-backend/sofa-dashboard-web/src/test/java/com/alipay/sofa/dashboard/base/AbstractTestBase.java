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
package com.alipay.sofa.dashboard.base;

import com.alipay.sofa.dashboard.SofaAdminServerApplication;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/10 1:55 PM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaAdminServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class AbstractTestBase {

    private static TestingServer server;

    protected CuratorFramework   client;

    protected RestTemplate       restTemplate;

    @LocalServerPort
    protected int                definedPort;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new TestingServer(2181, true);
        server.start();

    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        server.stop();
    }

    protected void createNode(String path, byte[] data, CreateMode mode) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            client.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data);
        }
    }

}
