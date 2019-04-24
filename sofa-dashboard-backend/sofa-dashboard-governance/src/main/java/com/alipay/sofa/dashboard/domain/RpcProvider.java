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
import java.util.Map;

/**
 * @author bystander
 * @version $Id: Provider.java, v 0.1 2018年12月10日 23:43 bystander Exp $
 */
public class RpcProvider implements Serializable {

    private String              serviceName;
    private String              url;
    private Map<String, String> parameters;
    private String              address;
    private int                 port;
    private Map<String, String> overrides;
    private String              appName;
    private int                 weight;
    private boolean             enabled;
    private boolean             dynamic;
    private boolean             cell;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public boolean isCell() {
        return cell;
    }

    public void setCell(boolean cell) {
        this.cell = cell;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Provider{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append(", address='").append(address).append('\'');
        sb.append(", port=").append(port);
        sb.append(", overrides=").append(overrides);
        sb.append(", appName='").append(appName).append('\'');
        sb.append(", weight=").append(weight);
        sb.append(", enabled=").append(enabled);
        sb.append(", dynamic=").append(dynamic);
        sb.append(", cell=").append(cell);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RpcProvider)) {
            return false;
        }

        RpcProvider provider = (RpcProvider) o;
        if (port != provider.port) {
            return false;
        }
        if (weight != provider.weight) {
            return false;
        }
        if (enabled != provider.enabled) {
            return false;
        }
        if (dynamic != provider.dynamic) {
            return false;
        }
        if (cell != provider.cell) {
            return false;
        }
        if (serviceName != null ? !serviceName.equals(provider.serviceName)
            : provider.serviceName != null) {
            return false;
        }
        if (url != null ? !url.equals(provider.url) : provider.url != null) {
            return false;
        }
        if (parameters != null ? !parameters.equals(provider.parameters)
            : provider.parameters != null) {
            return false;
        }
        if (address != null ? !address.equals(provider.address) : provider.address != null) {
            return false;
        }
        if (overrides != null ? !overrides.equals(provider.overrides) : provider.overrides != null) {
            return false;
        }
        return appName != null ? appName.equals(provider.appName) : provider.appName == null;
    }

    @Override
    public int hashCode() {
        int result = serviceName != null ? serviceName.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (overrides != null ? overrides.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + weight;
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (dynamic ? 1 : 0);
        result = 31 * result + (cell ? 1 : 0);
        return result;
    }
}