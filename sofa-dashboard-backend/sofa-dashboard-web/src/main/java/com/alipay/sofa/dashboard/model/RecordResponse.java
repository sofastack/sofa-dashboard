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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class RecordResponse implements Serializable {

    private static final int     serialVersionUID = 0x11;

    private final List<Overview> overview         = new ArrayList<>();

    private TreeNode             detail;

    private RecordResponse(Builder builder) {
        this.overview.addAll(builder.overview);
        setDetail(builder.detail);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public TreeNode getDetail() {
        return detail;
    }

    public void setDetail(TreeNode detail) {
        this.detail = detail;
    }

    public List<Overview> getOverview() {
        return overview;
    }

    public static class Overview implements Serializable {

        private static final int serialVersionUID = 0x11;

        private String           key;

        private String           value;

        public Overview() {
        }

        public Overview(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final class Builder {

        private final List<Overview> overview = new ArrayList<>();

        private TreeNode             detail;

        private Builder() {
        }

        public Builder overview(String key, String value) {
            this.overview.add(new Overview(key, value));
            return this;
        }

        public Builder overview(Map<String, Object> overviews) {
            overviews.forEach((k, v) -> this.overview.add(new Overview(k, String.valueOf(v))));
            return this;
        }

        public Builder detail(TreeNode detail) {
            this.detail = detail;
            return this;
        }

        public RecordResponse build() {
            return new RecordResponse(this);
        }
    }
}
