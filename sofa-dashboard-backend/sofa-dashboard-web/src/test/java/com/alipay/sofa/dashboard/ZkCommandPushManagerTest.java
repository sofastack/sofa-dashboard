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

import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.impl.ZkCommandPushManager;
import com.alipay.sofa.dashboard.model.CommandRequest;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/22 3:02 PM
 * @since:
 **/
public class ZkCommandPushManagerTest extends AbstractTestBase {

    @Autowired
    private ZkCommandPushManager zkCommandPushManager;

    @Before
    public void before() throws Exception {
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();
        client
            .create()
            .creatingParentContainersIfNeeded()
            .withMode(CreateMode.PERSISTENT)
            .forPath(
                SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR + "testApp",
                "".getBytes());
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void testPushCommand() throws Exception {

        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand("install");
        commandRequest.setPluginVersion("1.0.0");
        commandRequest.setDimension("app");

        commandRequest.setPluginName("testPlugin");
        commandRequest.setAppName("testApp");
        zkCommandPushManager.pushCommand(commandRequest);
        String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
                      + commandRequest.getAppName();
        byte[] bytes = client.getData().forPath(path);
        String data = new String(bytes);
        Assert.assertTrue(data.contains("activated"));

        // ip
        commandRequest.setDimension("ip");
        ArrayList<String> list = new ArrayList();
        list.add("127.0.0.1");
        commandRequest.setTargetHost(list);

        zkCommandPushManager.pushCommand(commandRequest);
        String ipPath = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
                        + commandRequest.getAppName() + SofaDashboardConstants.SEPARATOR
                        + "127.0.0.1";
        byte[] ipBytes = client.getData().forPath(ipPath);
        String ipData = new String(ipBytes);
        Assert.assertTrue(ipData.contains("activated"));
    }
}
