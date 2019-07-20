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

import com.alipay.sofa.ark.api.ResponseCode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/17 10:29 PM
 * @since:
 **/
public class ClientResponseModel {

    private String        message  = "";
    private ResponseCode  code     = ResponseCode.NOT_FOUND_BIZ;
    private Set<BizModel> bizInfos = new HashSet<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    public Set<BizModel> getBizInfos() {
        return bizInfos;
    }

    public void setBizInfos(Set<BizModel> bizInfos) {
        this.bizInfos = bizInfos;
    }
}
