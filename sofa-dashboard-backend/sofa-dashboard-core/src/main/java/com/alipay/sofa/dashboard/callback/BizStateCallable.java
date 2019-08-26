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
package com.alipay.sofa.dashboard.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/7/20 10:49 AM
 * @since:
 **/
public class BizStateCallable<V> implements Callable<V> {

    private static final Logger          LOGGER   = LoggerFactory.getLogger(BizStateCallable.class);

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Callable<V>            callable;
    private final V                      timeoutV;
    /** 2s */
    private final long                   timeout;

    /**
     * 构造一个 TimeoutCallable
     *
     * @param callable 要运行的 Callable
     * @param timeout Callable 的最大运行时间
     * @param timeoutV Callable 超时的返回结果
     */
    public BizStateCallable(Callable<V> callable, long timeout, V timeoutV) {
        this.timeout = timeout;
        this.callable = callable;
        this.timeoutV = timeoutV;
    }

    @Override
    public V call() {
        Future<V> future = executor.submit(callable);
        V v = null;
        try {
            v = future.get(timeout, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            LOGGER.error("biz state callback timeout or get error.", ex);
        }
        executor.shutdownNow(); // 给线程池中所有正在运行的线程发送 中断 信号
        return v != null ? v : timeoutV;
    }

}
