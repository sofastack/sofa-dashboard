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

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/6 下午4:02
 * @since:
 **/
public class AppModel {

    private String id;
    private String name;
    private String host;
    private String state;
    private int    port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppModel(String name, String host, String state) {
        this.name = name;
        this.host = host;
        this.state = state;
    }

    public AppModel() {
    }

    @Override
    public String toString() {
        return "AppModel{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", host='" + host
               + '\'' + ", state='" + state + '\'' + '}';
    }
}
