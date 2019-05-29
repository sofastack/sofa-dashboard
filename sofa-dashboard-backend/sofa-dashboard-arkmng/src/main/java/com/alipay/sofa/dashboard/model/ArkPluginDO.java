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

import java.util.Date;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 19/1/10 下午4:13
 * @since:
 **/
public class ArkPluginDO {
    private Integer id;
    private String  pluginName;
    private Date    createTime;
    private String  description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ArkPluginDO(String pluginName, Date createTime, String description) {
        this.pluginName = pluginName;
        this.createTime = createTime;
        this.description = description;
    }

    public ArkPluginDO() {
    }

    @Override
    public String toString() {
        return "ArkPluginDO{" + "id=" + id + ", pluginName='" + pluginName + '\'' + ", createTime="
               + createTime + ", description='" + description + '\'' + '}';
    }
}
