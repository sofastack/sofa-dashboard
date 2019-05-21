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
package com.alipay.sofa.dashboard.registry;

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.listener.RegistryDataChangeListener;
import com.alipay.sofa.dashboard.listener.zookeeper.RootNodeChangeListener;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.core.exception.SofaRpcRuntimeException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.alipay.sofa.rpc.common.utils.StringUtils.CONTEXT_SEP;

/**
 * @author bystander
 * @version $Id: ZookeeperAdminRegistry.java, v 0.1 2018年12月11日 17:35 bystander Exp $
 */
public class ZookeeperAdminRegistry implements AdminRegistry {

    private static final Logger    LOGGER = LoggerFactory.getLogger(ZookeeperAdminRegistry.class);

    /**
     * 注册中心服务配置
     */
    protected RegistryConfig       registryConfig;

    /**
     * Zookeeper zkClient
     */
    private CuratorFramework       zkClient;

    /**
     * Root path of registry data
     */
    private String                 rootPath;

    @Autowired
    private RootNodeChangeListener rootNodeChangeListener;

    @Override
    public boolean start(RegistryConfig registryConfig) {

        if (zkClient != null) {
            return true;
        }
        this.registryConfig = registryConfig;
        // xxx:2181,yyy:2181/path1/path2
        String addressInput = registryConfig.getAddress();
        if (StringUtils.isEmpty(addressInput)) {
            throw new SofaRpcRuntimeException("Address of zookeeper registry is empty.");
        }
        int idx = addressInput.indexOf(CONTEXT_SEP);
        String address; // IP地址
        if (idx > 0) {
            address = addressInput.substring(0, idx);
            rootPath = addressInput.substring(idx);
            if (!rootPath.endsWith(CONTEXT_SEP)) {
                // 保证以"/"结尾
                rootPath += CONTEXT_SEP;
            }
        } else {
            address = addressInput;
            rootPath = CONTEXT_SEP;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.builder().connectString(address)
                .sessionTimeoutMs(registryConfig.getConnectTimeout() * 3)
                .connectionTimeoutMs(registryConfig.getConnectTimeout()).canBeReadOnly(false)
                .retryPolicy(retryPolicy).defaultData(null).build();

        zkClient.getConnectionStateListenable().addListener((client, newState) -> {

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("reconnect to zookeeper,recover provider and consumer data");
            }
            if (newState == ConnectionState.RECONNECTED) {
                recoverRegistryData();
            }
        });

        if (zkClient == null) {
            LOGGER.warn("Start zookeeper registry must be do init first!");
            return false;
        }
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            return true;
        }
        try {
            zkClient.start();
            LOGGER.info("Zookeeper client start success,address is {}", address);
        } catch (Exception e) {
            throw new SofaRpcRuntimeException("Failed to start zookeeper zkClient", e);
        }
        return zkClient.getState() == CuratorFrameworkState.STARTED;
    }

    private void recoverRegistryData() {
        //TODO 碧远
    }

    @Override
    public void subscribe(String group, RegistryDataChangeListener listener) {
        // 注册Consumer节点
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient,
                SofaDashboardConstants.SEPARATOR + group, true);

            pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);

            pathChildrenCache.getListenable().addListener(rootNodeChangeListener);
        } catch (Exception e) {
            throw new SofaRpcRuntimeException("Failed to register consumer to zookeeperRegistry!",
                e);
        }

    }
}