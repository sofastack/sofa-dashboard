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
package com.alipay.sofa.dashboard.model;

import java.util.Objects;

/**
 * 引用运行状态概况
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/7/14 10:38 AM
 **/
public class ApplicationInfo implements Comparable<ApplicationInfo> {

    /**
     * 服务名称
     */
    private String applicationName;

    /**
     * 运行实例总数
     */
    private int    applicationCount;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(int applicationCount) {
        this.applicationCount = applicationCount;
    }

    @Override
    public String toString() {
        return "AppStatistic{" + "applicationName='" + applicationName + '\''
               + ", applicationCount=" + applicationCount + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ApplicationInfo statistic = (ApplicationInfo) o;
        return getApplicationCount() == statistic.getApplicationCount()
               && Objects.equals(getApplicationName(), statistic.getApplicationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApplicationName(), getApplicationCount());
    }

    @Override
    public int compareTo(ApplicationInfo o) {
        return applicationName.compareTo(o.applicationName);
    }
}
