/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A single {@code condition} that must be {@linkplain #matches matched} in order
 * for a component to be registered.
 *
 * <p>Conditions are checked immediately before the bean-definition is due to be
 * registered and are free to veto registration based on any criteria that can
 * be determined at that point.
 *
 * <p>Conditions must follow the same restrictions as {@link BeanFactoryPostProcessor}
 * and take care to never interact with bean instances. For more fine-grained control
 * of conditions that interact with {@code @Configuration} beans consider the
 * {@link ConfigurationCondition} interface.
 *
 * @author Phillip Webb
 * @see ConfigurationCondition
 * @see Conditional
 * @see ConditionContext
 * @since 4.0
 */
@FunctionalInterface
public interface Condition {

    /**
     * 确定条件是否匹配。false说明需要跳过
     *
     * @param context  条件上下文
     * @param metadata {@link org.springframework.core.type.AnnotationMetadata 类}的元数据
     *                 或 {@link org.springframework.core.type.MethodMetadata method} 被检查
     * @return {@code true} 如果条件匹配并且可以注册该组件，或 {@code false} 否决带注释的组件的注册
     */
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
