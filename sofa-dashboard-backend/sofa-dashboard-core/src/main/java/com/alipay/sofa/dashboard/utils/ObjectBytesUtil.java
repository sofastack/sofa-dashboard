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

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Used to convert an Object into a byte array and a byte array into an object
 *
 * @author: guolei.sgl (guolei.sgl@antfin.com) 19/1/17 下午5:49
 * @since:
 **/
public class ObjectBytesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectBytesUtil.class);

    public static <T> T convertFromBytes(byte[] bytes, Class<T> valueType) {
        try {
            if (bytes == null) {
                return null;
            }
            return JSON.parseObject(bytes, valueType);
        } catch (Exception e) {
            LOGGER.error("Error to convert object from data bytes.", e);
        }
        return null;
    }

    public static <T> T convertFromString(String input, Class<T> valueType) {
        try {
            if (StringUtils.isEmpty(input)) {
                return null;
            }
            return JSON.parseObject(input, valueType);
        } catch (Exception e) {
            LOGGER.error("Error to convert object from data bytes.", e);
        }
        return null;
    }

    public static byte[] convertFromObject(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            String jsonObj = JSON.toJSONString(obj);
            return jsonObj.getBytes();
        } catch (Exception e) {
            LOGGER.error("Error to convert bytes from data object.", e);
        }
        return null;

    }
}
