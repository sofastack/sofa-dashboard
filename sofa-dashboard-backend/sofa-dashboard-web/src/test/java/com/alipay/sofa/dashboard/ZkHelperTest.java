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
package com.alipay.sofa.dashboard;

import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.impl.ZkHelper;
import com.alipay.sofa.dashboard.model.AppUnitModel;
import com.alipay.sofa.dashboard.model.ClientResponseModel;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/22 2:06 PM
 * @since:
 **/
public class ZkHelperTest extends AbstractTestBase {

    String   arkAppBasePath = SofaDashboardConstants.SOFA_ARK_ROOT
                              + SofaDashboardConstants.SEPARATOR + "ark-master/127.0.0.1";
    @Autowired
    ZkHelper zkHelper;

    @Before
    public void before() throws Exception {
        restTemplate = new RestTemplate();
        // 初始化 zk 节点
        client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(
            1000, 3));
        client.start();
        client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
            .forPath("/apps/biz/ark-master/127.0.0.1", bizState.getBytes());
        client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
            .forPath(arkAppBasePath);
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void testGetArkAppFromZookeeper() throws Exception {
        List<AppUnitModel> arkAppFromZookeeper = zkHelper.getArkAppFromZookeeper("ark-master",
            "ark-master", "1.0.0");
        Assert.assertTrue(arkAppFromZookeeper.size() == 1);
        Assert.assertTrue(arkAppFromZookeeper.get(0).getStatus().equalsIgnoreCase("activated"));
    }

    @Test
    public void testGetBizState() throws Exception {
        ClientResponseModel bizState = zkHelper.getBizState("ark-master", "127.0.0.1");
        Assert.assertTrue(bizState.getCode().name().equalsIgnoreCase("SUCCESS"));
    }

    private static String bizState = "{\n"
                                     + "    \"message\": \"Biz count=1\\nbizName=ark-master, bizVersion=1.0.0, bizState=activated\\n\",\n"
                                     + "    \"code\": \"SUCCESS\",\n"
                                     + "    \"bizInfos\": [\n"
                                     + "        {\n"
                                     + "            \"bizName\": \"ark-master\",\n"
                                     + "            \"bizVersion\": \"1.0.0\",\n"
                                     + "            \"bizState\": \"ACTIVATED\",\n"
                                     + "            \"mainClass\": \"io.sofastack.ark.master.MasterApplication\",\n"
                                     + "            \"webContextPath\": \"/\",\n"
                                     + "            \"priority\": 100,\n"
                                     + "            \"denyImportPackages\": [],\n"
                                     + "            \"denyImportPackageNodes\": [],\n"
                                     + "            \"denyImportPackageStems\": [],\n"
                                     + "            \"denyImportClasses\": [],\n"
                                     + "            \"denyImportResources\": [],\n"
                                     + "            \"classPath\": [\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/netty-3.7.0.Final.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/aopalliance-1.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-compatible-springboot2-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-datatype-jsr310-2.9.7.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-archive-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/validation-api-2.0.1.Final.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-web-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/logback-classic-1.2.3.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/javax.annotation-api-1.3.2.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-el-9.0.12.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-expression-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-beans-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guice-3.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jboss-logging-3.3.2.Final.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-json-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-annotations-2.9.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/infra-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-aop-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-client-2.9.1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-module-parameter-names-2.9.7.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guides-dashboard-facade-1.0.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-websocket-9.0.12.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-compatible-springboot1-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-databind-2.9.7.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-api-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-webmvc-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-core-9.0.12.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/micrometer-core-1.1.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-recipes-2.9.1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-common-springboot-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-api-2.11.1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/commons-io-2.5.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-web-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-jcl-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jline-0.9.94.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-common-tools-1.0.18.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-dashboard-client-1.1.0-SNAPSHOT.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-common-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/classmate-1.4.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/fastjson-1.2.47.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/HdrHistogram-2.1.9.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-actuator-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/zookeeper-3.4.6.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-framework-2.9.1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/logback-core-1.2.3.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-core-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-core-2.9.7.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-actuator-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-tomcat-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/runtime-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-datatype-jdk8-2.9.7.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/hessian-3.3.6.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-1.2.16.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-springboot-starter-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-exception-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/javax.inject-1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/healthcheck-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-to-slf4j-2.11.1.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-support-starter-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guava-18.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/slf4j-api-1.7.25.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/LatencyUtils-2.0.3.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-logging-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/hibernate-validator-6.0.13.Final.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log-sofa-boot-starter-1.0.18.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-context-5.1.2.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/snakeyaml-1.23.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-spi-0.6.0.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-actuator-autoconfigure-2.1.0.RELEASE.jar!/\",\n"
                                     + "                \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jul-to-slf4j-1.7.25.jar!/\"\n"
                                     + "            ],\n"
                                     + "            \"identity\": \"ark-master:1.0.0\",\n"
                                     + "            \"bizClassLoader\": {\n"
                                     + "                \"parent\": null,\n"
                                     + "                \"bizIdentity\": \"ark-master:1.0.0\",\n"
                                     + "                \"urls\": [\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/netty-3.7.0.Final.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/aopalliance-1.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-compatible-springboot2-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-datatype-jsr310-2.9.7.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-archive-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/validation-api-2.0.1.Final.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-web-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/logback-classic-1.2.3.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/javax.annotation-api-1.3.2.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-el-9.0.12.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-expression-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-beans-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guice-3.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jboss-logging-3.3.2.Final.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-json-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-annotations-2.9.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/infra-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-aop-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-client-2.9.1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-module-parameter-names-2.9.7.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guides-dashboard-facade-1.0.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-websocket-9.0.12.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-compatible-springboot1-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-databind-2.9.7.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-api-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-webmvc-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/tomcat-embed-core-9.0.12.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/micrometer-core-1.1.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-recipes-2.9.1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-common-springboot-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-api-2.11.1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/commons-io-2.5.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-web-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-jcl-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jline-0.9.94.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-common-tools-1.0.18.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-dashboard-client-1.1.0-SNAPSHOT.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-common-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/classmate-1.4.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/fastjson-1.2.47.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/HdrHistogram-2.1.9.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-actuator-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/zookeeper-3.4.6.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/curator-framework-2.9.1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/logback-core-1.2.3.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-core-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-core-2.9.7.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-actuator-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-tomcat-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/runtime-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jackson-datatype-jdk8-2.9.7.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/hessian-3.3.6.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-1.2.16.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-springboot-starter-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-exception-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/javax.inject-1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/healthcheck-sofa-boot-starter-3.1.4.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log4j-to-slf4j-2.11.1.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-support-starter-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/guava-18.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/slf4j-api-1.7.25.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/LatencyUtils-2.0.3.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-starter-logging-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/hibernate-validator-6.0.13.Final.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/log-sofa-boot-starter-1.0.18.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-context-5.1.2.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/snakeyaml-1.23.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/sofa-ark-spi-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/spring-boot-actuator-autoconfigure-2.1.0.RELEASE.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/biz/guides-dashboard-ark-master-1.0.0-ark-biz.jar!/lib/jul-to-slf4j-1.7.25.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/plugin/web-ark-plugin-0.6.0.jar!/\",\n"
                                     + "                    \"jar:file:/sofastack-dashboard-guides/guides-dashboard-ark-master/target/guides-dashboard-ark-master-1.0.0.jar!/SOFA-ARK/plugin/config-ark-plugin-0.6.0.jar!/\"\n"
                                     + "                ]\n" + "            }\n" + "        }\n"
                                     + "    ]\n" + "}";
}
