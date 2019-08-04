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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode implements Serializable {

    private static final int serialVersionUID = 0x11;

    private String           name;

    private String           value;

    private List<TreeNode>   children;

    public TreeNode() {
    }

    private TreeNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static TreeNode create(String name, String value) {
        return new TreeNode(name, value);
    }

    public TreeNode child(TreeNode node) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(node);
        return this;
    }

    public TreeNode child(String name, String value) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(new TreeNode(name, value));
        return this;
    }

    public TreeNode child(String name, Consumer<TreeNode> childrenGenerator) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        TreeNode next = new TreeNode(name, null);
        childrenGenerator.accept(next);
        this.children.add(next);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}