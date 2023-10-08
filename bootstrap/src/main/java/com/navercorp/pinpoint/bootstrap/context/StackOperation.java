/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap.context;

/**
 * @author emeroad
 */
public interface StackOperation {

    public static final int DEFAULT_STACKID = -1;
    public static final int ROOT_STACKID = 0;

    void traceBlockBegin();

    void traceBlockBegin(int stackId);

    // TODO consider to make a interface as below
    // traceRootBlockBegin

    void traceRootBlockEnd();

    void traceBlockEnd();

    void traceBlockEnd(int stackId);
    boolean isRootStack();
}