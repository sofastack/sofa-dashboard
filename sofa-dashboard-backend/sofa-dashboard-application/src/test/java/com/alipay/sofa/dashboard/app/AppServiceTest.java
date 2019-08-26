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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.mock.MockRegistry;
import com.alipay.sofa.dashboard.model.ApplicationInfo;
import com.alipay.sofa.dashboard.spi.AppService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class AppServiceTest {

    @Rule
    public final MockRegistry registry = new MockRegistry();

    private final AppService  service  = new AppServiceImpl(registry.subscriber());

    private final Random      random   = new Random();

    @Test
    public void emptyDataTest() {
        List<ApplicationInfo> allStatistics = service.getAllStatistics();
        Assert.assertTrue(allStatistics.isEmpty());

        List<ApplicationInfo> queryStatistic = service.getStatisticsByKeyword("none");
        Assert.assertTrue(queryStatistic.isEmpty());

        List<Application> allInstances = service.getAllInstances();
        Assert.assertTrue(allInstances.isEmpty());

        List<Application> queryInstances = service.getInstancesByName("none");
        Assert.assertTrue(queryInstances.isEmpty());
    }

    @Test
    public void queryAllStatisticsTest() throws Exception {
        long current = System.currentTimeMillis();
        for (String[] pattern : new String[][] { { "10.1.1.1", "service_a" },
                                                 { "10.1.1.2", "service_b" },
                                                 { "10.1.1.3", "service_c" },
                                                 { "10.1.1.4", "service_d" },
                                                 { "10.1.1.5", "service_d" } }) {
            registry.publisher(
                Application.newBuilder().appState("NORMAL").startTime(current)
                    .lastRecover(System.currentTimeMillis()).hostName(pattern[0])
                    .appName(pattern[1]).port(random.nextInt(65536)).build()).register();
        }

        List<ApplicationInfo> expected = registry.all().stream()
            .collect(Collectors.groupingBy(it -> it.getAppName(), Collectors.counting()))
            .entrySet().stream()
            .map(entry -> {
                ApplicationInfo statistic = new ApplicationInfo();
                statistic.setApplicationName(entry.getKey());
                statistic.setApplicationCount(entry.getValue().intValue());
                return statistic;
            })
            .collect(Collectors.toList());
        List<ApplicationInfo> query = service.getAllStatistics();

        Collections.sort(expected);
        Collections.sort(query);
        Assert.assertArrayEquals(expected.toArray(), query.toArray());
    }

    @Test
    public void queryStatisticsByKeyWordTest() throws Exception {
        long current = System.currentTimeMillis();
        for (String[] pattern : new String[][] { { "10.1.1.1", "service_a" },
                                                 { "10.1.1.2", "service_b" },
                                                 { "10.1.1.3", "service_c" },
                                                 { "10.1.1.4", "service_d" },
                                                 { "10.1.1.5", "service_d" } }) {
            registry.publisher(
                Application.newBuilder().appState("NORMAL").startTime(current)
                    .lastRecover(System.currentTimeMillis()).hostName(pattern[0])
                    .appName(pattern[1]).port(random.nextInt(65536)).build()).register();
        }

        for (String keyword : new String[] { "a", "service", "d", "f" }) {
            List<ApplicationInfo> expected = registry.all().stream()
                .filter(it -> it.getAppName().contains(keyword))
                .collect(Collectors.groupingBy(it -> it.getAppName(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    ApplicationInfo statistic = new ApplicationInfo();
                    statistic.setApplicationName(entry.getKey());
                    statistic.setApplicationCount(entry.getValue().intValue());
                    return statistic;
                })
                .collect(Collectors.toList());
            List<ApplicationInfo> query = service.getStatisticsByKeyword(keyword);

            Collections.sort(expected);
            Collections.sort(query);
            Assert.assertArrayEquals(expected.toArray(), query.toArray());
        }
    }

    @Test
    public void queryAllInstancesTest() throws Exception {
        long current = System.currentTimeMillis();
        for (String[] pattern : new String[][] { { "10.1.1.1", "service_a" },
                { "10.1.1.2", "service_b" }, { "10.1.1.3", "service_c" },
                { "10.1.1.4", "service_d" }, { "10.1.1.5", "service_d" } }) {
            registry.publisher(
                Application.newBuilder().appState("NORMAL").startTime(current)
                    .lastRecover(System.currentTimeMillis()).hostName(pattern[0])
                    .appName(pattern[1]).port(random.nextInt(65536)).build()).register();
        }

        List<Application> apps = registry.all();
        List<Application> query = service.getAllInstances();

        Collections.sort(apps);
        Collections.sort(query);
        Assert.assertArrayEquals(apps.toArray(), query.toArray());
    }

    @Test
    public void queryInstancesByNameTest() throws Exception {
        long current = System.currentTimeMillis();
        for (String[] pattern : new String[][] { { "10.1.1.1", "service_a" },
                                                 { "10.1.1.2", "service_b" },
                                                 { "10.1.1.3", "service_c" },
                                                 { "10.1.1.4", "service_d" },
                                                 { "10.1.1.5", "service_d" } }) {
            registry.publisher(
                Application.newBuilder().appState("NORMAL").startTime(current)
                    .lastRecover(System.currentTimeMillis()).hostName(pattern[0])
                    .appName(pattern[1]).port(random.nextInt(65536)).build()).register();
        }

        for (String appName : new String[] { "service_a", "service_d", "service_f", null }) {
            List<Application> apps = registry.all().stream()
                .filter(
                    it -> StringUtils.isEmpty(appName) || Objects.equals(it.getAppName(), appName))
                .collect(Collectors.toList());
            List<Application> query = service.getInstancesByName(appName);
            Collections.sort(apps);
            Collections.sort(query);
            Assert.assertArrayEquals(apps.toArray(), query.toArray());
        }
    }
}
