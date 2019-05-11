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

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/10 3:31 PM
 * @since:
 **/
public class MappingsInfo {

    private List<ServletInfo>        servlets;
    private List<HandlerFilterInfo>  servletFilters;
    private List<HandlerMappingInfo> dispatcherServlet;

    public List<ServletInfo> getServlets() {
        return servlets;
    }

    public void setServlets(List<ServletInfo> servlets) {
        this.servlets = servlets;
    }

    public List<HandlerFilterInfo> getServletFilters() {
        return servletFilters;
    }

    public void setServletFilters(List<HandlerFilterInfo> servletFilters) {
        this.servletFilters = servletFilters;
    }

    public List<HandlerMappingInfo> getDispatcherServlet() {
        return dispatcherServlet;
    }

    public void setDispatcherServlet(List<HandlerMappingInfo> dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    public static class ServletInfo {

        private String mappings;
        private String name;
        private String className;

        public String getMappings() {
            return mappings;
        }

        public void setMappings(String mappings) {
            this.mappings = mappings;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class HandlerFilterInfo {

        private String urlPatternMappings;
        private String servletNameMappings;
        private String name;
        private String className;

        public String getUrlPatternMappings() {
            return urlPatternMappings;
        }

        public void setUrlPatternMappings(String urlPatternMappings) {
            this.urlPatternMappings = urlPatternMappings;
        }

        public String getServletNameMappings() {
            return servletNameMappings;
        }

        public void setServletNameMappings(String servletNameMappings) {
            this.servletNameMappings = servletNameMappings;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class HandlerMappingInfo {

        private String predicate;
        private String handler;
        private String methods;
        private String paramsType;
        private String responseType;

        public String getPredicate() {
            return predicate;
        }

        public void setPredicate(String predicate) {
            this.predicate = predicate;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String getMethods() {
            return methods;
        }

        public void setMethods(String methods) {
            this.methods = methods;
        }

        public String getParamsType() {
            return paramsType;
        }

        public void setParamsType(String paramsType) {
            this.paramsType = paramsType;
        }

        public String getResponseType() {
            return responseType;
        }

        public void setResponseType(String responseType) {
            this.responseType = responseType;
        }
    }
}
