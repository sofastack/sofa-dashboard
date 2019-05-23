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
import com.alipay.sofa.dashboard.impl.ZkHelper;
import com.alipay.sofa.dashboard.model.Application;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/23 10:29 PM
 * @since:
 **/
public class ZkHelperTest extends AbstractTestBase {

    @Autowired
    private ZkHelper zkHelper;

    @Before
    public void before() throws Exception {
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();
    }

    @Test
    public void testZkHelper() {
        String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR
                      + "testApp" + SofaDashboardConstants.SEPARATOR + "127.0.0.1";
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(path);
            int count = zkHelper.getArkAppCount("testApp");
            Assert.assertTrue(count == 1);

            List<Application> apps = zkHelper.getArkAppFromZookeeper("testApp", "testPlugin",
                "1.0.0");
            Assert.assertTrue(apps.size() == 1);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
