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
 * 应用实例模型
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/6 下午4:02
 * @since:
 **/
public class AppInfo {

    private String appName;
    private String hostName;
    private int    port;
    private String appState;
    /**
     * 应用启动时间(如果应用是重启的，则表示最近一次启动时间)
     */
    private String startTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public AppInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppInfo that = (AppInfo) o;
        return port == that.port && Objects.equals(appName, that.appName)
               && Objects.equals(hostName, that.hostName)
               && Objects.equals(appState, that.appState)
               && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, hostName, port, appState, startTime);
    }
}
