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

package com.navercorp.pinpoint.profiler.plugin.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.navercorp.pinpoint.bootstrap.FieldAccessor;
import com.navercorp.pinpoint.bootstrap.MetadataAccessor;
import com.navercorp.pinpoint.bootstrap.instrument.MethodFilter;
import com.navercorp.pinpoint.bootstrap.interceptor.group.ExecutionPoint;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ClassCondition;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ClassFileTransformerBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ConditionalClassFileTransformerBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ConditionalClassFileTransformerSetup;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.ConstructorEditorBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.DedicatedClassFileTransformer;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.InterceptorBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodEditorBuilder;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodTransformerExceptionHandler;
import com.navercorp.pinpoint.bootstrap.plugin.transformer.MethodTransformerProperty;
import com.navercorp.pinpoint.profiler.plugin.DefaultProfilerPluginContext;
import com.navercorp.pinpoint.profiler.plugin.FieldAccessorInjector;
import com.navercorp.pinpoint.profiler.plugin.MetadataInitializationStrategy.ByConstructor;
import com.navercorp.pinpoint.profiler.plugin.MetadataInjector;
import com.navercorp.pinpoint.profiler.plugin.interceptor.AnnotatedInterceptorInjector;
import com.navercorp.pinpoint.profiler.plugin.interceptor.TargetAnnotatedInterceptorInjector;

public class DefaultClassFileTransformerBuilder implements ClassFileTransformerBuilder, ConditionalClassFileTransformerBuilder, RecipeBuilder<ClassRecipe> {

    private final DefaultProfilerPluginContext pluginContext;
    
    private final List<ClassRecipe> recipes = new ArrayList<ClassRecipe>();
    private final List<RecipeBuilder<ClassRecipe>> recipeBuilders = new ArrayList<RecipeBuilder<ClassRecipe>>();
    
    private final ClassCondition condition;
    private final String targetClassName;

    public DefaultClassFileTransformerBuilder(DefaultProfilerPluginContext pluginContext, String targetClassName) {
        this(pluginContext, targetClassName, null);
    }
    
    private DefaultClassFileTransformerBuilder(DefaultProfilerPluginContext pluginContext, String targetClassName, ClassCondition condition) {
        this.pluginContext = pluginContext;
        this.targetClassName = targetClassName;
        this.condition = condition;
    }

    @Override
    public void conditional(ClassCondition condition, ConditionalClassFileTransformerSetup describer) {
        DefaultClassFileTransformerBuilder conditional = new DefaultClassFileTransformerBuilder(pluginContext, targetClassName, condition);
        describer.setup(conditional);
        recipeBuilders.add(conditional);
    }
    
    @Override
    public void injectFieldAccessor(String fieldName) {
        FieldAccessor snooper = pluginContext.allocateFieldSnooper(fieldName);
        recipes.add(new FieldAccessorInjector(snooper, fieldName));
    }
    
    @Override
    public void injectMetadata(String name) {
        MetadataAccessor accessor = pluginContext.allocateMetadataAccessor(name);
        recipes.add(new MetadataInjector(name, accessor));
    }
    
    @Override
    public void injectMetadata(String name, String initialValueType) {
        MetadataAccessor accessor = pluginContext.allocateMetadataAccessor(name);
        recipes.add(new MetadataInjector(name, accessor, new ByConstructor(initialValueType)));
    }
    
    @Override
    public void injectInterceptor(String className, Object... constructorArgs) {
        recipeBuilders.add(new TargetAnnotatedInterceptorInjectorBuilder(className, constructorArgs));
    }

    @Override
    public MethodEditorBuilder editMethods(MethodFilter filter) {
        DefaultMethodEditorBuilder builder = new DefaultMethodEditorBuilder(filter);
        recipeBuilders.add(builder);
        return builder;
    }

    @Override
    public MethodEditorBuilder editMethod(String name, String... parameterTypeNames) {
        DefaultMethodEditorBuilder builder = new DefaultMethodEditorBuilder(name, parameterTypeNames);
        recipeBuilders.add(builder);
        return builder;
    }

    @Override
    public ConstructorEditorBuilder editConstructor(String... parameterTypeNames) {
        DefaultMethodEditorBuilder builder = new DefaultMethodEditorBuilder(parameterTypeNames);
        recipeBuilders.add(builder);
        return builder;
    }
    
    @Override
    public void weave(String aspectClassName) {
        recipes.add(new ClassWeaver(aspectClassName));
    }
    
    @Override
    public DedicatedClassFileTransformer build() {
        ClassRecipe recipe = buildClassRecipe(); 
        return new DefaultDedicatedClassFileTransformer(pluginContext.getByteCodeInstrumentor(), pluginContext.getClassLoaderFactory(), targetClassName, recipe);
    }

    private ClassRecipe buildClassRecipe() {
        List<ClassRecipe> recipes = new ArrayList<ClassRecipe>(this.recipes);
        
        for (RecipeBuilder<ClassRecipe> builder : recipeBuilders) {
            recipes.add(builder.buildRecipe());
        }
        
        if (recipes.isEmpty()) {
            throw new IllegalStateException("No class transformation registered"); 
        }
        
        ClassRecipe recipe = recipes.size() == 1 ? recipes.get(0) : new ClassCookBook(recipes);
        return recipe;
    }
    
