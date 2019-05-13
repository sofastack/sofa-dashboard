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
import com.alipay.sofa.dashboard.model.CommandRequest;
import com.alipay.sofa.dashboard.spi.CommandPushManager;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/13 8:06 PM
 * @since:
 **/
public class ZkCommandPushManagerTest extends AbstractTestBase {

    @Autowired
    CommandPushManager zkCommandPushManager;

    @Before
    public void before() throws Exception {
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();

        CommandRequest request = new CommandRequest();
        request.setAppName("test-ark-command");
        request.setCommand("install");
        request.setDimension("ip");
        request.setPluginName("test-plugin");
        request.setPluginVersion("1.0.0");
        List<String> target = new ArrayList<>();
        target.add("127.0.0.1");
        request.setTargetHost(target);
        zkCommandPushManager.pushCommand(request);
        String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
                      + request.getAppName() + SofaDashboardConstants.SEPARATOR
                      + request.getTargetHost().get(0);
        byte[] bytes = client.getData().forPath(path);
        String installResult = new String(bytes);
        Assert.assertTrue(installResult.equals("test-plugin:1.0.0:activated?bizUrl=null"));

        request.setDimension("app");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName();
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert.assertTrue(installResult.equals("test-plugin:1.0.0:activated?bizUrl=null"));
    }

    @Test
    public void test() throws Exception {
        CommandRequest request = new CommandRequest();
        request.setAppName("test-ark-command");
        request.setCommand("install");
        request.setDimension("ip");
        request.setPluginName("test-plugin");
        request.setPluginVersion("2.0.0");
        List<String> target = new ArrayList<>();
        target.add("127.0.0.1");
        request.setTargetHost(target);
        zkCommandPushManager.pushCommand(request);
        String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
                      + request.getAppName() + SofaDashboardConstants.SEPARATOR
                      + request.getTargetHost().get(0);
        byte[] bytes = client.getData().forPath(path);
        String installResult = new String(bytes);
        Assert
            .assertTrue(installResult
                .equals("test-plugin:1.0.0:activated?bizUrl=null;test-plugin:2.0.0:deactivated?bizUrl=null"));

        request.setCommand("switch");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName() + SofaDashboardConstants.SEPARATOR
               + request.getTargetHost().get(0);
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert
            .assertTrue(installResult
                .equals("test-plugin:1.0.0:deactivated?bizUrl=null;test-plugin:2.0.0:activated?bizUrl=null"));

        request.setCommand("uninstall");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName() + SofaDashboardConstants.SEPARATOR
               + request.getTargetHost().get(0);
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert.assertTrue(installResult.equals("test-plugin:1.0.0:deactivated?bizUrl=null"));

        request.setDimension("app");
        request.setAppName("test-ark-command");
        request.setCommand("install");
        request.setPluginName("test-plugin");
        request.setPluginVersion("2.0.0");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName();
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert
            .assertTrue(installResult
                .equals("test-plugin:1.0.0:activated?bizUrl=null;test-plugin:2.0.0:deactivated?bizUrl=null"));

        request.setCommand("switch");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName();
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert
            .assertTrue(installResult
                .equals("test-plugin:1.0.0:deactivated?bizUrl=null;test-plugin:2.0.0:activated?bizUrl=null"));

        request.setCommand("uninstall");
        zkCommandPushManager.pushCommand(request);
        path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
               + request.getAppName() + SofaDashboardConstants.SEPARATOR
               + request.getTargetHost().get(0);
        bytes = client.getData().forPath(path);
        installResult = new String(bytes);
        Assert.assertTrue(installResult.equals("test-plugin:1.0.0:deactivated?bizUrl=null"));
    }
}
