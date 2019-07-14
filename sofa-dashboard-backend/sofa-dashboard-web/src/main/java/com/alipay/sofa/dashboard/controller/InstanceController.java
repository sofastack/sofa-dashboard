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

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/14 11:07 AM
 * @since:
 **/
import com.alipay.sofa.dashboard.application.ApplicationService;
import com.alipay.sofa.dashboard.model.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 应用实例控制器
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/11 2:51 PM
 * @since:
 **/
@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    @Autowired
    private ApplicationService applicationService;

    /**
     * 获取指定应用的实例列表
     * @param applicationName
     * @return
     */
    @RequestMapping("/list")
    public List<AppInfo> instances(@RequestParam("applicationName") String applicationName) {
        if (StringUtils.isEmpty(applicationName)) {
            return new ArrayList<>();
        }
        return applicationService.fetchAllInstanceByName(applicationName);
    }
}
