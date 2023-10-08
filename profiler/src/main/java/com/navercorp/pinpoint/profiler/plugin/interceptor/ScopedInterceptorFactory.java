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
package com.navercorp.pinpoint.profiler.plugin.interceptor;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.MethodInfo;
import com.navercorp.pinpoint.bootstrap.interceptor.Interceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.SimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.StaticAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.interceptor.group.ExecutionPoint;
import com.navercorp.pinpoint.bootstrap.interceptor.group.InterceptorGroup;

/**
 * @author Jongho Moon
 *
 */
public class ScopedInterceptorFactory implements InterceptorFactory {
    private final InterceptorFactory next;
    private final InterceptorGroup group;
    private final ExecutionPoint executionPoint;
    
    public ScopedInterceptorFactory(InterceptorFactory next, InterceptorGroup group, ExecutionPoint point) {
        this.next = next;
        this.group = group;
        this.executionPoint = point;
    }

    @Override
    public Interceptor getInterceptor(ClassLoader classLoader, InstrumentClass target, MethodInfo targetMethod) {
        Interceptor interceptor = next.getInterceptor(classLoader, target, targetMethod);
        
        if (interceptor instanceof SimpleAroundInterceptor) {
            return new ScopedSimpleAroundInterceptor((SimpleAroundInterceptor)interceptor, group, executionPoint);
        }  else if (interceptor instanceof StaticAroundInterceptor) {
            return new ScopedStaticAroundInterceptor((StaticAroundInterceptor)interceptor, group, executionPoint);
        }
        
        throw new IllegalArgumentException("Unexpected interceptor type: " + interceptor.getClass());
    }
}
