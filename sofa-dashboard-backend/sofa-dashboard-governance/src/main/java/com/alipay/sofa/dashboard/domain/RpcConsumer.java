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
package com.alipay.sofa.dashboard.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author bystander
 * @version $Id: Consumer.java, v 0.1 2018年12月10日 23:43 bystander Exp $
 */
public class RpcConsumer implements Serializable {

    private String              serviceName;
    private Map<String, String> parameters;
    private String              address;
    private Map<String, String> overrides;
    private String              appName;
    private boolean             enabled;
    private boolean             dynamic;
    private List<RpcProvider>   providers;
    private String              router;
    private boolean             cell;
    private int                 port;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, String> getOverrides() {
        return overrides;
    }

    public void setOverrides(Map<String, String> overrides) {
        this.overrides = overrides;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<RpcProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<RpcProvider> providers) {
        this.providers = providers;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public boolean isCell() {
        return cell;
    }

    public void setCell(boolean cell) {
        this.cell = cell;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RpcConsumer{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append(", address='").append(address).append('\'');
        sb.append(", overrides=").append(overrides);
        sb.append(", appName='").append(appName).append('\'');
        sb.append(", enabled=").append(enabled);
        sb.append(", dynamic=").append(dynamic);
        sb.append(", providers=").append(providers);
        sb.append(", router='").append(router).append('\'');
        sb.append(", cell=").append(cell);
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RpcConsumer that = (RpcConsumer) o;
        return enabled == that.enabled && dynamic == that.dynamic && cell == that.cell
               && port == that.port && Objects.equals(serviceName, that.serviceName)
               && Objects.equals(parameters, that.parameters)
               && Objects.equals(address, that.address)
               && Objects.equals(overrides, that.overrides)
               && Objects.equals(appName, that.appName)
               && Objects.equals(providers, that.providers) && Objects.equals(router, that.router);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, parameters, address, overrides, appName, enabled, dynamic,
            providers, router, cell, port);
    }
}