    @Override
    public ClassRecipe buildRecipe() {
        if (condition == null) {
            throw new IllegalStateException();
        }
        
        ClassRecipe recipe = buildClassRecipe();
        return new ConditionalClassRecipe(pluginContext, condition, recipe);
    }




    private class TargetAnnotatedInterceptorInjectorBuilder implements RecipeBuilder<ClassRecipe> {
        private final String interceptorClassName;
        private final Object[] constructorArguments;
        
        public TargetAnnotatedInterceptorInjectorBuilder(String interceptorClassName, Object[] constructorArguments) {
            this.interceptorClassName = interceptorClassName;
            this.constructorArguments = constructorArguments;
        }

        @Override
        public ClassRecipe buildRecipe() {
            return new TargetAnnotatedInterceptorInjector(pluginContext, interceptorClassName, constructorArguments);
        }
    }

    private class AnnotatedInterceptorInjectorBuilder implements InterceptorBuilder, RecipeBuilder<MethodRecipe> {
        private final String interceptorClassName;
        private final Object[] constructorArguments;
        
        private String groupName;
        private ExecutionPoint executionPoint;
        
        public AnnotatedInterceptorInjectorBuilder(String interceptorClassName, Object[] constructorArguments) {
            this.interceptorClassName = interceptorClassName;
            this.constructorArguments = constructorArguments;
        }
        
        @Override
        public void group(String groupName) {
            group(groupName, ExecutionPoint.ALWAYS);            
        }
        
        @Override
        public void group(String groupName, ExecutionPoint point) {
            this.groupName = groupName;
            this.executionPoint = point;
        }

        @Override
        public MethodRecipe buildRecipe() {
            return new AnnotatedInterceptorInjector(pluginContext, interceptorClassName, constructorArguments, groupName, executionPoint);
        }
    }
    
    public class DefaultMethodEditorBuilder implements MethodEditorBuilder, ConstructorEditorBuilder, RecipeBuilder<ClassRecipe> {
        private final String methodName;
        private final String[] parameterTypeNames;
        private final MethodFilter filter;
        private final List<RecipeBuilder<MethodRecipe>> recipeBuilders = new ArrayList<RecipeBuilder<MethodRecipe>>();
        private final EnumSet<MethodTransformerProperty> properties = EnumSet.noneOf(MethodTransformerProperty.class);
        private MethodTransformerExceptionHandler exceptionHandler;

        private DefaultMethodEditorBuilder(String... parameterTypeNames) {
            this.methodName = null;
            this.parameterTypeNames = parameterTypeNames;
            this.filter = null;
        }
        
        private DefaultMethodEditorBuilder(String methodName, String... parameterTypeNames) {
            this.methodName = methodName;
            this.parameterTypeNames = parameterTypeNames;
            this.filter = null;
        }

        private DefaultMethodEditorBuilder(MethodFilter filter) {
            this.methodName = null;
            this.parameterTypeNames = null;
            this.filter = filter;
        }
        
        @Override
        public void property(MethodTransformerProperty... properties) {
            this.properties.addAll(Arrays.asList(properties));
        }

        @Override
        public InterceptorBuilder injectInterceptor(String interceptorClassName, Object... constructorArguments) {
            AnnotatedInterceptorInjectorBuilder builder = new AnnotatedInterceptorInjectorBuilder(interceptorClassName, constructorArguments);
            recipeBuilders.add(builder);
            return builder;
        }
        
        @Override
        public void exceptionHandler(MethodTransformerExceptionHandler handler) {
            this.exceptionHandler = handler;
        }

        @Override
        public MethodTransformer buildRecipe() {
            List<MethodRecipe> recipes = buildMethodRecipe();
            MethodTransformer transformer = buildMethodEditor(recipes);
            
            return transformer;
        }

        private MethodTransformer buildMethodEditor(List<MethodRecipe> recipes) {
            MethodTransformer transformer;
            if (filter != null) {
                transformer = new FilteringMethodTransformer(filter, recipes, exceptionHandler);
            } else if (methodName != null) {
                transformer = new DedicatedMethodTransformer(methodName, parameterTypeNames, recipes, exceptionHandler, properties.contains(MethodTransformerProperty.IGNORE_IF_NOT_EXIST));
            } else {
                transformer = new ConstructorTransformer(parameterTypeNames, recipes, exceptionHandler, properties.contains(MethodTransformerProperty.IGNORE_IF_NOT_EXIST));
            }
            
            return transformer;
        }

        private List<MethodRecipe> buildMethodRecipe() {
            if (recipeBuilders.isEmpty()) {
                // For now, a method transformer without any interceptor is meaningless. 
                throw new IllegalStateException("No interceptors are defiend");
            }

            List<MethodRecipe> recipes = new ArrayList<MethodRecipe>(recipeBuilders.size());
            
            for (RecipeBuilder<MethodRecipe> builder : recipeBuilders) {
                recipes.add(builder.buildRecipe());
            }
            
            return recipes;
        }
    }
}
