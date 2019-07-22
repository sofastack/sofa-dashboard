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

import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.model.AppRecord;
import com.alipay.sofa.dashboard.spi.AppService;
import com.alipay.sofa.dashboard.spi.MonitorService;
import com.alipay.sofa.dashboard.utils.HostPortUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用实例控制器
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/7/11 2:51 PM
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping("/api/instance")
@Api(value = "应用实例资源", tags = "instance")
public class InstanceController {

    @Autowired
    private AppService     applicationService;

    @Autowired
    private MonitorService service;

    @GetMapping("/list")
    @ApiOperation(
        value = "获取指定应用的实例列表",
        notes = "指定一个应用名称，返回所有匹配的应用实例，每个实例由一个id作为唯一标识")
    public List<AppRecord> instances(
        @RequestParam(value = "applicationName", required = false) String applicationName) {
        return applicationService.getInstancesByName(applicationName).stream()
            .map(AppRecord::new)
            .collect(Collectors.toList());
    }

    @GetMapping("/{instanceId}/env")
    @ApiOperation("根据实例id获取实例运行时参数")
    public EnvironmentDescriptor getEnv(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.fromInstanceId(instanceId);
        return service.fetchEnvironment(hostAndPort);
    }

    @GetMapping("/{instanceId}/health")
    @ApiOperation("根据实例id获取实例运行健康状态")
    public InfoDescriptor getInfo(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.fromInstanceId(instanceId);
        return service.fetchInfo(hostAndPort);
    }

    @GetMapping("/{instanceId}/loggers")
    @ApiOperation("根据实例id获取实例Logger信息")
    public LoggersDescriptor getLoggers(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.fromInstanceId(instanceId);
        return service.fetchLoggers(hostAndPort);
    }

    @GetMapping("/{instanceId}/mappings")
    @ApiOperation("根据实例id获取实例Mapping信息")
    public MappingsDescriptor getMappings(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.fromInstanceId(instanceId);
        return service.fetchMappings(hostAndPort);
    }

}
