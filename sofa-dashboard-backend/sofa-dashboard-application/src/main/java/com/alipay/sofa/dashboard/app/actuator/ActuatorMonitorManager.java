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
package com.alipay.sofa.dashboard.app.actuator;

import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.enums.ActuatorPathEnum;
import com.alipay.sofa.dashboard.model.monitor.DetailThreadInfo;
import com.alipay.sofa.dashboard.model.monitor.DetailsItem;
import com.alipay.sofa.dashboard.model.monitor.EnvironmentInfo;
import com.alipay.sofa.dashboard.model.monitor.HealthInfo;
import com.alipay.sofa.dashboard.model.monitor.LoggersInfo;
import com.alipay.sofa.dashboard.model.monitor.MappingsInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryNonHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.MetricsInfo;
import com.alipay.sofa.dashboard.model.monitor.ThreadDumpInfo;
import com.alipay.sofa.dashboard.spi.MonitorManager;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import com.alipay.sofa.dashboard.utils.FixedQueue;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 10:34 PM
 * @since:
 **/
@Component
public class ActuatorMonitorManager implements MonitorManager {

    private RestTemplate                                     restTemplate       = new RestTemplate();

    public static Map<String, FixedQueue<DetailThreadInfo>>  cacheDetailThreads = new ConcurrentHashMap<>();

    public static Map<String, FixedQueue<MemoryHeapInfo>>    cacheHeapMemory    = new ConcurrentHashMap<>();

    public static Map<String, FixedQueue<MemoryNonHeapInfo>> cacheNonHeapMemory = new ConcurrentHashMap<>();

    @Override
    public EnvironmentInfo fetchEnvironment(Object source) {
        Map envMap = doRequest(source,ActuatorPathEnum.ENV.getPath(),restTemplate);
        EnvironmentInfo environmentInfo = new EnvironmentInfo();
        if (envMap.isEmpty()){
            return environmentInfo;
        }
        Object activeProfiles = envMap.get("activeProfiles");
        if (activeProfiles instanceof List){
            environmentInfo.setActiveProfiles((List<String>) activeProfiles);
        }else {
            environmentInfo.setActiveProfiles(new ArrayList<>());
        }
        List<EnvironmentInfo.PropertySourceDescriptor> propertySources = new ArrayList<>();
        Object propertyList = envMap.get("propertySources");
        if (propertyList instanceof List){
            List<Map<String,Object>> propertyDataList=(ArrayList)propertyList;
            propertyDataList.forEach((item)->{
                EnvironmentInfo.PropertySourceDescriptor propertySource = new EnvironmentInfo.PropertySourceDescriptor();
                // name
                String name = DashboardUtil.getEmptyStringIfNull(item,("name"));
                propertySource.setName(name);
                // properties
                List<Map> properties = new ArrayList<>();
                // begin to parse properties
                Map propMap = (Map) item.get("properties");
                Set keys = propMap.keySet();
                keys.forEach((key)->{
                    Map valItem = new HashMap<>();
                    Object obj = propMap.get(key);
                    Object value = null;
                    if (obj instanceof Map){
                        Map propertyValueMap = (Map) obj;
                        value = propertyValueMap.get("value");
                    }
                    valItem.put(key,value);
                    properties.add(valItem);
                });
                propertySource.setProperties(properties);
                propertySources.add(propertySource);
            });

        }
        environmentInfo.setPropertySources(propertySources);
        return environmentInfo;
    }

    @Override
    public List<MetricsInfo> fetchMetrics(Object source) {
        // do not support
        return null;
    }

    @Override
    public HealthInfo fetchHealth(Object source) {
        Map healthMap = doRequest(source,ActuatorPathEnum.HEALTH.getPath(),restTemplate);
        HealthInfo healthInfo = new HealthInfo();
        if (healthMap.isEmpty()){
            return healthInfo;
        }
        String status = DashboardUtil.getEmptyStringIfNull(healthMap,"status");
        healthInfo.setStatus(status);
        List<HealthInfo.HealthItemInfo> detailsList = new ArrayList<>();
        Object detailsObj = healthMap.get("details");
        if (detailsObj instanceof Map){
            Map detailsMap = (Map) detailsObj;
            Set healthItemKeys = detailsMap.keySet();
            healthItemKeys.forEach((healthItemKey)->{
                HealthInfo.HealthItemInfo healthItemInfo = new HealthInfo.HealthItemInfo();
                healthItemInfo.setName(String.valueOf(healthItemKey));
                Object healthItemObj = detailsMap.get(healthItemKey);
                if (healthItemObj instanceof Map){
                    Map<String,Object> healthItemMap = (Map<String, Object>) healthItemObj;
                    Object statusObj = healthItemMap.get("status");
                    healthItemInfo.setStatus(StringUtils.isEmpty(statusObj)?"": (String) statusObj);
                    Object healthItemDetails = healthItemMap.get("details");
                    if (healthItemDetails instanceof Map){
                        healthItemInfo.setDetails((Map<String, Object>) healthItemDetails);
                    }
                }
                detailsList.add(healthItemInfo);
            });
        }
        healthInfo.setDetails(detailsList);
        return healthInfo;
    }

