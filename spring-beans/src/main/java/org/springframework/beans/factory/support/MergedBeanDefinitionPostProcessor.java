/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 在运行时用于<i>合并的</i>Bean定义的后处理器回调接口。
 * {@link BeanPostProcessor}实现可以实现此子接口，以便对Spring {@code BeanFactory}
 * 用于创建bean实例的合并bean定义（原始bean定义的已处理副本）进行后处理。.
 *
 * <p>{@link #postProcessMergedBeanDefinition}方法例如可以内省* bean定义，以便在后处理
 * bean的实际实例之前准备一些缓存的元数据。还允许修改bean定义，但仅用于实际用于并发修改的定义属性。
 * 本质上，这仅适用于{@link RootBeanDefinition}本身定义的操作，不适用于其基类的属性.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * 对指定bean的给定合并bean定义进行后处理.
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 * @see AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors
	 */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

	/**
	 * 通知，用于指定名称的Bean定义已被重置，并且此后处理器应清除受影响的Bean的所有元数据。<p>默认实现为空.
	 * @param beanName the name of the bean
	 * @since 5.1
	 * @see DefaultListableBeanFactory#resetBeanDefinition
	 */
	default void resetBeanDefinition(String beanName) {
	}

}
