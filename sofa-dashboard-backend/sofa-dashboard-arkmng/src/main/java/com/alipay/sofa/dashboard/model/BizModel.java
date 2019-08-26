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

import com.alipay.sofa.ark.spi.model.BizState;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.alipay.sofa.ark.spi.service.PriorityOrdered.DEFAULT_PRECEDENCE;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/17 10:09 PM
 * @since:
 **/
public class BizModel {

    private String      bizName;

    private String      bizVersion;

    private BizState    bizState;

    private String      mainClass;

    private String      webContextPath;

    private URL[]       classPath;

    private ClassLoader bizClassLoader;

    private int         priority               = DEFAULT_PRECEDENCE;

    private Set<String> denyImportPackages;

    private Set<String> denyImportPackageNodes = new HashSet<>();

    private Set<String> denyImportPackageStems = new HashSet<>();

    private Set<String> denyImportClasses;

    private Set<String> denyImportResources;

    private String      identity;

    public String getBizName() {
        return bizName;
    }

    public String getBizVersion() {
        return bizVersion;
    }

    public BizState getBizState() {
        return bizState;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getWebContextPath() {
        return webContextPath;
    }

    public ClassLoader getBizClassLoader() {
        return bizClassLoader;
    }

    public Set<String> getDenyImportPackages() {
        return denyImportPackages;
    }

    public Set<String> getDenyImportPackageNodes() {
        return denyImportPackageNodes;
    }

    public Set<String> getDenyImportPackageStems() {
        return denyImportPackageStems;
    }

    public Set<String> getDenyImportClasses() {
        return denyImportClasses;
    }

    public Set<String> getDenyImportResources() {
        return denyImportResources;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public void setBizVersion(String bizVersion) {
        this.bizVersion = bizVersion;
    }

    public void setBizState(BizState bizState) {
        this.bizState = bizState;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setWebContextPath(String webContextPath) {
        this.webContextPath = webContextPath;
    }

    public URL[] getClassPath() {
        return classPath;
    }

    public void setClassPath(URL[] classPath) {
        this.classPath = classPath;
    }

    public void setBizClassLoader(ClassLoader bizClassLoader) {
        this.bizClassLoader = bizClassLoader;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDenyImportPackages(Set<String> denyImportPackages) {
        this.denyImportPackages = denyImportPackages;
    }

    public void setDenyImportPackageNodes(Set<String> denyImportPackageNodes) {
        this.denyImportPackageNodes = denyImportPackageNodes;
    }

    public void setDenyImportPackageStems(Set<String> denyImportPackageStems) {
        this.denyImportPackageStems = denyImportPackageStems;
    }

    public void setDenyImportClasses(Set<String> denyImportClasses) {
        this.denyImportClasses = denyImportClasses;
    }

    public void setDenyImportResources(Set<String> denyImportResources) {
        this.denyImportResources = denyImportResources;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public static class ClassLoader {
        private String parent      = "";
        private String bizIdentity = "";
        private URL[]  urls        = new URL[0];

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getBizIdentity() {
            return bizIdentity;
        }

        public void setBizIdentity(String bizIdentity) {
            this.bizIdentity = bizIdentity;
        }

        public URL[] getUrls() {
            return urls;
        }

        public void setUrls(URL[] urls) {
            this.urls = urls;
        }

    }
}
