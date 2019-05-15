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

import com.alipay.sofa.dashboard.model.monitor.DetailsItem;
import com.alipay.sofa.dashboard.model.monitor.EnvironmentInfo;
import com.alipay.sofa.dashboard.model.monitor.HealthInfo;
import com.alipay.sofa.dashboard.model.monitor.LoggersInfo;
import com.alipay.sofa.dashboard.model.monitor.MappingsInfo;
import com.alipay.sofa.dashboard.model.monitor.ThreadDumpInfo;
import com.alipay.sofa.dashboard.spi.MonitorManager;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 9:01 PM
 * @since:
 **/
@RestController
@RequestMapping("/api/actuator")
public class AppActuatorController {

    @Autowired
    private MonitorManager monitorManager;

    @RequestMapping("details")
    public Map baseDetails(@RequestParam("id") String targetHost) {
        String source = DashboardUtil.simpleDecode(targetHost);
        HealthInfo healthInfo = monitorManager.fetchHealth(source);
        Map map = monitorManager.fetchInfo(source);
        List<DetailsItem> threads = monitorManager.fetchDetailsThread(source);
        List<DetailsItem> maps = monitorManager.fetchHeapMemory(source);
        List<DetailsItem> nonHeap = monitorManager.fetchNonHeapMemory(source);
        Map<String, Object> data = new HashMap<>();
        data.put("health", healthInfo);
        data.put("info", map);
        data.put("threads", threads);
        data.put("heap", maps);
        data.put("nonheap", nonHeap);
        return data;
    }

    @RequestMapping("env")
    public Map env(@RequestParam("id") String targetHost) {
        String source = DashboardUtil.simpleDecode(targetHost);
        EnvironmentInfo environmentInfo = monitorManager.fetchEnvironment(source);
        Map<String, Object> data = new HashMap<>();
        data.put("env", environmentInfo);
        return data;
    }

    @RequestMapping("loggers")
    public Map loggers(@RequestParam("id") String targetHost) {
        String source = DashboardUtil.simpleDecode(targetHost);
        LoggersInfo loggersInfo = monitorManager.fetchLoggers(source);
        Map<String, Object> data = new HashMap<>();
        data.put("loggers", loggersInfo);
        return data;
    }

    @RequestMapping("mappings")
    public Map mappings(@RequestParam("id") String targetHost) {
        String source = DashboardUtil.simpleDecode(targetHost);
        Map<String, MappingsInfo> mappingsInfoMap = monitorManager.fetchMappings(source);
        Map<String, Object> data = new HashMap<>();
        data.put("mappings", mappingsInfoMap);
        return data;
    }

    @RequestMapping("thread-dump")
    public Map threadDump(@RequestParam("id") String targetHost) {
        String source = DashboardUtil.simpleDecode(targetHost);
        List<ThreadDumpInfo> threadDumps = monitorManager.fetchThreadDump(source);
        Map<String, Object> data = new HashMap<>();
        data.put("threaddump", threadDumps);
        return data;
    }
}
