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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.client.registry.AppPublisher;
import com.alipay.sofa.dashboard.client.registry.AppSubscriber;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class MockRegistry implements TestRule {

    /**
     * In-memory copy of zookeeper session information
     */
    private volatile Map<String, Set<Application>> applications = new ConcurrentHashMap<>();

    public AppPublisher publisher(Application app) {
        MockPublisher publisher = new MockPublisher();
        publisher.start();
        return publisher;
    }

    public AppSubscriber subscriber() {
        MockSubscriber subscriber = new MockSubscriber();
        subscriber.start();
        return subscriber;
    }

    public List<Application> all() {
        Set<Application> reduce = applications.values().stream().reduce((a, b) -> {
            Set<Application> result = new LinkedHashSet<>();
            result.addAll(a);
            result.addAll(b);
            return result;
        }).orElse(new HashSet<>());
        List<Application> result = new ArrayList<>(reduce);
        Collections.sort(result);
        return result;
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                applications.clear(); // 在测例执行前清空一次Registry数据
                statement.evaluate();
            }
        };
    }

    private class MockPublisher implements AppPublisher {

        @Override
        public Application getApplication() {
            Application mockApp = new Application();
            mockApp.setAppName("testApp");
            mockApp.setAppState("RUNNING");
            mockApp.setHostName("127.0.0.1");
            mockApp.setLastRecover(System.currentTimeMillis());
            mockApp.setPort(8080);
            mockApp.setStartTime(System.currentTimeMillis());
            return mockApp;
        }

        @Override
        public boolean start() {
            return true;
        }

        @Override
        public void shutdown() throws Exception {
            unRegister();
        }

        @Override
        public void register() throws Exception {
            applications.compute(getApplication().getAppName(), (k, v) -> {
                Set<Application> container = Optional.ofNullable(v)
                    .orElse(new ConcurrentSkipListSet<>());
                container.add(getApplication());
                return container;
            });
        }

        @Override
        public void unRegister() throws Exception {
            applications.computeIfPresent(getApplication().getAppName(), (k, v) -> {
                v.remove(getApplication());
                return v;
            });
        }
    }

    private class MockSubscriber implements AppSubscriber {

        @Override
        public boolean start() {
            return true;
        }

        @Override
        public void shutdown() {
            // Do nothing
        }

        @Override
        public List<Application> getAll() {
            // Get a readonly copy
            Map<String, Set<Application>> copy = Collections.unmodifiableMap(applications);

            Set<Application> apps = copy.values().stream().reduce((a, b) -> {
                Set<Application> collector = new HashSet<>(a);
                collector.addAll(b);
                return collector;
            }).orElse(new HashSet<>());
            return new ArrayList<>(apps);
        }

        @Override
        public List<Application> getByName(@Nullable String appName) {
            // Get a readonly copy
            Map<String, Set<Application>> copy = Collections.unmodifiableMap(applications);

            Set<Application> apps = copy.get(appName);
            return apps == null ? new ArrayList<>() : new ArrayList<>(apps);
        }

        @Override
        public List<String> getAllNames() {
            // Get a readonly copy
            Set<String> copy = Collections.unmodifiableSet(applications.keySet());

            return new ArrayList<>(copy);
        }

        @Override
        public Map<String, Integer> summaryCounts() {
            // Get a readonly copy
            Map<String, Set<Application>> copy = Collections.unmodifiableMap(applications);

            return copy.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                it -> it.getValue() == null ? 0 : it.getValue().size()));
        }
    }
}
