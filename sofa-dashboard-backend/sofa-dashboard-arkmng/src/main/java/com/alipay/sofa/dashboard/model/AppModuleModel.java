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

import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/24 上午11:12
 * @since:
 **/
public class AppModuleModel {
    /**
     * 默认版本
     */
    private String            defaultVersion;
    /**
     * 默认版本下使用的所有应用列表
     */
    private List<ArkAppModel> appList     = new ArrayList<>();
    /**
     * 版本列表
     */
    private List<String>      versionList = new ArrayList<>();

    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    public List<ArkAppModel> getAppList() {
        return appList;
    }

    public void setAppList(List<ArkAppModel> appList) {
        this.appList = appList;
    }

    public List<String> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<String> versionList) {
        this.versionList = versionList;
    }
}
