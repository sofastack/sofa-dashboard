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
package com.alipay.sofa.dashboard.listener;

import java.util.List;

/**
 * @author bystander
 * @version $Id: RegistryDataChangeListener.java, v 0.1 2018年12月12日 08:07 bystander Exp $
 */
public interface RegistryDataChangeListener {

    /**
     * 数据变更时回调
     *
     * @param type
     * @param data
     */
    void onDataChange(String type, List<String> data);
}