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
import com.alipay.sofa.dashboard.client.model.env.PropertySourceDescriptor;
import com.alipay.sofa.dashboard.client.model.health.HealthDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.client.model.memory.MemoryDescriptor;
import com.alipay.sofa.dashboard.client.model.thread.ThreadSummaryDescriptor;
import com.alipay.sofa.dashboard.model.InstanceRecord;
import com.alipay.sofa.dashboard.model.RecordResponse;
import com.alipay.sofa.dashboard.model.StampedValueEntity;
import com.alipay.sofa.dashboard.spi.AppService;
import com.alipay.sofa.dashboard.spi.MonitorService;
import com.alipay.sofa.dashboard.utils.HostPortUtils;
import com.alipay.sofa.dashboard.utils.MapUtils;
import com.alipay.sofa.dashboard.utils.TreeNodeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用实例控制器
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/7/11 2:51 PM
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    @Autowired
    private AppService     applicationService;

    @Autowired
    private MonitorService service;

    @GetMapping
    public List<InstanceRecord> instances(
        @RequestParam(value = "applicationName", required = false) String applicationName) {
        // 这里默认情况如果没有传入 applicationName，前端应不展示任何数据
        if (StringUtils.isEmpty(applicationName)){
            return new ArrayList<>();
        }
        return applicationService.getInstancesByName(applicationName).stream()
            .map(InstanceRecord::new)
            .collect(Collectors.toList());
    }

    @GetMapping("/{instanceId}/env")
    public RecordResponse getEnv(
        @PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        EnvironmentDescriptor descriptor = service.fetchEnvironment(hostAndPort);

        //
        // 注意descriptor可能为空
        //
        Map<String, String> envMap = new HashMap<>();
        for (PropertySourceDescriptor propertySource :
            Optional.ofNullable(descriptor).orElse(new EnvironmentDescriptor())
                .getPropertySources()) {
            propertySource.getProperties().forEach((key, value) ->
                envMap.put(key, String.valueOf(value.getValue())));
        }

        //
        // 接口层重新拼装一次前端需要的数据结构概览
        //
        return RecordResponse.newBuilder()
            .overview("Name", envMap.get("spring.application.name"))
            .overview("Address",
                String.format("%s:%d", hostAndPort.getHost(), hostAndPort.getPort()))
            .overview("sofa-boot.version", envMap.get("sofa-boot.version"))
            .detail(TreeNodeConverter.convert(descriptor))
            .build();
    }

    @GetMapping("/{instanceId}/info")
    public RecordResponse getInfo(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        InfoDescriptor descriptor = service.fetchInfo(hostAndPort);

        //
        // 注意descriptor可能为空
        //
        Map<String, Object> infoMap = MapUtils.toFlatMap(
            Optional.ofNullable(descriptor).orElse(new InfoDescriptor()).getInfo())
            .entrySet().stream()
            .limit(3)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //
        // 接口层重新拼装一次前端需要的数据结构概览
        //
        return RecordResponse.newBuilder()
            .overview(infoMap)
            .detail(TreeNodeConverter.convert(descriptor))
            .build();
    }

    @GetMapping("/{instanceId}/health")
    public RecordResponse getHealth(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        HealthDescriptor descriptor = service.fetchHealth(hostAndPort);

        //
        // 注意descriptor可能为空
        //
        if (descriptor == null) {
            descriptor = new HealthDescriptor();
            descriptor.setStatus("UNKNOWN");
        }

        return RecordResponse
            .newBuilder()
            .overview("Health", descriptor.getStatus())
            .overview("Address",
                String.format("%s:%d", hostAndPort.getHost(), hostAndPort.getPort()))
            .detail(TreeNodeConverter.convert(descriptor)).build();
    }

    @GetMapping("/{instanceId}/loggers")
    public RecordResponse getLoggers(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        LoggersDescriptor descriptor = service.fetchLoggers(hostAndPort);

        return RecordResponse.newBuilder().overview(new HashMap<>(16))
            .detail(TreeNodeConverter.convert(descriptor)).build();
    }

    @GetMapping("/{instanceId}/mappings")
    public RecordResponse getMappings(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        MappingsDescriptor descriptor = service.fetchMappings(hostAndPort);

        int servletCount = descriptor == null ? 0 : descriptor.getMappings().values()
            .stream()
            .map(it -> it.getServlets().size())
            .reduce(Integer::sum)
            .orElse(0);
        int servletFilterCount = descriptor == null ? 0 : descriptor.getMappings().values()
            .stream()
            .map(it -> it.getServletFilters().size())
            .reduce(Integer::sum)
            .orElse(0);
        int dispatcherServletCount = descriptor == null ? 0 : descriptor.getMappings().values()
            .stream()
            .map(it -> it.getDispatcherServlet().size())
            .reduce(Integer::sum)
            .orElse(0);

        return RecordResponse.newBuilder()
            .overview("DispatcherServletCount", String.valueOf(dispatcherServletCount))
            .overview("ServletFilterCount", String.valueOf(servletFilterCount))
            .overview("ServletCount", String.valueOf(servletCount))
            .detail(TreeNodeConverter.convert(descriptor))
            .build();
    }

    @GetMapping("/{instanceId}/memory")
    public List<StampedValueEntity<MemoryDescriptor>> getMemoryRecords(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        return service.fetchMemoryInfo(hostAndPort);
    }

    @GetMapping("/{instanceId}/thread")
    public List<StampedValueEntity<ThreadSummaryDescriptor>> getThreadRecords(@PathVariable("instanceId") String instanceId) {
        HostAndPort hostAndPort = HostPortUtils.getById(instanceId);
        return service.fetchThreadInfo(hostAndPort);
    }
}
