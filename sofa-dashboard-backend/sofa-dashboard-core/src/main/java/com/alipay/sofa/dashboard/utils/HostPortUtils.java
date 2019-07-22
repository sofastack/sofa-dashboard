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

import com.alipay.sofa.dashboard.client.model.common.HostAndPort;

/**
 * 实例地址id映射工具类
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public final class HostPortUtils {

    private static final String KEY = "g";

    /**
     * 工具类隐藏构造方法
     */
    private HostPortUtils() {
    }

    public static String toInstanceId(HostAndPort hostAndPort) {
        StringBuilder sb = new StringBuilder();
        String[] ips = hostAndPort.getHost().split("\\.");
        for (String ip : ips) {
            int i = Integer.parseInt(ip);
            sb.append(Integer.toHexString(i));
            sb.append(KEY);
        }
        sb.append(Integer.toHexString(hostAndPort.getPort()));
        return sb.toString();
    }

    public static HostAndPort fromInstanceId(String instanceId) {
        String[] split = instanceId.split(KEY);
        assert split.length == 5;
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < 4; index++) {
            sb.append(Integer.parseInt(split[index], 16));
            if (index != 3) {
                sb.append(".");
            }
        }

        String host = sb.toString();
        int port = Integer.parseInt(split[5], 16);
        return new HostAndPort(host, port);
    }

}
