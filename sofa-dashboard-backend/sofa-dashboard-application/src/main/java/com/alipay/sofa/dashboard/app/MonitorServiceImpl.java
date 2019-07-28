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
package com.alipay.sofa.dashboard.app;

import com.alipay.sofa.dashboard.client.io.RecordExporter;
import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.model.health.HealthDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.io.RecordName;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.client.model.memory.MemoryDescriptor;
import com.alipay.sofa.dashboard.client.model.thread.ThreadSummaryDescriptor;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import com.alipay.sofa.dashboard.model.StampedValueEntity;
import com.alipay.sofa.dashboard.spi.MonitorService;
import org.springframework.stereotype.Component;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 应用统计面板实现
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 **/
@Component
public class MonitorServiceImpl implements MonitorService {

    /**
     * 查询最近数据时间片(minutes)
     */
    private static final int     SINGLE_QUERY_DURATION = 1;

    /**
     * 查询过去多条记录时间片(minutes)
     */
    private static final int     MULTI_QUERY_DURATION  = 5;

    private final RecordExporter exporter;

    public MonitorServiceImpl(RecordExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    @Nullable
    public EnvironmentDescriptor fetchEnvironment(HostAndPort hostAndPort) {
        return queryOne(hostAndPort, RecordName.ENVIRONMENT, EnvironmentDescriptor.class);
    }

    @Override
    public HealthDescriptor fetchHealth(HostAndPort hostAndPort) {
        return queryOne(hostAndPort, RecordName.HEALTH, HealthDescriptor.class);
    }

    @Override
    public InfoDescriptor fetchInfo(HostAndPort hostAndPort) {
        return queryOne(hostAndPort, RecordName.INFO, InfoDescriptor.class);
    }

    @Override
    public LoggersDescriptor fetchLoggers(HostAndPort hostAndPort) {
        return queryOne(hostAndPort, RecordName.LOGGERS, LoggersDescriptor.class);
    }

    @Override
    public MappingsDescriptor fetchMappings(HostAndPort hostAndPort) {
        return queryOne(hostAndPort, RecordName.MAPPINGS, MappingsDescriptor.class);
    }

    @Override
    public List<StampedValueEntity<ThreadSummaryDescriptor>> fetchThreadInfo(HostAndPort hostAndPort) {
        return queryList(hostAndPort, RecordName.THREAD_SUMMARY, ThreadSummaryDescriptor.class);
    }

    @Override
    public List<StampedValueEntity<MemoryDescriptor>> fetchMemoryInfo(HostAndPort hostAndPort) {
        return queryList(hostAndPort, RecordName.MEMORY, MemoryDescriptor.class);
    }

    @Nullable
    private <T> T queryOne(HostAndPort hostAndPort, String schemeName, Class<T> descriptorType) {
        List<StoreRecord> records = exporter.getLatestRecords(hostAndPort, schemeName,
            TimeUnit.MINUTES.toMillis(SINGLE_QUERY_DURATION));
        return records.stream().map(StoreRecord::getValue)
            .map(it -> JsonUtils.parseObject(it, descriptorType))
            .findFirst()
            .orElse(null);
    }

    @NonNull
    private <T extends Serializable> List<StampedValueEntity<T>> queryList(
        HostAndPort hostAndPort, String schemeName, Class<T> descriptorType) {
        List<StoreRecord> records = exporter.getLatestRecords(hostAndPort,
            schemeName, TimeUnit.MINUTES.toMillis(MULTI_QUERY_DURATION));

        return records.stream().map(it -> {
            StampedValueEntity<T> value = new StampedValueEntity<>();
            value.setName(RecordName.THREAD_SUMMARY);
            value.setTimestamp(formatTime(it.getTimestamp()));
            value.setValue(JsonUtils.parseObject(it.getValue(), descriptorType));
            return value;
        }).collect(Collectors.toList());
    }

    @NonNull
    private String formatTime(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }
}
