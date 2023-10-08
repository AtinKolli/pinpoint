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
package com.navercorp.pinpoint.plugin.tomcat;

import static com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodTransformerProperty.*;

import com.navercorp.pinpoint.bootstrap.instrument.MethodFilter;
import com.navercorp.pinpoint.bootstrap.instrument.MethodInfo;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ClassFileTransformerBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodEditorBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodTransformerProperty;

/**
 * @author Jongho Moon
 * @author jaehong.kim
 *
 */
public class TomcatPlugin implements ProfilerPlugin, TomcatConstants {

    /*
     * (non-Javadoc)
     * 
     * @see com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin#setUp(com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext)
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {
        context.addApplicationTypeDetector(new TomcatDetector());

        TomcatConfiguration config = new TomcatConfiguration(context.getConfig());

        if (config.isTomcatHidePinpointHeader()) {
            addRequestFacadeEditor(context);
        }

        addRequestEditor(context);
        addStandardHostValveEditor(context, config);
        addStandardServiceEditor(context);
        addTomcatConnectorEditor(context);
        addWebappLoaderEditor(context);
    }

    private void addRequestEditor(ProfilerPluginSetupContext context) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.connector.Request");
        builder.injectMetadata(METADATA_TRACE);
        builder.injectMetadata(METADATA_ASYNC);
        
        // clear request.
        MethodEditorBuilder recycleMethodEditorBuilder = builder.editMethod("recycle");
        recycleMethodEditorBuilder.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.RequestRecycleInterceptor");
        
        // trace asynchronous process. 
        MethodEditorBuilder startAsyncMethodEditor = builder.editMethod("startAsync", "javax.servlet.ServletRequest", "javax.servlet.ServletResponse");
        startAsyncMethodEditor.property(MethodTransformerProperty.IGNORE_IF_NOT_EXIST);
        startAsyncMethodEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.RequestStartAsyncInterceptor");
        
        context.addClassFileTransformer(builder.build());
    }

    private void addRequestFacadeEditor(ProfilerPluginSetupContext context) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.connector.RequestFacade");
        builder.weave("com.navercorp.pinpoint.plugin.tomcat.aspect.RequestFacadeAspect");
        context.addClassFileTransformer(builder.build());
    }

    private void addStandardHostValveEditor(ProfilerPluginSetupContext context, TomcatConfiguration config) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.core.StandardHostValve");
        builder.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.StandardHostValveInvokeInterceptor", config.getTomcatExcludeUrlFilter());
        context.addClassFileTransformer(builder.build());
    }

    private void addStandardServiceEditor(ProfilerPluginSetupContext context) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.core.StandardService");
        // Tomcat 6
        MethodEditorBuilder startEditor = builder.editMethod("start");
        startEditor.property(IGNORE_IF_NOT_EXIST);
        startEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.StandardServiceStartInterceptor");

        // Tomcat 7
        MethodEditorBuilder startInternalEditor = builder.editMethod("startInternal");
        startInternalEditor.property(IGNORE_IF_NOT_EXIST);
        startInternalEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.StandardServiceStartInterceptor");
        
        context.addClassFileTransformer(builder.build());
    }

    private void addTomcatConnectorEditor(ProfilerPluginSetupContext context) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.connector.Connector");

        // Tomcat 6
        MethodEditorBuilder initializeEditor = builder.editMethod("initialize");
        initializeEditor.property(IGNORE_IF_NOT_EXIST);
        initializeEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.ConnectorInitializeInterceptor");

        // Tomcat 7
        MethodEditorBuilder initInternalEditor = builder.editMethod("initInternal");
        initInternalEditor.property(IGNORE_IF_NOT_EXIST);
        initInternalEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.ConnectorInitializeInterceptor");
        
        context.addClassFileTransformer(builder.build());
    }

    private void addWebappLoaderEditor(ProfilerPluginSetupContext context) {
        ClassFileTransformerBuilder builder = context.getClassEditorBuilder("org.apache.catalina.loader.WebappLoader");
        // Tomcat 6 - org.apache.catalina.loader.WebappLoader.start()
        MethodEditorBuilder startEditor = builder.editMethod("start");
        startEditor.property(IGNORE_IF_NOT_EXIST);
        startEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.WebappLoaderStartInterceptor");

        // Tomcat 7, 8 - org.apache.catalina.loader.WebappLoader.startInternal()
        MethodEditorBuilder startInternalEditor = builder.editMethod("startInternal");
        startInternalEditor.property(IGNORE_IF_NOT_EXIST);
        startInternalEditor.injectInterceptor("com.navercorp.pinpoint.plugin.tomcat.interceptor.WebappLoaderStartInterceptor");
        
        context.addClassFileTransformer(builder.build());
    }
}
