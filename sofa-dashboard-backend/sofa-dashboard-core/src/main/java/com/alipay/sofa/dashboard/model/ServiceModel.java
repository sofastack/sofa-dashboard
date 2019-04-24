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

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 18/12/7 下午2:07
 * @since:
 **/
public class ServiceModel {
    private String serviceId;

    private String serviceProviderAppName;

    private String serviceProviderAppNum;

    private String serviceConsumerAppNum;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceProviderAppName() {
        return serviceProviderAppName;
    }

    public void setServiceProviderAppName(String serviceProviderAppName) {
        this.serviceProviderAppName = serviceProviderAppName;
    }

    public String getServiceProviderAppNum() {
        return serviceProviderAppNum;
    }

    public void setServiceProviderAppNum(String serviceProviderAppNum) {
        this.serviceProviderAppNum = serviceProviderAppNum;
    }

    public String getServiceConsumerAppNum() {
        return serviceConsumerAppNum;
    }

    public void setServiceConsumerAppNum(String serviceConsumerAppNum) {
        this.serviceConsumerAppNum = serviceConsumerAppNum;
    }
}
