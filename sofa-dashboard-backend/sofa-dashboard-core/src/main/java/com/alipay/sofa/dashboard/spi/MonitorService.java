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

import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.model.health.HealthDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.client.model.memory.MemoryDescriptor;
import com.alipay.sofa.dashboard.client.model.thread.ThreadSummaryDescriptor;
import com.alipay.sofa.dashboard.model.StampedValueEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 应用实例统计面板信息
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface MonitorService {

    /**
     * 获取应用环境变量描述.
     *
     * @param hostAndPort 实例地址
     * @return 实例环境变量信息，如果无数据则返回{@code null}
     */
    @Nullable
    EnvironmentDescriptor fetchEnvironment(@NonNull HostAndPort hostAndPort);

    /**
     * 获取应用实例的健康度信息.
     *
     * @param hostAndPort 实例地址
     * @return 实例健康度信息，如果无数据则返回{@code null}
     */
    @Nullable
    HealthDescriptor fetchHealth(@NonNull HostAndPort hostAndPort);

    /**
     * 获取应用实例描述.
     *
     * @param hostAndPort 实例地址
     * @return 实例描述, 如果无数据则返回{@code null}
     */
    @Nullable
    InfoDescriptor fetchInfo(@NonNull HostAndPort hostAndPort);

    /**
     * 获取应用的logger信息.
     *
     * @param hostAndPort 实例地址
     * @return logger信息, 如果无数据则返回{@code null}
     */
    @Nullable
    LoggersDescriptor fetchLoggers(@NonNull HostAndPort hostAndPort);

    /**
     * 获取引用的mapping信息
     *
     * @param hostAndPort 实例地址
     * @return mapping信息, 如果无数据则返回{@code null}
     */
    @Nullable
    MappingsDescriptor fetchMappings(@NonNull HostAndPort hostAndPort);

    /**
     * 获取过去一段时间的线程概况统计信息(按照时间升序排序).
     *
     * @param hostAndPort 实例地址
     * @return 线程概况统计信息, 如果数据为空则返回空列表
     */
    @NonNull
    List<StampedValueEntity<ThreadSummaryDescriptor>> fetchThreadInfo(@NonNull HostAndPort hostAndPort);

    /**
     * 获取过去一段时间的内存概况统计信息(按照时间升序排序).
     *
     * @param hostAndPort 实例地址
     * @return 内存概况统计信息，如果数据为空则返回空列表
     */
    @NonNull
    List<StampedValueEntity<MemoryDescriptor>> fetchMemoryInfo(@NonNull HostAndPort hostAndPort);

}
