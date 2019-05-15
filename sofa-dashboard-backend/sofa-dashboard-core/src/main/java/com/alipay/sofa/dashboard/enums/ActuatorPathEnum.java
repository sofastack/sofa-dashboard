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
package com.alipay.sofa.dashboard.enums;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/8 10:44 AM
 * @since:
 **/
public enum ActuatorPathEnum {

    ENV("/actuator/env", "app env info"),
    HEALTH("/actuator/health", "health check"),
    MAPPINGS("/actuator/mappings", "mappings"),
    LOGGERS("/actuator/loggers", "loggers"),
    THREADDUMP("/actuator/threaddump", "threaddump"),
    METRICS("/actuator/metrics", "metrics"),
    HTTPTRACE("/actuator/httptrace", "httptrace"),
    INFO("/actuator/info", "info");

    private String path;
    private String description;

    ActuatorPathEnum(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
