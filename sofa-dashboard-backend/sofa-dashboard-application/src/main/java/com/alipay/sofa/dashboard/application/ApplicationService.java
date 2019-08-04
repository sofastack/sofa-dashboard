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
package com.alipay.sofa.dashboard.application;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/14 10:49 AM
 * @since:
 **/

import com.alipay.sofa.dashboard.model.AppInfo;
import com.alipay.sofa.dashboard.model.ApplicationInfo;
import com.alipay.sofa.dashboard.spi.ApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/11 2:45 PM
 * @since:
 **/
@Service
public class ApplicationService {

    @Autowired
    private ApplicationManager applicationManager;

    public List<ApplicationInfo> applications() {
        List<ApplicationInfo> result = new ArrayList<>();
        List<String> applicationNames = applicationManager.applicationNames();
        for (String applicationName : applicationNames) {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setApplicationCount(applicationManager.appInfoList(applicationName)
                .size());
            applicationInfo.setApplicationName(applicationName);
            result.add(applicationInfo);
        }
        return result;
    }

    public List<AppInfo> fetchAllInstanceByName(String applicationName) {
        return applicationManager.appInfoList(applicationName);
    }

    public List<ApplicationInfo> applicationsByMatch(String applicationName) {
        List<ApplicationInfo> result = new ArrayList<>();
        applications().forEach((item) -> {
            if (item.getApplicationName().contains(applicationName)){
                result.add(item);
            }
        });
        return result;
    }
}
