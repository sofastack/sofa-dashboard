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

import com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager;
import com.alipay.sofa.dashboard.model.AppModel;
import com.alipay.sofa.dashboard.model.Application;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import com.alipay.sofa.dashboard.vo.ApplicationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/7 下午5:15
 * @since:
 **/
@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    @Autowired
    private ZookeeperApplicationManager zookeeperApplicationManager;

    /**
     * 获取应用列表
     *
     * @return
     */
    @GetMapping("/list")
    public ApplicationVO getList() {
        ApplicationVO applicationVO = new ApplicationVO();
        Map<String, Set<Application>> applications = zookeeperApplicationManager.getApplications();
        Collection<Set<Application>> apps = applications.values();
        int total = 0;
        int success = 0;
        int fail = 0;
        List<AppModel> data = new ArrayList<>();
        if (!apps.isEmpty()) {
            for (Set<Application> appList : apps) {
                for (Application app : appList) {
                    AppModel appModel = new AppModel();
                    appModel.setId(DashboardUtil.simpleEncode(app.getHostName(), app.getPort()));
                    appModel.setHost(app.getHostName());
                    appModel.setPort(app.getPort());
                    appModel.setName(app.getAppName());
                    appModel.setState(app.getAppState());
                    if ("UP".equals(app.getAppState())) {
                        success += 1;
                    } else {
                        fail += 1;
                    }
                    data.add(appModel);
                }
                total += appList.size();
            }
        }
        applicationVO.setData(data);
        applicationVO.setTotalCount(total);
        applicationVO.setSuccessCount(success);
        applicationVO.setFailCount(fail);
        return applicationVO;
    }

    /**
     * 移除应用
     *
     * @param name
     * @return
     */
    @RequestMapping("/remove")
    public Boolean remove(@RequestParam("name") String name) {
        List<AppModel> applications = zookeeperApplicationManager.applications();
        for (AppModel app : applications) {
            if (app.getName().equals(name)) {
                applications.remove(app);
                return true;
            }
        }
        return false;
    }
}
