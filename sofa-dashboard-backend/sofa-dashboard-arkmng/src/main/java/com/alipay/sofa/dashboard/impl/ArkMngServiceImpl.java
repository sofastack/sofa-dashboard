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
package com.alipay.sofa.dashboard.impl;

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.dao.ArkDao;
import com.alipay.sofa.dashboard.model.AppArkDO;
import com.alipay.sofa.dashboard.model.ArkModuleUserDO;
import com.alipay.sofa.dashboard.model.ArkModuleVersionDO;
import com.alipay.sofa.dashboard.model.ArkPluginDO;
import com.alipay.sofa.dashboard.model.ArkPluginModel;
import com.alipay.sofa.dashboard.service.ArkMngService;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * data for ark mng
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/19 上午11:14
 * @since:
 **/
@Service
public class ArkMngServiceImpl implements ArkMngService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArkMngServiceImpl.class);

    @Autowired
    private ArkDao              arkDao;

    @Override
    public List<ArkPluginModel> fetchRegisteredPlugins() {
        return doFetchPluginsByName(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerPlugin(ArkPluginDO model, String version) {
        try {
            // 向模块表中插入一条数据
            arkDao.insert(model);
            int mId = model.getId();
            // 向模板版本表中插入数据
            doInsertModuleVersion(mId, version, model.getPluginName());
            // 向模板Owner表中插入数据
            ArkModuleUserDO arkModuleUserDO = new ArkModuleUserDO();
            arkModuleUserDO.setModuleId(mId);
            // -1 不需要权限校验即可访问使用
            arkModuleUserDO.setUserId(-1);
            arkModuleUserDO.setCreateTime(model.getCreateTime());
            arkDao.insertModuleUser(arkModuleUserDO);
        } catch (Exception e) {
            LOGGER.error("Error to register plugin.", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addNewVersion(String pluginName, String version) {
        int mId = arkDao.queryModuleIdByName(pluginName);
        // 向模板版本表中插入数据
        return doInsertModuleVersion(mId, version, pluginName) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removePlugins(String pluginName) {
        try {
            int mId = arkDao.queryModuleIdByName(pluginName);
            // 移除模块表
            arkDao.remove(mId);
            // 移除模块版本表
            arkDao.removeModuleVersion(mId);
            // 移除模块Owner表
            arkDao.removeModuleUser(mId);
        } catch (Exception e) {
            LOGGER.error("Error to remove plugin.", e);
            return false;
        }
        return true;
    }

    @Override
    public List<ArkPluginModel> fetchPluginsByName(String pluginName) {
        return doFetchPluginsByName(pluginName);
    }

    @Override
    public int relatedAppToPlugin(String pluginName, String appName) {
        int moduleId = queryModuleIdByPluginName(pluginName);
        if (moduleId < 0) {
            return -1;
        }
        AppArkDO appArkDO = new AppArkDO();
        appArkDO.setAppName(appName);
        appArkDO.setCreateTime(DashboardUtil.now());
        appArkDO.setModuleId(moduleId);
        arkDao.insertAppArk(appArkDO);
        return appArkDO.getId();
    }

    @Override
    public List<String> queryAppsByPlugin(String pluginName) {
        int moduleId = queryModuleIdByPluginName(pluginName);
        return arkDao.queryAppsModuleId(moduleId);
    }

    @Override
    public int cancelRelatedAppToPlugin(String pluginName, String appName) {
        int moduleId = queryModuleIdByPluginName(pluginName);
        return arkDao.deleteByPluginNameAndAppName(moduleId, appName);
    }

    private int queryModuleIdByPluginName(String pluginName) {
        return arkDao.queryModuleIdByPluginName(pluginName);
    }

    private List<ArkPluginModel> doFetchPluginsByName(String pluginName) {
        List<ArkPluginModel> result = new ArrayList<>();
        // 模糊查询所有
        List<ArkPluginDO> list = arkDao.queryModuleInfoByName(pluginName);
        for (ArkPluginDO item : list) {
            ArkPluginModel temp = new ArkPluginModel();
            List<String> versions = arkDao.queryVersionsByMid(item.getId());
            temp.setVersions(versions);
            temp.setId(item.getId());
            temp.setAddress(item.getPluginUrl());
            temp.setDescription(item.getDescription());
            temp.setPluginName(item.getPluginName());
            result.add(temp);
        }
        return result;
    }

    /**
     * 构建版本信息，此处会生成默认的 版本地址,与插件地址组合之后可以唯一确定当前 插件包的地址
     * <p>
     * 如 插件地址 为 ：http://ip:port/sofa/ark
     * 版本地址 为 ：/1.0.0/test-plugin-1.0.0-ark-biz.jar
     * <p>
     * 则 最终地址为 http://ip:port/sofa/ark/1.0.0/test-plugin-1.0.0-ark-biz.jar
     *
     * @param mId
     * @param version
     * @param pluginName
     * @return
     */
    private int doInsertModuleVersion(int mId, String version, String pluginName) {
        // 向模板版本表中插入数据
        ArkModuleVersionDO arkModuleVersionDO = new ArkModuleVersionDO();
        arkModuleVersionDO.setCreateTime(DashboardUtil.now());
        arkModuleVersionDO.setModuleId(mId);
        arkModuleVersionDO.setModuleVersion(version);
        arkModuleVersionDO.setSourcePath(SofaDashboardConstants.SEPARATOR + version
                                         + SofaDashboardConstants.SEPARATOR + pluginName
                                         + SofaDashboardConstants.HORIZONTAL + version
                                         + "-ark-biz.jar");
        arkModuleVersionDO.setIsRelease((byte) 0);
        arkDao.insertModuleVersion(arkModuleVersionDO);
        return arkModuleVersionDO.getId();
    }

}
