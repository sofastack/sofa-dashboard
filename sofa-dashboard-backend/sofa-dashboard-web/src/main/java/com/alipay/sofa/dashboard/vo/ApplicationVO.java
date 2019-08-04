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
package com.alipay.sofa.dashboard.vo;

import com.alipay.sofa.dashboard.model.AppInfo;

import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 19/1/11 下午6:49
 * @since:
 **/
public class ApplicationVO {
    private Integer       totalCount;
    private Integer       successCount;
    private Integer       failCount;
    private List<AppInfo> data;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public List<AppInfo> getData() {
        return data;
    }

    public void setData(List<AppInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApplicationVO{" + "totalCount=" + totalCount + ", successCount=" + successCount
               + ", failCount=" + failCount + ", data=" + data + '}';
    }
}
