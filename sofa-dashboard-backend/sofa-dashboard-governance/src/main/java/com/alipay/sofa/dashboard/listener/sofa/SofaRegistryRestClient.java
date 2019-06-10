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
package com.alipay.sofa.dashboard.listener.sofa;

import com.alipay.sofa.dashboard.cache.RegistryDataCache;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/5 8:02 PM
 * @since:
 **/
@Component
public class SofaRegistryRestClient {

    private static final Logger LOGGER                          = LoggerFactory
                                                                    .getLogger(SofaRegistryRestClient.class);

    private static final String DATA_PREFIX                     = "digest";
    private static final String REGISTRY_QUERY_DATA_INFO_IDS    = DATA_PREFIX
                                                                  + "/getDataInfoIdList";
    private static final String REGISTRY_QUERY_CHECK_SUM        = DATA_PREFIX
                                                                  + "/checkSumDataInfoIdList";
    private static final String REGISTRY_QUERY_SUB_SESSION_DATA = DATA_PREFIX + "/sub/data/query";
    private static final String REGISTRY_QUERY_PUB_SESSION_DATA = DATA_PREFIX + "/pub/data/query";

    @Autowired
    RegistryDataCache           registryDataCache;

    @Autowired
    private RestTemplate        restTemplate;

    private static String       sessionAddress;

    private static int          port;

    public void syncAllSessionData() {
        List<String> dataIds;
        String httpUrl = buildRequestUrl(REGISTRY_QUERY_DATA_INFO_IDS);
        try {
            ResponseEntity<List> forEntity = restTemplate.getForEntity(httpUrl, List.class);
            dataIds = forEntity.getBody();
            if (dataIds == null) {
                dataIds = new ArrayList<>();
            }
            List<RpcService> service = new ArrayList<>();
            for (String dataInfoId : dataIds) {
                RpcService rpcService = new RpcService();
                rpcService.setServiceName(dataInfoId);
                service.add(rpcService);
                List<RpcProvider> providers = syncProviders(dataInfoId);
                registryDataCache.addProviders(dataInfoId, providers);
                List<RpcConsumer> consumers = syncConsumers(dataInfoId);
                registryDataCache.addConsumers(dataInfoId, consumers);
            }
            registryDataCache.addService(service);
        } catch (Throwable t) {
            LOGGER.error(
                "Failed to sync all dataInfoIds from session. query url [" + httpUrl + "]", t);
        }
    }

    public Integer checkSum() {
        String pubUrl = buildRequestUrl(REGISTRY_QUERY_CHECK_SUM);
        ResponseEntity<Integer> checkSumResp = restTemplate.getForEntity(pubUrl, Integer.class);
        return checkSumResp.getBody();
    }

    private List<RpcProvider> syncProviders(String dataInfoId) {
        String pubUrl = buildRequestUrl(REGISTRY_QUERY_PUB_SESSION_DATA);
        pubUrl += "?dataInfoId={1}";
        ResponseEntity<Map> pubResponse = restTemplate.getForEntity(pubUrl, Map.class, dataInfoId);
        List<RpcProvider> providers = new ArrayList<>();
        if (pubResponse != null && pubResponse.getBody() != null) {
            Map<String, List<Map>> subMap = pubResponse.getBody();
            Set<String> subKeys = subMap.keySet();
            subKeys.forEach((key) -> {
                List<Map> publisherList = subMap.get(key);
                for (Map publisherMap : publisherList) {
                    RpcProvider provider = convertRpcProviderFromMap(publisherMap);
                    providers.add(provider);
                }

            });
        }
        return providers;
    }

    private List<RpcConsumer> syncConsumers(String dataInfoId) {
        String subUrl = buildRequestUrl(REGISTRY_QUERY_SUB_SESSION_DATA);
        List<RpcConsumer> consumers = new ArrayList<>();
        subUrl += "?dataInfoId={1}";
        ResponseEntity<Map> subResponse = restTemplate.getForEntity(subUrl, Map.class, dataInfoId);
        if (subResponse != null && subResponse.getBody() != null) {
            Map<String, List<Map>> subMap = subResponse.getBody();
            Set<String> subKeys = subMap.keySet();
            subKeys.forEach((key) -> {
                List<Map> subscriberList = subMap.get(key);
                for (Map subscriberMap : subscriberList) {
                    RpcConsumer consumer = convertRpcConsumerFromMap(subscriberMap);
                    consumers.add(consumer);
                }
            });
        }
        return consumers;
    }

