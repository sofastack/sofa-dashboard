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
package com.alipay.sofa.dashboard.service;

import com.alipay.sofa.dashboard.model.ArkPluginDO;
import com.alipay.sofa.dashboard.model.ArkPluginModel;

import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/19 上午11:13
 * @since:
 **/
public interface ArkMngService {

    /**
     * 获取当前所有注册的 plugin 信息
     *
     * @return
     */
    List<ArkPluginModel> fetchRegisteredPlugins();

    /**
     * 向管控端注册插件
     *
     * @param model
     * @return
     */
    boolean registerPlugin(ArkPluginDO model);

    /**
     * 给模块增加新的版本
     *
     * @param pluginName
     * @param version
     * @param address
     * @return
     */
    boolean addNewVersion(String pluginName, String version, String address);

    /**
     * 删除一个模块
     *
     * @param pluginName
     * @return
     */
    boolean removePlugins(String pluginName);

    /**
     * 通过插件名查询插件
     *
     * @param pluginName
     * @return
     */
    List<ArkPluginModel> fetchPluginsByName(String pluginName);

    /**
     * 关联应用和插件
     *
     * @param pluginName
     * @param appName
     * @return
     */
    int relatedAppToPlugin(String pluginName, String appName);

    /**
     * 根据插件名查询当前插件关联的应用名
     *
     * @param pluginName
     * @return
     */
    List<String> queryAppsByPlugin(String pluginName);

    /**
     * 取消
     *
     * @param appName
     * @param pluginName
     * @return
     */
    int cancelRelatedAppToPlugin(String pluginName, String appName);

}
