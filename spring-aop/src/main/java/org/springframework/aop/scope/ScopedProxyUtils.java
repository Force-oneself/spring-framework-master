/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.aop.scope;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Utility class for creating a scoped proxy.
 *
 * <p>Used by ScopedProxyBeanDefinitionDecorator and ClassPathBeanDefinitionScanner.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 2.5
 */
public abstract class ScopedProxyUtils {

    private static final String TARGET_NAME_PREFIX = "scopedTarget.";

    private static final int TARGET_NAME_PREFIX_LENGTH = TARGET_NAME_PREFIX.length();


    /**
     * 为提供的目标 bean 生成范围代理，注册目标
     * 具有内部名称并在作用域代理上设置“targetBeanName”的 bean.
     *
     * @param definition       the original bean definition
     * @param registry         the bean definition registry
     * @param proxyTargetClass whether to create a target class proxy
     * @return the scoped proxy definition
     * @see #getTargetBeanName(String)
     * @see #getOriginalBeanName(String)
     */
    public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
                                                         BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        String originalBeanName = definition.getBeanName();
        BeanDefinition targetDefinition = definition.getBeanDefinition();
        // 添加上统一前缀区分"scopedTarget."
        String targetBeanName = getTargetBeanName(originalBeanName);

        // 为原始 bean 名称创建范围代理定义，在内部目标定义中“隐藏”目标 bean.
        RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
        proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
        proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
        proxyDefinition.setSource(definition.getSource());
        proxyDefinition.setRole(targetDefinition.getRole());

        proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
        if (proxyTargetClass) {
            targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // ScopedProxyFactoryBean 的“proxyTargetClass”默认为TRUE，所以我们不需要在这里明确设置.
        } else {
            proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
        }

        // 从原始 bean 定义复制自动装配设置.
        proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
        proxyDefinition.setPrimary(targetDefinition.isPrimary());
        if (targetDefinition instanceof AbstractBeanDefinition) {
            proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
        }

        // 应忽略目标 bean 以支持范围代理.
        targetDefinition.setAutowireCandidate(false);
        targetDefinition.setPrimary(false);

        // 在工厂中将目标 bean 注册为单独的 bean.
        registry.registerBeanDefinition(targetBeanName, targetDefinition);

        // 将作用域代理定义作为主 bean 定义返回（可能是内部Bean）。
        return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
    }

    /**
     * 生成在作用域代理中使用的 bean 名称以引用目标 bean。
     *
     * @param originalBeanName the original name of bean
     * @return the generated bean to be used to reference the target bean
     * @see #getOriginalBeanName(String)
     */
    public static String getTargetBeanName(String originalBeanName) {
        return TARGET_NAME_PREFIX + originalBeanName;
    }

    /**
     * Get the original bean name for the provided {@linkplain #getTargetBeanName
     * target bean name}.
     *
     * @param targetBeanName the target bean name for the scoped proxy
     * @return the original bean name
     * @throws IllegalArgumentException if the supplied bean name does not refer
     *                                  to the target of a scoped proxy
     * @see #getTargetBeanName(String)
     * @see #isScopedTarget(String)
     * @since 5.1.10
     */
    public static String getOriginalBeanName(@Nullable String targetBeanName) {
        Assert.isTrue(isScopedTarget(targetBeanName), () -> "bean name '" +
                targetBeanName + "' does not refer to the target of a scoped proxy");
        return targetBeanName.substring(TARGET_NAME_PREFIX_LENGTH);
    }

    /**
     * Determine if the {@code beanName} is the name of a bean that references
     * the target bean within a scoped proxy.
     *
     * @since 4.1.4
     */
    public static boolean isScopedTarget(@Nullable String beanName) {
        return (beanName != null && beanName.startsWith(TARGET_NAME_PREFIX));
    }

}