    /**
     * Extract value from map ,if null return empty String
     *
     * @param map
     * @param key
     * @return
     */
    private String getEmptyStringIfNull(Map map, String key) {
        if (map == null || map.size() <= 0) {
            return StringUtils.EMPTY;
        }
        Object valueObject = map.get(key);
        String valueStr;
        try {
            valueStr = (String) valueObject;
        } catch (Throwable throwable) {
            return StringUtils.EMPTY;
        }
        return StringUtils.isBlank(valueStr) ? StringUtils.EMPTY : valueStr;
    }

    private RpcConsumer convertRpcConsumerFromMap(Map subscriberMap) {
        RpcConsumer consumer = new RpcConsumer();
        consumer.setAppName(getEmptyStringIfNull(subscriberMap, SofaDashboardConstants.APP_NAME));
        consumer.setServiceName(getEmptyStringIfNull(subscriberMap,
            SofaDashboardConstants.REGISTRY_DATA_ID_KEY));
        String processId = getEmptyStringIfNull(subscriberMap,
            SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY);
        if (processId.contains(SofaDashboardConstants.COLON)) {
            consumer.setAddress(processId.split(SofaDashboardConstants.COLON)[0]);
            consumer.setPort(Integer.valueOf(processId.split(SofaDashboardConstants.COLON)[1]));
        } else {
            Object sourceAddress = subscriberMap
                .get(SofaDashboardConstants.REGISTRY_SOURCE_ADDRESS_KEY);
            if (sourceAddress instanceof Map) {
                String ipAddress = getEmptyStringIfNull((Map) sourceAddress,
                    SofaDashboardConstants.REGISTRY_IP_KEY);
                String port = getEmptyStringIfNull((Map) sourceAddress, SofaDashboardConstants.PORT);
                consumer.setAddress(ipAddress);
                consumer.setPort(Integer.valueOf(StringUtils.isBlank(port) ? "0" : port));
            }
        }
        Map<String, String> attributes = (Map<String, String>) subscriberMap
            .get(SofaDashboardConstants.REGISTRY_ATTRIBUTES);
        consumer.setParameters(attributes);
        return consumer;
    }

    private RpcProvider convertRpcProviderFromMap(Map publisherMap) {
        RpcProvider provider = new RpcProvider();
        provider.setAppName(getEmptyStringIfNull(publisherMap, SofaDashboardConstants.APP_NAME));
        provider.setServiceName(getEmptyStringIfNull(publisherMap,
            SofaDashboardConstants.REGISTRY_DATA_ID_KEY));
        String processId = getEmptyStringIfNull(publisherMap,
            SofaDashboardConstants.REGISTRY_PROCESS_ID_KEY);
        if (processId.contains(SofaDashboardConstants.COLON)) {
            provider.setAddress(processId.split(SofaDashboardConstants.COLON)[0]);
            provider.setPort(Integer.valueOf(processId.split(SofaDashboardConstants.COLON)[1]));
        } else {
            Object sourceAddress = publisherMap
                .get(SofaDashboardConstants.REGISTRY_SOURCE_ADDRESS_KEY);
            if (sourceAddress instanceof Map) {
                String ipAddress = getEmptyStringIfNull((Map) sourceAddress,
                    SofaDashboardConstants.REGISTRY_IP_KEY);
                String port = getEmptyStringIfNull((Map) sourceAddress, SofaDashboardConstants.PORT);
                provider.setAddress(ipAddress);
                provider.setPort(Integer.valueOf(StringUtils.isBlank(port) ? "0" : port));
            }
        }
        Map<String, String> attributes = (Map<String, String>) publisherMap
            .get(SofaDashboardConstants.REGISTRY_ATTRIBUTES);
        provider.setParameters(attributes);
        return provider;
    }

    /**
     * build query request url
     *
     * @param resource
     * @return
     */
    private String buildRequestUrl(String resource) {
        StringBuilder sb = new StringBuilder();
        sb.append(SofaDashboardConstants.HTTP_SCHEME).append(sessionAddress)
            .append(SofaDashboardConstants.COLON).append(port);
        sb.append(SofaDashboardConstants.SEPARATOR).append(resource);
        return sb.toString();
    }

    public void init(RegistryConfig registryConfig) {
        String endPointAddress = registryConfig.getAddress();
        if (!endPointAddress.contains(SofaDashboardConstants.COLON)) {
            throw new RuntimeException(
                "Please check your session address.Illegal session address is [" + endPointAddress
                        + "]");
        }
        sessionAddress = endPointAddress.split(SofaDashboardConstants.COLON)[0];
        port = Integer.valueOf(endPointAddress.split(SofaDashboardConstants.COLON)[1]);
    }
}
