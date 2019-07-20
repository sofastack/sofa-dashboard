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
import com.alipay.sofa.dashboard.controller.ArkMngController;
import com.alipay.sofa.dashboard.model.ArkPluginModel;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/4/10 4:03 PM
 * @since:
 **/
public class ArkManagementTest extends AbstractTestBase {

    @Autowired
    ArkMngController arkMngController;

    @Before
    public void before() throws Exception {
        restTemplate = new RestTemplate();
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void testArkDb() {
        // 注册插件
        Map<String, String> params = new HashMap<>();
        params.put(SofaDashboardConstants.PLUGIN_NAME, "test_plugin");
        params.put(SofaDashboardConstants.DESCRIPTION, "description");
        String register = "http://localhost:" + definedPort + "/api/ark/register";
        ResponseEntity<Boolean> result1 = restTemplate.postForEntity(register, params,
            Boolean.class);
        Assert.assertTrue(result1.getBody());

        List<ArkPluginModel> result = arkMngController.searchPlugins("test_plugin");
        Assert.assertTrue(result.size() == 1);

        //增加版本
        Map<String, Object> versionParam = new HashMap<>();
        versionParam.put(SofaDashboardConstants.ID, result.get(0).getId());
        versionParam.put(SofaDashboardConstants.ADDRESS, "file://sss/ss/s");
        versionParam.put(SofaDashboardConstants.VERSION, "2.0");
        params.put(SofaDashboardConstants.ADDRESS, "http://localhost/xxx/yyy");
        String registerNewVersion = "http://localhost:" + definedPort
                                    + "/api/ark/register-new-version";

        ResponseEntity<Boolean> result2 = restTemplate.postForEntity(registerNewVersion,
            versionParam, Boolean.class);
        Assert.assertTrue(result2.getBody());

        // 查询插件
        String findReq = "http://localhost:" + definedPort
                         + "/api/ark/search-plugin?pluginName={1}";
        ResponseEntity<List> result3 = restTemplate
            .getForEntity(findReq, List.class, "test_plugin");
        Assert.assertEquals(1, result3.getBody().size());
    }
}
