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
import com.alipay.sofa.dashboard.model.ArkModuleVersionDO;
import com.alipay.sofa.dashboard.model.ArkPluginDO;
import com.alipay.sofa.dashboard.model.CommandRequest;
import com.alipay.sofa.dashboard.spi.CommandPushManager;
import com.alipay.sofa.dashboard.zookeeper.ZkCommandClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/2/12 5:21 PM
 * @since:
 **/
@Service
public class ZkCommandPushManager implements CommandPushManager {

    private static final Logger    LOGGER         = LoggerFactory
                                                      .getLogger(ZkCommandPushManager.class);

    private volatile AtomicBoolean isSyncAppState = new AtomicBoolean(false);

    @Autowired
    private ZkCommandClient        zkCommandClient;

    @Autowired
    private ArkDao                 arkDao;

    @Override
    public void pushCommand(CommandRequest commandRequest) {
        checkRoot();
        // 如果是按照应用维度推送命令，则直接放在 /appName 节点数据中
        if (commandRequest.getDimension().equals(SofaDashboardConstants.APP)) {
            String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR + commandRequest.getAppName();
            ArkOperation data = getData(commandRequest);
            try {
                Stat stat = getClient().checkExists().forPath(path);
                if (stat == null) {
                    getClient().create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, convertConfig(data, "").getBytes());
                } else {
                    String oldData = new String(getClient().getData().forPath(path));
                    getClient().setData().forPath(path, convertConfig(data, oldData).getBytes());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to install biz module via app dimension.", e);
                throw new RuntimeException(e);
            }
        } else {
            // 如果是按照IP维度推送，则放在 /ip 节点数据中
            List<String> targetHosts = commandRequest.getTargetHost();
            targetHosts.forEach(targetHost -> {
                String path = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR + commandRequest.getAppName() + SofaDashboardConstants.SEPARATOR + targetHost;
                ArkOperation data = getData(commandRequest);

                try {
                    if (getClient().checkExists().forPath(path) == null) {
                        getClient().create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, convertConfig(data, "").getBytes());
                    } else {
                        // 这里应该ark在首次从应用维度初始化之后向自己的节点写入状态数据
                        // 为了保持兼容，这里先从应用维度解析然后进行 merge
                        if (isSyncAppState.compareAndSet(false, true)) {
                            String appPath = SofaDashboardConstants.SOFA_ARK_ROOT + SofaDashboardConstants.SEPARATOR + commandRequest.getAppName();
                            String appOldData = new String(getClient().getData().forPath(appPath));
                            getClient().setData().forPath(path, convertConfig(data, appOldData).getBytes());
                        } else {
                            String oldData = new String(getClient().getData().forPath(path));
                            getClient().setData().forPath(path, convertConfig(data, oldData).getBytes());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to install biz module via ip dimension.", e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private String convertConfig(ArkOperation operation, String oldData) {
        List<ArkOperation> arkOperations = new ArrayList<>();
        String[] stateInfo = oldData.split(SofaDashboardConstants.SEMICOLON);
        for (String info : stateInfo) {
            if (StringUtils.isEmpty(info)) {
                continue;
            }
            arkOperations.add(convertArkOperation(info));
        }
        if (SofaDashboardConstants.INSTALL.equals(operation.getCommand())) {
            return handleInstall(operation.getBizName(), operation.getBizVersion(),
                operation.getParameters(), arkOperations);
        } else if (SofaDashboardConstants.SWITCH.equals(operation.getCommand())) {
            return handleSwitch(operation.getBizName(), operation.getBizVersion(), arkOperations);
        } else {
            return handleUninstall(operation.getBizName(), operation.getBizVersion(), arkOperations);
        }
    }

    private String handleInstall(String bizName, String bizVersion, String parameters,
                                 List<ArkOperation> current) {
        String state = SofaDashboardConstants.ACTIVATED;
        boolean isExist = false;
        for (ArkOperation arkOperation : current) {
            if (arkOperation.getBizName().equals(bizName)
                && arkOperation.getBizVersion().equals(bizVersion)) {
                isExist = true;
            } else if (arkOperation.getBizName().equals(bizName)
                       && arkOperation.getState().equals(SofaDashboardConstants.ACTIVATED)) {
                state = SofaDashboardConstants.DEACTIVATED;
            }
        }
        if (!isExist) {
            ArkOperation operation = new ArkOperation();
            operation.setBizName(bizName);
            operation.setBizVersion(bizVersion);
            operation.setState(state);
            operation.setParameters(parameters);
            current.add(operation);
        }
        return covertToConfig(current);
    }

    private String handleUninstall(String bizName, String bizVersion, List<ArkOperation> current) {
        for (ArkOperation arkOperation : current) {
            if (arkOperation.getBizName().equals(bizName)
                && arkOperation.getBizVersion().equals(bizVersion)) {
                current.remove(arkOperation);
                break;
            }
        }
        return covertToConfig(current);
    }

    private String handleSwitch(String bizName, String bizVersion, List<ArkOperation> current) {
        for (ArkOperation arkOperation : current) {
            if (arkOperation.getBizName().equals(bizName)
                && arkOperation.getBizVersion().equals(bizVersion)) {
                arkOperation.setState(SofaDashboardConstants.ACTIVATED);
            } else if (arkOperation.getBizName().equals(bizName)) {
                arkOperation.setState(SofaDashboardConstants.DEACTIVATED);
            }
        }
        return covertToConfig(current);
    }

    private String covertToConfig(List<ArkOperation> arkOperations) {
        StringBuilder sb = new StringBuilder();
        for (ArkOperation arkOperation : arkOperations) {
            sb.append(arkOperation.getBizName()).append(SofaDashboardConstants.COLON)
                .append(arkOperation.getBizVersion()).append(SofaDashboardConstants.COLON)
                .append(arkOperation.getState());
            if (!StringUtils.isEmpty(arkOperation.getParameters())) {
                sb.append(SofaDashboardConstants.Q_MARK).append(arkOperation.getParameters());
            }
            sb.append(SofaDashboardConstants.SEMICOLON);
        }
        String ret = sb.toString();
        if (ret.endsWith(SofaDashboardConstants.SEMICOLON)) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    private CuratorFramework getClient() {
        return zkCommandClient.getCuratorClient();
    }

    private ArkOperation getData(CommandRequest commandRequest) {
        ArkOperation arkOperation = new ArkOperation();
        arkOperation.setCommand(commandRequest.getCommand());
        arkOperation.setBizName(commandRequest.getPluginName());
        arkOperation.setBizVersion(commandRequest.getPluginVersion());
        arkOperation.setParameters("bizUrl=" + getBizPluginFileUrl(commandRequest));
        return arkOperation;
    }

    private String getBizPluginFileUrl(CommandRequest commandRequest) {
        List<ArkPluginDO> arkPluginList = arkDao.queryModuleInfoByNameStrict(commandRequest
            .getPluginName());
        if (arkPluginList.size() > 1) {
            throw new RuntimeException("Multiple modules with the same name coexist.");
        }
        if (arkPluginList.size() == 1) {
            ArkPluginDO arkPlugin = arkPluginList.get(0);
            ArkModuleVersionDO arkModuleVersion = arkDao.queryByModuleIdAndModuleVersion(
                arkPlugin.getId(), commandRequest.getPluginVersion());
            return arkModuleVersion.getSourcePath();
        }
        return null;
    }

    private void checkRoot() {
        try {
            if (getClient().checkExists().forPath(SofaDashboardConstants.SOFA_ARK_ROOT) == null) {
                getClient().create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(SofaDashboardConstants.SOFA_ARK_ROOT, null);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to check zookeeper root path.", e);
        }
    }

    private ArkOperation convertArkOperation(String config) {
        int idx = config.indexOf(SofaDashboardConstants.Q_MARK);
        String parameter = (idx == -1) ? "" : config.substring(idx + 1);
        String[] meta = (idx == -1) ? config.split(SofaDashboardConstants.COLON) : config
            .substring(0, idx).split(SofaDashboardConstants.COLON);
        ArkOperation arkOperation = new ArkOperation();
        arkOperation.setParameters(parameter);
        arkOperation.setBizName(meta[0]);
        arkOperation.setBizVersion(meta[1]);
        arkOperation.setState(meta[2]);
        return arkOperation;
    }

    class ArkOperation {
        String command;
        String bizName;
        String bizVersion;
        String parameters;
        String state;

        private String getState() {
            return state;
        }

        private void setState(String state) {
            this.state = state;
        }

        private String getCommand() {
            return command;
        }

        private void setCommand(String command) {
            this.command = command;
        }

        private String getBizName() {
            return bizName;
        }

        private void setBizName(String bizName) {
            this.bizName = bizName;
        }

        private String getBizVersion() {
            return bizVersion;
        }

        private void setBizVersion(String bizVersion) {
            this.bizVersion = bizVersion;
        }

        private String getParameters() {
            return parameters;
        }

        private void setParameters(String parameters) {
            this.parameters = parameters;
        }
    }
}
