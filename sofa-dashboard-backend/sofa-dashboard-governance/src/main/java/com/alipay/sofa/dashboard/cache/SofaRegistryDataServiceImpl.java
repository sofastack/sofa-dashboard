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
package com.alipay.sofa.dashboard.cache;

import com.alipay.sofa.dashboard.domain.RpcConsumer;
import com.alipay.sofa.dashboard.domain.RpcProvider;
import com.alipay.sofa.dashboard.domain.RpcService;
import com.alipay.sofa.dashboard.listener.sofa.SofaRegistryRestClient;
import com.alipay.sofa.dashboard.registry.SofaAdminRegistry;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/23 10:52 AM
 * @since:
 **/
public class SofaRegistryDataServiceImpl implements RegistryDataService {
    @Autowired
    private SofaRegistryRestClient sofaRegistryRestClient;

    @Override
    public Map<String, RpcService> fetchService() {
        Map<String, RpcService> result = new HashMap<>();
        SofaAdminRegistry.getCachedAllDataInfoIds().forEach((dataInfoId) -> {
            if (StringUtils.isNotBlank(dataInfoId)){
                RpcService service = new RpcService();
                service.setServiceName(dataInfoId);
                result.put(service.getServiceName(),service);
            }
        });
        return result;
    }

    @Override
    public List<RpcProvider> fetchProvidersByService(String serviceName) {
        return sofaRegistryRestClient.queryProviders(serviceName);
    }

    @Override
    public List<RpcConsumer> fetchConsumersByService(String serviceName) {
        return sofaRegistryRestClient.queryConsumers(serviceName);
    }
}
