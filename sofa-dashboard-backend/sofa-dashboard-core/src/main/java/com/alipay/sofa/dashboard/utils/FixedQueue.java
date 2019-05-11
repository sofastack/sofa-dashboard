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

import java.util.LinkedList;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/5/9 5:19 PM
 * @since:
 **/
public class FixedQueue<E> {

    /**
     * 固定长度
     */
    private final int     size;

    private LinkedList<E> queue = new LinkedList<>();

    public FixedQueue(int size) {
        this.size = size;
    }

    /**
     * 入列：当队列大小已满时，把队头的元素poll掉
     */
    public void offer(E e) {
        if (queue.size() >= size) {
            queue.poll();
        }
        queue.offer(e);
    }

    public E get(int position) {
        return queue.get(position);
    }

    public E getLast() {
        return queue.getLast();
    }

    public E getFirst() {
        return queue.getFirst();
    }

    public int getSize() {
        return size;
    }

    public int size() {
        return queue.size();
    }

    public LinkedList<E> getQueue() {
        return queue;
    }
}
