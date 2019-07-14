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
package com.alipay.sofa.dashboard.application;

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.model.AppInfo;
import com.alipay.sofa.dashboard.spi.ApplicationManager;
import com.alipay.sofa.dashboard.utils.ObjectBytesUtil;
import com.alipay.sofa.dashboard.zookeeper.ZkCommandClient;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/18 2:21 PM
 * @since:
 **/
@Component
public class ZookeeperApplicationManager implements ApplicationManager, InitializingBean {

    private static final Logger LOGGER           = LoggerFactory
                                                     .getLogger(ZookeeperApplicationManager.class);

    @Autowired
    private ZkCommandClient     zkCommandClient;

    /**
     * 缓存一份应用名列表
     **/
    private static List<String> applicationNames = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 拉取应用名称
        fetchApplications();
        // 添加监听器
        TreeCache treeCache = new TreeCache(zkCommandClient.getCuratorClient(),
            SofaDashboardConstants.SOFA_BOOT_CLIENT_ROOT);
        addListener(treeCache);
    }

    /**
     * 从 zookeeper 上获取所有应用名称
     */
    private void fetchApplications() {
        // 清空
        applicationNames.clear();
        String appsPath = SofaDashboardConstants.SOFA_BOOT_CLIENT_ROOT
                          + SofaDashboardConstants.SOFA_BOOT_CLIENT_INSTANCE;
        try {
            Stat stat = zkCommandClient.getCuratorClient().checkExists().forPath(appsPath);
            if (stat != null) {
                List<String> appNames = zkCommandClient.getCuratorClient().getChildren()
                    .forPath(appsPath);
                applicationNames.addAll(appNames);
            }
        } catch (Throwable t) {
            LOGGER.error("Error to fetch applications from Zookeeper.", t);
        }
    }

    private void addListener(final TreeCache cache) throws Exception {
        TreeCacheListener listener = (client, event) -> {
            switch (event.getType()) {
                case NODE_ADDED:
                    doUpdateApplications(event);
                    break;
                case NODE_UPDATED:
                    doUpdateApplications(event);
                    break;
                case NODE_REMOVED:
                    doRemoveApplications(event);
                    break;
                case CONNECTION_LOST:
                    doRemoveApplications(event);
                    break;
                case CONNECTION_RECONNECTED:
                    // 服务端断线重连之后 ，重新从 zk 上去拉取应用信息
                    fetchApplications();
                    break;
                default:
                    break;
            }
        };
        cache.getListenable().addListener(listener);
        cache.start();
    }

    private void doUpdateApplications(TreeCacheEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dashboard client event type = {},data = {}", event.getType(),
                event.getData());
        }
        ChildData chileData = event.getData();
        try {
            byte[] data = event.getData().getData();
            if (data != null && chileData.getPath().contains(SofaDashboardConstants.COLON)) {
                AppInfo appInfo = ObjectBytesUtil.convertFromBytes(data, AppInfo.class);
                if (appInfo != null) {
                    String appName = appInfo.getAppName();
                    if (applicationNames.contains(appName)) {
                        return;
                    }
                    applicationNames.add(appName);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Error to update applications from zookeeper.", e);
        }
    }

    private void doRemoveApplications(TreeCacheEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dashboard client event type = {},data = {}", event.getType(),
                event.getData());
        }
        ChildData chileData = event.getData();
        if (chileData.getPath() == null) {
            return;
        }
        try {
            // 基于应用实例维度删除，standard path is /apps/instance/appName/ip:port
            if (chileData.getPath().contains(SofaDashboardConstants.COLON)) {
                String[] paths = chileData.getPath().split(SofaDashboardConstants.SEPARATOR);
                if (paths.length == 5) {
                    String applicationName = paths[3];
                    String applicationPath = SofaDashboardConstants.SOFA_INSTANCE + applicationName;
                    List<String> instances = zkCommandClient.getCuratorClient().getChildren()
                        .forPath(applicationPath);
                    // 当前应用名下已经没有实例了，删除 zookeeper 上的应用名节点
                    if (instances == null || instances.isEmpty()) {
                        // 这里会重新触发删除节点操作，延迟到基于应用维度删除
                        zkCommandClient.getCuratorClient().delete().forPath(applicationPath);
                    }
                }
            }
            // 基于应用维度删除
            else if (chileData.getPath().length() > SofaDashboardConstants.SOFA_INSTANCE.length()) {
                String applicationName = chileData.getPath().substring(
                    SofaDashboardConstants.SOFA_INSTANCE.length());
                if (applicationNames.contains(applicationName)) {
                    applicationNames.remove(applicationName);
                }
            }

        } catch (Throwable e) {
            LOGGER.error("Error to remove applications in memory", e);
        }
    }

    @Override
    public List<AppInfo> appInfoList(String applicationName) {
        List<AppInfo> appList = new ArrayList<>();
        if (applicationNames.contains(applicationName)){
            String applicationPath = SofaDashboardConstants.SOFA_INSTANCE + applicationName;
            try {
                List<String> instances = zkCommandClient.getCuratorClient().getChildren().forPath(applicationPath);
                instances.forEach(instance -> {
                    String appInstance = applicationPath + SofaDashboardConstants.SEPARATOR + instance;
                    try {
                        byte[] bytes = zkCommandClient.getCuratorClient().getData().forPath(appInstance);
                        AppInfo appInfo = ObjectBytesUtil.convertFromBytes(bytes, AppInfo.class);
                        appList.add(appInfo);
                    } catch (Exception e) {
                        LOGGER.error("Error to get app instance from Zookeeper.", e);
                    }
                });
            } catch (Throwable e) {
                LOGGER.error("Error to get appInfo list.", e);
            }
        }
        return appList;
    }

    @Override
    public List<String> applicationNames() {
        return applicationNames;
    }

    public String getAppState(String appName, String ip, String pluginName, String version) {
        // todo optimized get ark biz state
        return "";
    }
}
