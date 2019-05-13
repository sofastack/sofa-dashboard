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

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import org.springframework.util.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 19/1/10 下午5:02
 * @since:
 **/
public class DashboardUtil {

    public static final String            KEY        = "g";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    public static String getCurrentDataKey() {
        return dateFormat.format(now());
    }

    /**
     * Extract value from map ,if null return empty String
     * @param map
     * @param key
     * @return
     */
    public static String getEmptyStringIfNull(Map map, String key) {
        if (map == null || map.size() <= 0) {
            return SofaDashboardConstants.EMPTY;
        }
        Object valueObject = map.get(key);
        String valueStr;
        try {
            valueStr = (String) valueObject;
        } catch (Throwable throwable) {
            return SofaDashboardConstants.EMPTY;
        }
        return StringUtils.isEmpty(valueStr) ? SofaDashboardConstants.EMPTY : valueStr;
    }

    /**
     * Extract value from map ,if null return defaultValue
     * @param map
     * @param key
     * @return
     */
    public static int getDefaultIfNull(Map map, String key, int defaultValue) {
        if (map == null || map.size() <= 0) {
            return defaultValue;
        }
        Object valueObject = map.get(key);
        int valueStr;
        try {
            valueStr = (Integer) valueObject;
        } catch (Throwable throwable) {
            return defaultValue;
        }
        return valueStr;
    }

    public static String simpleEncode(String host, int port) {
        if (SofaDashboardConstants.LOCALHOST.equals(host)) {
            host = SofaDashboardConstants.LOCALHOST_IP;
        }
        StringBuilder sb = new StringBuilder();
        String[] ips = host.split("\\.");
        for (String ip : ips) {
            int i = Integer.parseInt(ip);
            sb.append(Integer.toHexString(i));
            sb.append(KEY);
        }
        sb.append(Integer.toHexString(port));
        return sb.toString();
    }

    public static String simpleDecode(String encodeAddress) {
        StringBuilder sb = new StringBuilder();
        if (encodeAddress.contains(KEY)) {
            String[] split = encodeAddress.split(KEY);
            if (split.length == 5) {
                for (int index = 0; index < 4; index++) {
                    sb.append(Integer.parseInt(split[index], 16));
                    if (index != 3) {
                        sb.append(".");
                    }
                }
                sb.append(SofaDashboardConstants.COLON).append(Integer.parseInt(split[4], 16));
            }
        }
        return sb.toString();
    }

    public static String convertToString(List<String> source){
        if(source==null || source.isEmpty()){
            return SofaDashboardConstants.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        source.forEach((item)-> sb.append(item).append(","));
        return sb.substring(0,sb.toString().length()-1);
    }
}
