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

import com.alipay.sofa.dashboard.model.AppStatistic;
import com.alipay.sofa.dashboard.spi.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guolei.sgl (guolei.sgl@antfin.com) 18/12/7 下午5:15
 */
@RestController
@RequestMapping("/api/application")
@Api(value = "应用资源", tags = "application")
public class ApplicationController {

    @Autowired
    private AppService appService;

    @GetMapping("/list")
    @ApiOperation(value = "获取所有应用统计列表", notes = "指定一个关键字，返回所有包含关键字的应用，每一个应用具有应用名作为唯一标识")
    public List<AppStatistic> getApplications(@RequestParam(value = "applicationName", required = false) String applicationName) {
        return appService.getStatisticsByKeyword(applicationName);
    }
}
