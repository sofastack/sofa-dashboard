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
package com.alipay.sofa.dashboard.spi;

import com.alipay.sofa.dashboard.model.monitor.DetailsItem;
import com.alipay.sofa.dashboard.model.monitor.EnvironmentInfo;
import com.alipay.sofa.dashboard.model.monitor.HealthInfo;
import com.alipay.sofa.dashboard.model.monitor.LoggersInfo;
import com.alipay.sofa.dashboard.model.monitor.MappingsInfo;
import com.alipay.sofa.dashboard.model.monitor.MetricsInfo;
import com.alipay.sofa.dashboard.model.monitor.ThreadDumpInfo;

import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 9:19 PM
 * @since:
 **/
public interface MonitorManager {

    /**
     * 获取 Environment
     * @param source 源
     * @return
     */
    EnvironmentInfo fetchEnvironment(Object source);

    /**
     * 获取 Metrics
     * @param source 源
     * @return
     */
    List<MetricsInfo> fetchMetrics(Object source);

    /**
     * 获取 Health
     * @param source 源
     * @return
     */
    HealthInfo fetchHealth(Object source);

    /**
     * 获取 Info
     * @param source
     * @return
     */
    Map fetchInfo(Object source);

    /**
     * 获取 Thread
     * @param source
     * @return
     */
    List<DetailsItem> fetchDetailsThread(Object source);

    /**
     * 获取 Heap Memory
     * @param source
     * @return
     */
    List<DetailsItem> fetchHeapMemory(Object source);

    /**
     * 获取 Non-Heap Memory
     * @param source
     * @return
     */
    List<DetailsItem> fetchNonHeapMemory(Object source);

    /**
     * 获取 Loggers
     * @param source
     * @return
     */
    LoggersInfo fetchLoggers(Object source);

    /**
     * 获取 Mappings
     * @param source
     * @return
     */
    Map<String, MappingsInfo> fetchMappings(Object source);

    /**
     * 获取 Threaddump
     * @param source
     * @return
     */
    List<ThreadDumpInfo> fetchThreadDump(Object source);

}
