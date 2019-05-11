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
package com.alipay.sofa.dashboard.model.monitor;

import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 9:23 PM
 * @since:
 **/
public class MetricsInfo {

    private String                    name;

    private List<Map<String, String>> measurements;

    private List<AvailableTagInfo>    availableTags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Map<String, String>> measurements) {
        this.measurements = measurements;
    }

    public List<AvailableTagInfo> getAvailableTags() {
        return availableTags;
    }

    public void setAvailableTags(List<AvailableTagInfo> availableTags) {
        this.availableTags = availableTags;
    }
}
