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
import com.alipay.sofa.dashboard.client.model.info.InfoDescriptor;
import com.alipay.sofa.dashboard.model.TreeNode;

import java.util.Map;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public final class TreeNodeConverter {

    private TreeNodeConverter() {
    }

    public static TreeNode convert(EnvironmentDescriptor descriptor) {
        if (descriptor == null) {
            return TreeNode.create("Environment", null);
        }

        return TreeNode.create("Environment", null)
            .child("activeProfiles", descriptor.getActiveProfiles().toString())
            .child("propertySources", (propertySourcesTree) -> {

                for (PropertySourceDescriptor propertySource : descriptor.getPropertySources()) {
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
        if (descriptor == null) {
            return TreeNode.create("Information", null);
        }

        TreeNode root = TreeNode.create("Information", null);
        convertMap(root, descriptor.getInfo());
        return root;
    }

    private static void convertMap(TreeNode root, Map<String, Object> childMap) {
        if (childMap == null || childMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : childMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                TreeNode next = TreeNode.create(entry.getKey(), null);
                convertMap(next, (Map<String, Object>) entry.getValue());
                root.child(next);
            } else {
                root.child(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }
}
