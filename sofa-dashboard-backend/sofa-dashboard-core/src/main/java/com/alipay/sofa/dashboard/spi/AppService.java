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

import com.alipay.sofa.dashboard.client.model.common.Application;
import com.alipay.sofa.dashboard.model.ApplicationInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 应用实例统计服务
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface AppService {

    /**
     * 获取全部应用统计信息
     *
     * @return 全部应用统计信息，如果没有实例则返回空列表
     */
    @NonNull
    List<ApplicationInfo> getAllStatistics();

    /**
     * 获取包含关键词的应用统计信息
     *
     * @param keyword 关键词，如果为空，则返回全部统计信息
     * @return 满足条件应用统计信息，如果没有实例则返回空列表
     */
    @NonNull
    List<ApplicationInfo> getStatisticsByKeyword(@Nullable String keyword);

    /**
     * 获取全部应用实例信息
     *
     * @return 全部应用实例信息, 如果没有实例则返回空列表
     */
    @NonNull
    List<Application> getAllInstances();

    /**
     * 获取服务名匹配的全部实例信息, 如果查询名为空，则返回全部实例信息
     *
     * @param serviceName 服务名称
     * @return 满足条件应用实例信息，如果没有实例则返回空列表
     */
    @NonNull
    List<Application> getInstancesByName(@Nullable String serviceName);
}
