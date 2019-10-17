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
import com.alipay.sofa.dashboard.client.registry.AppSubscriber;
import com.alipay.sofa.dashboard.model.ApplicationInfo;
import com.alipay.sofa.dashboard.spi.AppService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用实例服务实现
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/7/11 2:45 PM
 **/
@Service
public class AppServiceImpl implements AppService {

    private final AppSubscriber subscriber;

    public AppServiceImpl(AppSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public List<ApplicationInfo> getAllStatistics() {
        return getStatisticsByKeyword(null);
    }

    @Override
    public List<ApplicationInfo> getStatisticsByKeyword(@Nullable String keyword) {
        return subscriber.summaryCounts().entrySet().stream().map(entry -> {
            ApplicationInfo statistic = new ApplicationInfo();
            statistic.setApplicationName(entry.getKey());
            statistic.setApplicationCount(entry.getValue());
            return statistic;
        }).filter(it ->
            // 关键词为空或者服务名包含关键词
                (StringUtils.isEmpty(keyword) ||
            // 名字不为空，并且实例数不为 0
            !StringUtils.isEmpty(it.getApplicationName())
            && it.getApplicationName().contains(keyword)) && it.getApplicationCount() > 0
        ).collect(Collectors.toList());
    }

    @Override
    public List<Application> getAllInstances() {
        return subscriber.getAll();
    }

    @Override
    public List<Application> getInstancesByName(@Nullable String serviceName) {
        return StringUtils.isEmpty(serviceName) ? subscriber.getAll() : subscriber
            .getByName(serviceName);
    }
}
