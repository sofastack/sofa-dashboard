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

import com.alibaba.fastjson.JSONObject;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/16 8:52 PM
 * @since:
 **/
public class FastJsonUtils {

    public static String getString(JSONObject json, String path) {
        return get(json, path, String.class);
    }

    public static Integer getInteger(JSONObject json, String path) {
        return get(json, path, Integer.class);
    }

    public static <T> T get(JSONObject json, String path, Class<T> t) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] nodes = path.split("/");
        JSONObject tmp = json;
        for (int i = 0; i < nodes.length - 1; i++) {
            tmp = tmp.getJSONObject(nodes[i]);
        }
        String key = nodes[nodes.length - 1];
        if (t == String.class) {
            return (T) tmp.getString(key);
        } else if (t == Integer.class) {
            return (T) tmp.getInteger(key);
        }
        return (T) tmp.get(key);
    }
}