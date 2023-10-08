/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.plugin;

import com.navercorp.pinpoint.bootstrap.instrument.Scope;
import com.navercorp.pinpoint.bootstrap.interceptor.group.InterceptorGroup;

/**
 * @author Jongho Moon
 *
 */
public class DefaultInterceptorGroup implements InterceptorGroup {
    private final String name;
    private final ThreadLocal<Scope> threadLocal;
    
    public DefaultInterceptorGroup(final String name) {
        this.name = name;
        this.threadLocal = new ThreadLocal<Scope>() {

            @Override
            protected Scope initialValue() {
                return new DefaultScope(name);
            }
            
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getCurrentTransaction() {
        return threadLocal.get();
    }
}
