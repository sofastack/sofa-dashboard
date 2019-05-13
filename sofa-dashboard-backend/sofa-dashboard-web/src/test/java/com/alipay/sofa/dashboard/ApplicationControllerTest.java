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

import com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager;
import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.model.Application;
import com.alipay.sofa.dashboard.utils.ObjectBytesUtil;
import com.alipay.sofa.dashboard.vo.ApplicationVO;
import com.alipay.sofa.dashboard.zookeeper.ZkCommandClient;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/10 3:38 PM
 * @since:
 **/
public class ApplicationControllerTest extends AbstractTestBase {

    @Autowired
    ZkCommandClient                     zkCommandClient;

    @Autowired
    private ZookeeperApplicationManager zookeeperApplicationManager;

    @Before
    public void before() throws Exception {
        zookeeperApplicationManager.getApplications().clear();
        restTemplate = new RestTemplate();
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();
        initAppData();
        // 反射调用，强制获取一次数据
        Class classObj = Class
            .forName("com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager");
        ZookeeperApplicationManager zookeeperApplicationManager = (ZookeeperApplicationManager) classObj
            .newInstance();
        Method method = classObj.getDeclaredMethod("initApplications");
        Field field = classObj.getDeclaredField("zkCommandClient");
        field.setAccessible(true);
        method.setAccessible(true);
        field.set(zookeeperApplicationManager, zkCommandClient);
        method.invoke(zookeeperApplicationManager);

    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void testGetList() {
        String request = "http://localhost:" + definedPort + "/api/application/list";
        ApplicationVO list = restTemplate.getForObject(request, ApplicationVO.class);
        Assert.assertTrue(list != null && list.getData().get(0).getName().equals("test"));
    }

    @Test
    public void testRemove() {
        String request = "http://localhost:" + definedPort + "/api/application/remove?name={1}";
        boolean result = restTemplate.getForObject(request, Boolean.class, "test");
        Assert.assertTrue(result);
    }

    private void initAppData() throws Exception {
        Application application = new Application();
        application.setAppName("test");
        application.setHostName("192.168.0.1");
        application.setPort(8080);
        application.setAppState("UP");

        createNode("/apps", null, CreateMode.PERSISTENT);
        createNode("/apps/instance/test/192.168.0.1:8080",
            ObjectBytesUtil.convertFromObject(application), CreateMode.EPHEMERAL);
    }
}
