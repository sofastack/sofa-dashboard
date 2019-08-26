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

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/22 11:42 AM
 * @since:
 **/
public class FastJsonUtilsTest {

    @Test
    public void testFastJsonUtils() {
        JSONObject parent = new JSONObject();
        JSONObject child = new JSONObject();
        child.put("childKey", "childValue");
        child.put("childKeyInteger", 1);
        parent.put("parentKeySub", child);
        parent.put("parentKeyString", "String");
        parent.put("parentKeyInteger", 1);

        Assert.assertTrue(FastJsonUtils.getString(parent, "parentKeyString").equals("String"));
        Assert.assertTrue(FastJsonUtils.getString(parent, "parentKeySub/childKey").equals(
            "childValue"));

        Assert.assertTrue(FastJsonUtils.getInteger(parent, "parentKeyInteger") == 1);
        Assert.assertTrue(FastJsonUtils.getInteger(parent, "parentKeySub/childKeyInteger") == 1);

    }

}
