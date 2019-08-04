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
package com.alipay.sofa.dashboard.mock;

import com.alipay.sofa.dashboard.client.io.RecordExporter;
import com.alipay.sofa.dashboard.client.io.RecordImporter;
import com.alipay.sofa.dashboard.client.model.common.HostAndPort;
import com.alipay.sofa.dashboard.client.model.io.StoreRecord;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 用于单元测试的存储实例
 */
public class MockStore implements TestRule, RecordImporter, RecordExporter {

    private final Map<HostAndPort, Map<String, Queue<StoreRecord>>> store = new ConcurrentHashMap<>();

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                store.clear(); // 在测例执行前清空一次Store数据
                statement.evaluate();
            }
        };
    }

    @Override
    public List<StoreRecord> getLatestRecords(HostAndPort hostAndPort, String schemeName,
                                              long duration) {
        Queue<StoreRecord> records = store.getOrDefault(hostAndPort, new HashMap<>()).getOrDefault(
            schemeName, new PriorityQueue<>());
        return new ArrayList<>(records);
    }

    @Override
    public void createTablesIfNotExists(HostAndPort hostAndPort, Set<String> dimensionSchemes) {
        // Do nothing here
    }

    @Override
    public void addRecords(HostAndPort hostAndPort, List<StoreRecord> records) {
        store.compute(hostAndPort, (key, value) -> {
            Map<String, Queue<StoreRecord>> data = Optional.ofNullable(value)
                .orElse(new ConcurrentHashMap<>());
            for (StoreRecord record : records) {
                data.compute(record.getSchemeName(), (dKey, dValue) -> {
                    Queue<StoreRecord> queue = Optional.ofNullable(dValue)
                        .orElse(new PriorityBlockingQueue<>(16,
                            Comparator.comparingLong(o -> o.getTimestamp())));
                    queue.add(record);
                    return queue;
                });
            }
            return data;
        });
    }
}