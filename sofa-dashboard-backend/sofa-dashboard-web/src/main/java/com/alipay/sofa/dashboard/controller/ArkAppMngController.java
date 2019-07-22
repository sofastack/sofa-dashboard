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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.impl.ZkHelper;
import com.alipay.sofa.dashboard.model.*;
import com.alipay.sofa.dashboard.service.ArkMngService;
import com.alipay.sofa.dashboard.spi.CommandPushManager;
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
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/21 下午2:26
 * @since:
 **/
@RestController
@RequestMapping("/api/arkapp")
public class ArkAppMngController {

    @Autowired
    private ArkMngService      arkMngService;

    @Autowired
    private CommandPushManager commandPushManager;

    @Autowired
    private ZkHelper           zkHelper;

    @RequestMapping("/app-list")
    public AppModuleModel fetchArlApps(@RequestParam("pluginName") String pluginName,
                                       @RequestParam("version") String version) throws Exception {

        AppModuleModel appModuleModel = new AppModuleModel();
        List<String> versions = new ArrayList<>();
        // 如果插件名为空，则返回空
        if (StringUtils.isEmpty(pluginName)) {
            return appModuleModel;
        }

        List<ArkPluginModel> arkPluginModels = arkMngService.fetchPluginsByName(pluginName);
        arkPluginModels.forEach((item) ->
                item.getVersions().forEach((versionItem -> {
                    versions.add(versionItem.getVersion());
                }))
        );

        // 版本为空，则插件当前插件的所有版本，然后选择一个默认的
        if (StringUtils.isEmpty(version) && !versions.isEmpty()) {
            version = versions.get(0);
        }
        appModuleModel.setDefaultVersion(version);
        appModuleModel.setVersionList(versions);
        List<ArkAppModel> list = new ArrayList<>();

        // 从数据库中拿到所有绑定的应用名
        List<String> appNames = arkMngService.queryAppsByPlugin(pluginName);

        for (String appName : appNames) {
            ArkAppModel arkAppModel = new ArkAppModel();
            arkAppModel.setAppName(appName);
            arkAppModel.setPluginName(pluginName);
            arkAppModel.setPluginVersion(version);
            // 这里需要去匹配当前注册到zk上应用名为appName的所有实例信息
            List<Application> applications = zkHelper.getArkAppFromZookeeper(appName, pluginName, version);
            arkAppModel.setIpUnitList(getAppUnitModel(applications));
            list.add(arkAppModel);
        }
        appModuleModel.setAppList(list);
        return appModuleModel;
    }

    @RequestMapping("/command")
    public void command(@RequestBody Map<String, Object> commandMap) {
        // parse commandMap
        CommandRequest commandRequest = parseCommandRequest(commandMap);
        commandPushManager.pushCommand(commandRequest);
    }

    private CommandRequest parseCommandRequest(Map<String, Object> commandMap) {
        CommandRequest request = new CommandRequest();
        request.setCommand(commandMap.get(SofaDashboardConstants.COMMAND).toString());
        request.setAppName(commandMap.get(SofaDashboardConstants.APP_NAME).toString());
        request.setPluginName(commandMap.get(SofaDashboardConstants.PLUGIN_NAME).toString());
        request.setPluginVersion(commandMap.get(SofaDashboardConstants.PLUGIN_VERSION).toString());
        if (commandMap.get(SofaDashboardConstants.TARGET_HOSTS) instanceof List) {
            request.setTargetHost((List<String>) commandMap
                .get(SofaDashboardConstants.TARGET_HOSTS));
        }
        request.setDimension(commandMap.get(SofaDashboardConstants.DIMENSION).toString());
        return request;
    }

    private List<AppUnitModel> getAppUnitModel(List<Application> applications) {
        List<AppUnitModel> list = new ArrayList<>();
        if (applications == null || applications.isEmpty()) {
            return list;
        }
        applications.forEach((item) -> {
            AppUnitModel appUnitModel = new AppUnitModel();
            appUnitModel.setIp(item.getHostName());
            appUnitModel.setStatue(item.getAppState());
            list.add(appUnitModel);
        });
        return list;
    }
}
