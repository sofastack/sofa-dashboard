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
package com.alipay.sofa.dashboard.utils;

import com.alipay.sofa.dashboard.client.model.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.client.model.env.PropertySourceDescriptor;
import com.alipay.sofa.dashboard.client.model.env.PropertyValueDescriptor;
import com.alipay.sofa.dashboard.client.model.health.HealthDescriptor;
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.client.model.logger.LoggersDescriptor;
import com.alipay.sofa.dashboard.client.model.mappings.MappingsDescriptor;
import com.alipay.sofa.dashboard.model.TreeNode;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public final class TreeNodeConverter {

    private TreeNodeConverter() {
    }

    public static TreeNode convert(EnvironmentDescriptor descriptor) {
        TreeNode root = TreeNode.create("Environment", null);
        if (descriptor == null) {
            return root;
        }

        Set<String> propFilters = Sets.newHashSet("systemProperties", "systemEnvironment");

        return root
            .child("activeProfiles", descriptor.getActiveProfiles().toString())
            .child("propertySources", (propertySourcesTree) -> {
                for (PropertySourceDescriptor propertySource : descriptor.getPropertySources()) {
                    if (propFilters.contains(propertySource.getName())) {
                        continue;
                    }
                    propertySourcesTree.child(propertySource.getName(), (propertyValueTree) -> {

                        for (Map.Entry<String, PropertyValueDescriptor> entry :
                            propertySource.getProperties().entrySet()) {
                            propertyValueTree.child(entry.getKey(),
                                String.valueOf(entry.getValue().getValue()));
                        }
                    });
                }
            });
    }

    public static TreeNode convert(InfoDescriptor descriptor) {
        TreeNode root = TreeNode.create("Information", null);
        if (descriptor == null) {
            return root;
        }
        convertMap(root, descriptor.getInfo());
        return root;
    }

    public static TreeNode convert(LoggersDescriptor descriptor) {
        TreeNode root = TreeNode.create("Loggers", null);
        if (descriptor == null) {
            return root;
        }
        return root
            .child("levels", descriptor.getLevels().toString())
            .child("loggers", (loggersTree) ->
                descriptor.getLoggers().forEach((name, item) ->
                    loggersTree.child(name, item.getEffectiveLevel())));
    }

    public static TreeNode convert(MappingsDescriptor descriptor) {
        TreeNode root = TreeNode.create("Mappings", null);
        if (descriptor == null || descriptor.getMappings() == null) {
            return root;
        }

        descriptor.getMappings().forEach((name, mapping) -> {
            root.child(name, (tree) -> {
                tree.child("dispatchServlet", (dispatchServletTree) -> {
                    // dispatch servlet
                    mapping.getDispatcherServlet().forEach((dispatchServlet) ->
                        dispatchServletTree.child(dispatchServlet.getHandler(),
                            dispatchServlet.getPredicate()));
                });

                tree.child("servletFilter", (servletFilterTree) -> {
                    // servlet filter
                    mapping.getServletFilters().forEach((servletFilter) ->
                        servletFilterTree.child(servletFilter.getName(),
                            servletFilter.getUrlPatternMappings()));
                });

                tree.child("servlet", (servletTree) -> {
                    // servlet
                    mapping.getServlets().forEach((servlet) ->
                        servletTree.child(servlet.getClassName(),
                            servlet.getMappings()));
                });
            });
        });

        return root;
    }

    public static TreeNode convert(HealthDescriptor descriptor) {
        TreeNode root = TreeNode.create("Health", descriptor.getStatus());
        convertMap(root, descriptor.getDetails());
        return root;
    }

    private static void convertMap(TreeNode root, Map<String, Object> childMap) {
        if (childMap == null || childMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : childMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                TreeNode next = TreeNode.create(entry.getKey(), null);
                //noinspection unchecked
                convertMap(next, (Map<String, Object>) entry.getValue());
                root.child(next);
            } else {
                root.child(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }
}