    @Override
    public Map fetchInfo(Object source) {
        Map<String,String> data = new LinkedHashMap<>();
        Map infoMap = doRequest(source,ActuatorPathEnum.INFO.getPath(),restTemplate);
        if (infoMap.isEmpty()){
            return data;
        }
        Set<String> keys = infoMap.keySet();
        keys.forEach((key)->{
            recursionMap(infoMap,data,key);
        });
        return data;
    }

    @Override
    public List<DetailsItem> fetchDetailsThread(Object source) {
        List<DetailThreadInfo> data = cacheDetailThreads.get(getAppId(source)).getQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item)->{
            result.add(item.getLive());
            result.add(item.getDaemon());
            result.add(item.getPeak());
        });
        return result;
    }

    @Override
    public List<DetailsItem> fetchHeapMemory(Object source) {
        LinkedList<MemoryHeapInfo> data = cacheHeapMemory.get(getAppId(source)).getQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item)->{
            result.add(item.getSize());
            result.add(item.getUsed());
        });
        return result;
    }

    @Override
    public List<DetailsItem> fetchNonHeapMemory(Object source) {
        LinkedList<MemoryNonHeapInfo> data = cacheNonHeapMemory.get(getAppId(source)).getQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item)->{
            result.add(item.getMetaspace());
            result.add(item.getSize());
            result.add(item.getUsed());
        });
        return result;
    }

    @Override
    public LoggersInfo fetchLoggers(Object source) {
        LoggersInfo loggersInfo = new LoggersInfo();
        Map queryData = doRequest(source,ActuatorPathEnum.LOGGERS.getPath(),restTemplate);
        if (queryData.isEmpty()){
            return loggersInfo;
        }
        Object levels = queryData.get("levels");
        if (levels instanceof List){
            loggersInfo.setLevels((List<String>) levels);
        }
        Map<String,LoggersInfo.LoggerItem> loggersData = new LinkedHashMap<>();
        // loggers
        Object loggers = queryData.get("loggers");
        if (loggers instanceof Map){
            Map<String,Object> loggersMap = (Map<String,Object>)loggers;
            loggersMap.keySet().forEach((loggerNameKey)-> {
                LoggersInfo.LoggerItem item = new LoggersInfo.LoggerItem();
                Object objMap = loggersMap.get(loggerNameKey);
                if (objMap instanceof Map){
                    item.setConfiguredLevel(DashboardUtil.getEmptyStringIfNull(((Map)objMap),"configuredLevel"));
                    item.setEffectiveLevel(DashboardUtil.getEmptyStringIfNull(((Map)objMap),"effectiveLevel"));
                    loggersData.put(loggerNameKey, item);
                }
            });
        }
        loggersInfo.setLoggers(loggersData);
        return loggersInfo;
    }

    @Override
    public Map<String, MappingsInfo> fetchMappings(Object source) {
        Map<String, MappingsInfo> result = new LinkedHashMap<>();
        Map queryData = doRequest(source,ActuatorPathEnum.MAPPINGS.getPath(),restTemplate);
        if (queryData.isEmpty()){
            return result;
        }
        if (queryData.containsKey("contexts")){
            Object contexts = queryData.get("contexts");
            if (contexts instanceof Map){
                Map<String,Object> contextsMap = (Map<String,Object>)contexts;
                Set<String> appNames = contextsMap.keySet();
                appNames.forEach((appName)->{
                    MappingsInfo mappingsInfo = new MappingsInfo();
                    Object appObj = contextsMap.get(appName);
                    if (appObj instanceof Map && ((Map) appObj).containsKey("mappings")){
                        Object mappingsObj = ((Map) appObj).get("mappings");
                        if(mappingsObj instanceof Map){
                            List<MappingsInfo.HandlerMappingInfo> mappings = doParseDispatcherServlet((Map)mappingsObj);
                            List<MappingsInfo.HandlerFilterInfo> filters = doParseFilters((Map)mappingsObj);
                            List<MappingsInfo.ServletInfo> servlets = doParseServlets((Map)mappingsObj);
                            mappingsInfo.setServletFilters(filters);
                            mappingsInfo.setDispatcherServlet(mappings);
                            mappingsInfo.setServlets(servlets);
                        }

                    }
                    result.put(appName,mappingsInfo);
                });
            }
        }
        return result;
    }

    @Override
    public List<ThreadDumpInfo> fetchThreadDump(Object source) {
        List<ThreadDumpInfo> result = new ArrayList<>();
        Map queryData = doRequest(source,ActuatorPathEnum.THREADDUMP.getPath(),restTemplate);
        if (queryData.isEmpty()){
            return result;
        }
        if (queryData.containsKey("threads")){
            Object threadsObj = queryData.get("threads");
            if (threadsObj instanceof List){
                List threadsList = (List) threadsObj;
                threadsList.forEach((thread)->{
                    if (thread instanceof Map){
                        ThreadDumpInfo threaddumpInfo = new ThreadDumpInfo();
                        Map threadMap = (Map) thread;
                        threaddumpInfo.setThreadId(DashboardUtil.getDefaultIfNull(threadMap,"threadId",0));
                        threaddumpInfo.setThreadName(DashboardUtil.getEmptyStringIfNull(threadMap,"threadName"));
                        threaddumpInfo.setBlockedCount(DashboardUtil.getDefaultIfNull(threadMap,"blockedCount",0));
                        threaddumpInfo.setBlockedTime(DashboardUtil.getDefaultIfNull(threadMap,"blockedTime",0));
                        threaddumpInfo.setLockName(DashboardUtil.getEmptyStringIfNull(threadMap,"lockName"));
                        threaddumpInfo.setLockOwnerId(DashboardUtil.getEmptyStringIfNull(threadMap,"lockOwnerId"));
                        threaddumpInfo.setLockOwnerName(DashboardUtil.getEmptyStringIfNull(threadMap,"lockOwnerName"));
                        threaddumpInfo.setThreadState(DashboardUtil.getEmptyStringIfNull(threadMap,"threadState"));
                        threaddumpInfo.setWaitedCount(DashboardUtil.getDefaultIfNull(threadMap,"waitedCount",0));
                        threaddumpInfo.setWaitedTime(DashboardUtil.getDefaultIfNull(threadMap,"waitedTime",0));
                        result.add(threaddumpInfo);
                    }
                });
            }
        }
        return result;
    }

    private List<MappingsInfo.HandlerMappingInfo> doParseDispatcherServlet(Map mappings) {
        List<MappingsInfo.HandlerMappingInfo> list = new ArrayList<>();
        if (mappings.containsKey("dispatcherServlets")){
            Object dispatcherServlets = mappings.get("dispatcherServlets");
            if (dispatcherServlets instanceof Map && ((Map) dispatcherServlets).containsKey("dispatcherServlet")){
                Object dispatcherServletObj = ((Map) dispatcherServlets).get("dispatcherServlet");
                if (dispatcherServletObj instanceof List){
                    List dispatcherServletList = (List) dispatcherServletObj;
                    dispatcherServletList.forEach((dispatcherServlet)->{
                        MappingsInfo.HandlerMappingInfo mappingInfo = new MappingsInfo.HandlerMappingInfo();
                        if (dispatcherServlet instanceof Map){
                            Map dispatcherServletMap = (Map) dispatcherServlet;
                            String handler = DashboardUtil.getEmptyStringIfNull(dispatcherServletMap,"handler");
                            String predicate = "";
                            String methods = "";
                            String paramsType = "";
                            String responseType = "";
                            Object detailsObj = dispatcherServletMap.get("details");
                            if (detailsObj instanceof Map && ((Map) detailsObj).containsKey("handlerMethod")){
                                Object handlerMethodObj =  ((Map) detailsObj).get("handlerMethod");
                                if (handlerMethodObj instanceof Map){
                                    Map handlerMethodMap = (Map) handlerMethodObj;
                                    String descriptor = DashboardUtil.getEmptyStringIfNull(handlerMethodMap,"descriptor");
                                    if (descriptor.indexOf(')') > 0){
                                        String[] split = descriptor.split("\\)");
                                        if (split.length == 2){
                                            paramsType =  split[0].substring(1);
                                            responseType = split[1];
                                        }
                                    }
                                }

                                Object requestMappingConditionsObj =  ((Map) detailsObj).get("requestMappingConditions");
                                if (requestMappingConditionsObj instanceof Map){
                                    // methods
                                    Object methodsObj =  ((Map) requestMappingConditionsObj).get("methods");
                                    if(methodsObj instanceof List){
                                        List methodsList = (List) methodsObj;
                                        if (methodsList.size() > 0){
                                            methods = (String) methodsList.get(0);
                                        }
                                    }
                                    // predicate
                                    Object patternsObj =  ((Map) requestMappingConditionsObj).get("patterns");
                                    if(patternsObj instanceof List){
                                        List patternsList = (List) patternsObj;
                                        if (patternsList.size() > 0){
                                            predicate = (String) patternsList.get(0);
                                        }
                                    }
                                }
                            }else {
                                predicate = DashboardUtil.getEmptyStringIfNull(dispatcherServletMap,"predicate");
                            }
                            mappingInfo.setHandler(handler);
                            mappingInfo.setParamsType(paramsType);
                            mappingInfo.setResponseType(responseType);
                            mappingInfo.setPredicate(predicate);
                            mappingInfo.setMethods(methods);
                            list.add(mappingInfo);
                        }
                    });
                }
            }
        }
        return list;
    }

    private List<MappingsInfo.HandlerFilterInfo> doParseFilters(Map mappings) {
        List<MappingsInfo.HandlerFilterInfo> list = new ArrayList<>();
        if (mappings.containsKey("servletFilters")){
            Object servletFilters = mappings.get("servletFilters");
            if (servletFilters instanceof List){
                List servletFilterList = (List) servletFilters;
                servletFilterList.forEach((filter)->{
                    if (filter instanceof Map){
                        MappingsInfo.HandlerFilterInfo item = new MappingsInfo.HandlerFilterInfo();
                        Map filterMap = (Map) filter;
                        List<String> urlPatternMappings = (List<String>) filterMap.get("urlPatternMappings");
                        List<String> servletNameMappings = (List<String>) filterMap.get("servletNameMappings");
                        String name = DashboardUtil.getEmptyStringIfNull(filterMap,"name");
                        String className = DashboardUtil.getEmptyStringIfNull(filterMap,"className");
                        item.setClassName(className);
                        item.setName(name);
                        item.setServletNameMappings(DashboardUtil.convertToString(servletNameMappings));
                        item.setUrlPatternMappings(DashboardUtil.convertToString(urlPatternMappings));
                        list.add(item);
                    }
                });
            }
        }
        return list;
    }

    private List<MappingsInfo.ServletInfo> doParseServlets(Map mappings) {
        List<MappingsInfo.ServletInfo> list = new ArrayList<>();
        if (mappings.containsKey("servlets")){
            Object servlets = mappings.get("servlets");
            if (servlets instanceof List){
                List servletList = (List) servlets;
                servletList.forEach((servlet)->{
                    if (servlet instanceof Map){
                        MappingsInfo.ServletInfo item = new MappingsInfo.ServletInfo();
                        Map servletMap = (Map) servlet;
                        List<String> mappingsList = (List<String>) servletMap.get("mappings");
                        String name = DashboardUtil.getEmptyStringIfNull(servletMap,"name");
                        String className = DashboardUtil.getEmptyStringIfNull(servletMap,"className");
                        item.setClassName(className);
                        item.setName(name);
                        item.setMappings(DashboardUtil.convertToString(mappingsList));
                        list.add(item);
                    }
                });
            }
        }
        return list;
    }

    private void recursionMap(Map map, Map data, String finalKey) {
        String[] keys = finalKey.split("\\.");
        String currentKey = keys[keys.length - 1];
        Object obj = map.get(currentKey);
        if (obj instanceof Map) {
            Map<String, Object> itemMap = (Map<String, Object>) obj;
            Set<String> itemMapKetSets = itemMap.keySet();
            for (String itemKey : itemMapKetSets) {
                String cacheFinalKey = finalKey;
                finalKey += "." + itemKey;
                recursionMap(itemMap, data, finalKey);
                finalKey = cacheFinalKey;
            }
        } else {
            data.put(finalKey, String.valueOf(obj));
        }
    }

    public static Map doRequest(Object source, String path, RestTemplate restTemplate) {
        Map result;
        String targetRequest = buildTargetRestRequest(source, path);
        if (StringUtils.isEmpty(targetRequest)) {
            result = new HashMap();
        } else {
            result = restTemplate.getForObject(targetRequest, Map.class);
            if (result == null) {
                result = new HashMap();
            }
        }
        return result;
    }

    private static String buildTargetRestRequest(Object source, String path) {
        if (source == null) {
            return null;
        }
        return SofaDashboardConstants.HTTP_SCHEME + String.valueOf(source) + path;
    }

    private String getAppId(Object source) {
        String targetHost = String.valueOf(source);
        String[] address = targetHost.split(SofaDashboardConstants.COLON);
        return DashboardUtil.simpleEncode(address[0], Integer.valueOf(address[1]));
    }
}
