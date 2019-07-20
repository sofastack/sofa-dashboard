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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/16 11:28 AM
 * @since:
 **/
public class SofaDashboardUtil {

    /**
     * 从 map 中解析状态数据
     * @param result
     * @param pluginName
     * @param version
     * @return
     */
    public static String parseStateFromMapJSON(Map result, String pluginName, String version) {
        if (result == null || result.isEmpty()) {
            return "";
        }
        if (result.get(SofaDashboardConstants.CODE) instanceof String) {
            String code = (String) result.get(SofaDashboardConstants.CODE);
            if (SofaDashboardConstants.SUCCESS.equals(code)) {
                Object bizInfos = result.get(SofaDashboardConstants.BIZ_INFOS);
                if (bizInfos instanceof ArrayList) {
                    List<Map> bizInfoList = (List<Map>) bizInfos;
                    for (Map item : bizInfoList) {
                        if (item.containsKey(SofaDashboardConstants.BIZ_NAME)
                            && item.containsKey(SofaDashboardConstants.BIZ_VERSION)) {
                            if (pluginName.equals(item.get(SofaDashboardConstants.BIZ_NAME))
                                && version.equals(item.get(SofaDashboardConstants.BIZ_VERSION))) {
                                return item.get(SofaDashboardConstants.BIZ_STATE) == null ? ""
                                    : item.get(SofaDashboardConstants.BIZ_STATE).toString();
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
