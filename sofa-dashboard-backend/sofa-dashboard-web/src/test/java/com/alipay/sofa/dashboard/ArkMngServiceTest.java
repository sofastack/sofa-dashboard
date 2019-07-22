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
import com.alipay.sofa.dashboard.model.ArkPluginDO;
import com.alipay.sofa.dashboard.model.ArkPluginModel;
import com.alipay.sofa.dashboard.service.ArkMngService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/22 2:32 PM
 * @since:
 **/
public class ArkMngServiceTest extends AbstractTestBase {

    @Autowired
    private ArkMngService arkMngService;

    @Before
    public void before() {
        ArkPluginDO model = new ArkPluginDO();
        model.setCreateTime(new Date());
        model.setDescription("this is test");
        model.setPluginName("test-plugin");
        boolean result = arkMngService.registerPlugin(model);
        Assert.assertTrue(result);
    }

    @Test
    public void testUpdatePlugin() {
        ArkPluginDO arkPluginDO = new ArkPluginDO();
        arkPluginDO.setId(getPluginId("test-plugin"));
        arkPluginDO.setPluginName("test-plugin1");
        arkMngService.updatePlugin(arkPluginDO);
        List<ArkPluginModel> result = arkMngService.fetchPluginsByName("test-plugin1");
        Assert.assertTrue(result.size() == 1);

        // reback
        arkPluginDO.setPluginName("test-plugin");
        arkMngService.updatePlugin(arkPluginDO);
    }

    @Test
    public void testRelatedAppToPlugin() {
        int effectLine = arkMngService.relatedAppToPlugin(getPluginId("test-plugin"), "test-app");
        Assert.assertTrue(effectLine > 0);
        int cancelEffectLine = arkMngService.cancelRelatedAppToPlugin("test-plugin", "test-app");
        Assert.assertTrue(cancelEffectLine > 0);
    }

    @Test
    public void testAddNewVersion() {
        int mId = getPluginId("test-plugin");
        boolean isAdd = arkMngService.addNewVersion(mId, "1.0", "file:///a/b/c");
        Assert.assertTrue(isAdd);
        boolean isDelete = arkMngService.deleteVersion(mId, "1.0");
        Assert.assertTrue(isDelete);
    }

    private int getPluginId(String pluginName) {
        List<ArkPluginModel> arkPluginModels = arkMngService.fetchPluginsByName(pluginName);

        return arkPluginModels.get(0).getId();
    }

    @After
    public void after() {
        List<ArkPluginModel> arkPluginModels = arkMngService.fetchPluginsByName("test-plugin");
        arkMngService.removePlugins(arkPluginModels.get(0).getId());
    }
}
