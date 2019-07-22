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
package com.alipay.sofa.dashboard.controller;

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.impl.ZkHelper;
import com.alipay.sofa.dashboard.model.AppArkModel;
import com.alipay.sofa.dashboard.model.ArkPluginDO;
import com.alipay.sofa.dashboard.model.ArkPluginModel;
import com.alipay.sofa.dashboard.service.ArkMngService;
import com.alipay.sofa.dashboard.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/19 上午11:06
 * @since:
 **/
@RestController
@RequestMapping("/api/ark")
public class ArkMngController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArkMngController.class);

    @Autowired
    private ArkMngService       arkMngService;

    @Autowired
    private ZkHelper            zkHelper;

    /**
     * 获取 ark plugin 数据
     *
     * @return
     */
    @RequestMapping("/plugin-list")
    public List<ArkPluginModel> queryArkPluginList() {
        List<ArkPluginModel> list = arkMngService.fetchRegisteredPlugins();
        list.forEach((item) -> {
            List<String> appNames = arkMngService.queryAppsByPlugin(item.getPluginName());
            List<AppArkModel> appArkList = new ArrayList<>();
            appNames.forEach((appName) -> {
                try {
                    AppArkModel appArkModel = new AppArkModel();
                    appArkModel.setAppName(appName);
                    appArkModel.setInstanceNum(zkHelper.getArkAppCount(appName));
                    appArkList.add(appArkModel);
                } catch (Exception e) {
                    LOGGER.error("Failed to calculate ark app count.", e);
                }
            });
            item.setAppArkList(appArkList);
        });
        return list;
    }

    /**
     * register module
     *
     * @param map
     * @return
     */
    @RequestMapping("/register")
    public boolean registerPlugin(@RequestBody Map<String, String> map) {
        if (map == null) {
            return false;
        }
        String pluginName = map.get(SofaDashboardConstants.PLUGIN_NAME);
        String description = map.get(SofaDashboardConstants.DESCRIPTION);
        ArkPluginDO arkPluginDO = new ArkPluginDO(pluginName, DateUtil.now(), description);
        return arkMngService.registerPlugin(arkPluginDO);
    }

    @RequestMapping("/registerNewVersion")
    public boolean registerNewVersion(@RequestBody Map<String, String> map) {
        if (map == null) {
            return false;
        }
        String version = map.get(SofaDashboardConstants.VERSION);
        String pluginName = map.get(SofaDashboardConstants.PLUGIN_NAME);
        String address = map.get(SofaDashboardConstants.ADDRESS);
        return arkMngService.addNewVersion(pluginName, version, address);
    }

    @RequestMapping("/deletePluginModel")
    public boolean deletePluginModel(@RequestBody Map<String, String> map) {
        if (map == null) {
            return false;
        }
        String pluginName = map.get(SofaDashboardConstants.PLUGIN_NAME);
        return arkMngService.removePlugins(pluginName);
    }

    @RequestMapping("/search-plugin")
    public List<ArkPluginModel> searchPlugins(@RequestParam("pluginName") String pluginName) {
        if (StringUtils.isEmpty(pluginName)) {
            // 返回全部
            return arkMngService.fetchRegisteredPlugins();
        }
        return arkMngService.fetchPluginsByName(pluginName);
    }

    @RequestMapping("/related-app")
    public boolean relatedApp(@RequestParam("pluginName") String pluginName,
                              @RequestParam("appName") String appName) {
        return arkMngService.relatedAppToPlugin(pluginName, appName) > 0;
    }

    @RequestMapping("/cancel-related-app")
    public boolean cancelRelatedApp(@RequestParam("pluginName") String pluginName,
                                    @RequestParam("appName") String appName) {
        return arkMngService.cancelRelatedAppToPlugin(pluginName, appName) > 0;
    }
}
