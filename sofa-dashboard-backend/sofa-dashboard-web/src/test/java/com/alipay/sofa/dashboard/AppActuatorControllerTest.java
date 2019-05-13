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

import com.alipay.sofa.dashboard.app.actuator.ActuatorMonitorManager;
import com.alipay.sofa.dashboard.app.task.AppDynamicInfoTask;
import com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager;
import com.alipay.sofa.dashboard.controller.AppActuatorController;
import com.alipay.sofa.dashboard.model.Application;
import com.alipay.sofa.dashboard.model.monitor.DetailThreadInfo;
import com.alipay.sofa.dashboard.model.monitor.DetailsItem;
import com.alipay.sofa.dashboard.model.monitor.EnvironmentInfo;
import com.alipay.sofa.dashboard.model.monitor.LoggersInfo;
import com.alipay.sofa.dashboard.model.monitor.MappingsInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryNonHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.ThreadDumpInfo;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import com.alipay.sofa.dashboard.utils.FixedQueue;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/13 5:20 PM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaAdminServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppActuatorControllerTest {

    @LocalServerPort
    protected int                       definedPort;

    private static TestingServer        server;

    @Autowired
    AppActuatorController               appActuatorController;
    @Autowired
    ActuatorMonitorManager              actuatorMonitorManager;

    @Autowired
    AppDynamicInfoTask                  appDynamicInfoTask;

    @Autowired
    private ZookeeperApplicationManager zookeeperApplicationManager;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new TestingServer(2181, true);
        server.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        server.stop();
    }

    @Before
    public void before() {
        FixedQueue<MemoryHeapInfo> queue1 = new FixedQueue(4);
        FixedQueue<MemoryNonHeapInfo> queue2 = new FixedQueue(4);
        FixedQueue<DetailThreadInfo> queue3 = new FixedQueue(4);
        ActuatorMonitorManager.cacheHeapMemory.put(
            DashboardUtil.simpleEncode("127.0.0.1", definedPort), queue1);
        ActuatorMonitorManager.cacheNonHeapMemory.put(
            DashboardUtil.simpleEncode("127.0.0.1", definedPort), queue2);
        ActuatorMonitorManager.cacheDetailThreads.put(
            DashboardUtil.simpleEncode("127.0.0.1", definedPort), queue3);

        Application application = new Application();
        application.setHostName("localhost");
        application.setPort(definedPort);
        Set<Application> values = new HashSet<>();
        values.add(application);
        zookeeperApplicationManager.getApplications().put("test", values);
    }

    @Test
    public void testEnv() throws InterruptedException {
        String source = "127.0.0.1:" + definedPort;

        EnvironmentInfo environmentInfo = actuatorMonitorManager.fetchEnvironment(source);
        Assert.assertTrue(environmentInfo.getPropertySources().size() > 0);

        Map map = actuatorMonitorManager.fetchInfo(source);
        Assert.assertTrue(map.get("app.version").equals("1.0.0"));
        Assert.assertTrue(map.size() > 0);

        appDynamicInfoTask.fetchAppDynamicInfo();
        List<DetailsItem> heapMemory = actuatorMonitorManager.fetchHeapMemory(source);
        Assert.assertTrue(heapMemory.size() > 0 && heapMemory.size() < 4);
        List<DetailsItem> nonHeapMemory = actuatorMonitorManager.fetchNonHeapMemory(source);
        Assert.assertTrue(nonHeapMemory.size() > 0 && nonHeapMemory.size() < 4);
        List<DetailsItem> detailsThread = actuatorMonitorManager.fetchDetailsThread(source);
        Assert.assertTrue(detailsThread.size() > 0 && detailsThread.size() < 4);
        LoggersInfo loggersInfo = actuatorMonitorManager.fetchLoggers(source);
        Assert.assertTrue(loggersInfo.getLevels().size() > 0);
        Map<String, MappingsInfo> mappingsInfoMap = actuatorMonitorManager.fetchMappings(source);
        Assert.assertTrue(mappingsInfoMap.containsKey("test-backend"));
        List<ThreadDumpInfo> threadDumpInfos = actuatorMonitorManager.fetchThreadDump(source);
        Assert.assertTrue(threadDumpInfos.size() > 0);
    }
}
