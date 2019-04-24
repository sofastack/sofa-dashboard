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

import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 19/1/10 下午10:08
 * @since:
 **/
public class CommandRequest {

    /**
     * 命令推送维度
     */
    private String       dimension;

    /**
     * 命令
     */
    private String       command;
    /**
     * 目标主机
     */
    private List<String> targetHost;

    /**
     * 插件名
     */
    private String       pluginName;

    /**
     * 插件版本
     */
    private String       pluginVersion;

    /**
     * 宿主应用名
     */
    private String       appName;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(List<String> targetHost) {
        this.targetHost = targetHost;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "CommandRequest{" + "dimension='" + dimension + '\'' + ", command='" + command
               + '\'' + ", targetHost=" + targetHost + ", pluginName='" + pluginName + '\''
               + ", pluginVersion='" + pluginVersion + '\'' + ", appName='" + appName + '\'' + '}';
    }
}
