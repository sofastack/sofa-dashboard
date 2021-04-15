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

import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.model.health.HealthDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.io.RecordName;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.client.utils.JsonUtils;
import com.alipay.sofa.dashboard.mock.MockStore;
import com.alipay.sofa.dashboard.spi.MonitorService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class MonitorServiceTest {

    private static final Logger  LOGGER  = LoggerFactory.getLogger(MonitorServiceTest.class);

    @Rule
    public final MockStore       store   = new MockStore();

    private final MonitorService service = new MonitorServiceImpl(store);

    private final Random         random  = new Random();

    @Test
    public void fetchEnvironmentTest() {
        EnvironmentDescriptor descriptor = new EnvironmentDescriptor();
        HostAndPort hostAndPort = randomInstance();

        store.addRecords(
            hostAndPort,
            Lists.newArrayList(StoreRecord.newBuilder().schemeName(RecordName.ENVIRONMENT)
                .timestamp(System.currentTimeMillis()).value(JsonUtils.toJsonString(descriptor))
                .build()));

        EnvironmentDescriptor query = service.fetchEnvironment(hostAndPort);
        Assert.assertNotNull(query);
        LOGGER.info("Fetch env => {}", JsonUtils.toJsonString(query));
    }

    @Test
    public void fetchHealthTest() {
        HealthDescriptor descriptor = new HealthDescriptor();
        HostAndPort hostAndPort = randomInstance();

        store.addRecords(
            hostAndPort,
            Lists.newArrayList(StoreRecord.newBuilder().schemeName(RecordName.HEALTH)
                .timestamp(System.currentTimeMillis()).value(JsonUtils.toJsonString(descriptor))
                .build()));

        HealthDescriptor query = service.fetchHealth(hostAndPort);
        Assert.assertNotNull(query);
        LOGGER.info("Fetch env => {}", JsonUtils.toJsonString(query));
    }

    @Test
    public void fetchInfoTest() {
        InfoDescriptor descriptor = new InfoDescriptor();
        HostAndPort hostAndPort = randomInstance();

        store.addRecords(
            hostAndPort,
            Lists.newArrayList(StoreRecord.newBuilder().schemeName(RecordName.INFO)
                .timestamp(System.currentTimeMillis()).value(JsonUtils.toJsonString(descriptor))
                .build()));

        InfoDescriptor query = service.fetchInfo(hostAndPort);
        Assert.assertNotNull(query);
        LOGGER.info("Fetch env => {}", JsonUtils.toJsonString(query));
    }

    @Test
    public void fetchLoggersTest() {
        LoggersDescriptor descriptor = new LoggersDescriptor();
        HostAndPort hostAndPort = randomInstance();

        store.addRecords(
            hostAndPort,
            Lists.newArrayList(StoreRecord.newBuilder().schemeName(RecordName.LOGGERS)
                .timestamp(System.currentTimeMillis()).value(JsonUtils.toJsonString(descriptor))
                .build()));

        LoggersDescriptor query = service.fetchLoggers(hostAndPort);
        Assert.assertNotNull(query);
        LOGGER.info("Fetch env => {}", JsonUtils.toJsonString(query));
    }

    @Test
    public void fetchMappingsTest() {
        MappingsDescriptor descriptor = new MappingsDescriptor();
        HostAndPort hostAndPort = randomInstance();

        store.addRecords(
            hostAndPort,
            Lists.newArrayList(StoreRecord.newBuilder().schemeName(RecordName.MAPPINGS)
                .timestamp(System.currentTimeMillis()).value(JsonUtils.toJsonString(descriptor))
                .build()));

        MappingsDescriptor query = service.fetchMappings(hostAndPort);
        Assert.assertNotNull(query);
        LOGGER.info("Fetch env => {}", JsonUtils.toJsonString(query));
    }

    private HostAndPort randomInstance() {
        String host = UUID.randomUUID().toString().replace("-", "").substring(8);
        int port = random.nextInt(65536);
        return new HostAndPort(host, null, port);
    }
}
