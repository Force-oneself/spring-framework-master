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

package org.springframework.beans.factory.config;

import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * 由大多数可列出的bean工厂实现的配置接口。除了{@link ConfigurableBeanFactory}外，
 * 它还提供了分析和修改bean定义以及预先实例化单例的工具。
 *
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 * @since 03.11.2003
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * 忽略给定的依赖类型进行自动装配：例如，字符串。默认为无。
	 *
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * 忽略给定的依赖接口进行自动装配。
	 * <p>通常由应用程序上下文用来注册以其他方式解析的依赖项，例如通过BeanFactoryAware的BeanFactory
	 * 或通过ApplicationContextAware的ApplicationContext。
	 * <p>默认情况下，仅BeanFactoryAware接口被忽略。 要忽略其他类型，请为每种类型调用此方法.
	 *
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * 用相应的自动装配值注册一个特殊的依赖类型。
	 * <p>这是用于工厂/上下文引用的，这些引用应该是可自动执行的，但在工厂中未定义为bean：
	 * 解析为bean所在的ApplicationContext实例的ApplicationContext类型的依赖项。
	 * <p>注意：在普通BeanFactory中没有注册这样的默认类型，甚至对于BeanFactory接口本身也没有。
	 *
	 * @param dependencyType 要注册的依赖项类型。这通常是一个基本接口，
	 *                       例如BeanFactory，并且扩展名也可以解析为
	 *                       如果声明为自动装配依赖项（例如ListableBeanFactory），
	 *                       则只要给定值实际实现扩展接口即可。
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 *                       implementation of the {@link org.springframework.beans.factory.ObjectFactory}
	 *                       interface, which allows for lazy resolution of the actual target value.
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * 确定指定的bean是否符合自动装配候选条件，可以注入到声明匹配类型依赖项的其他bean中。
	 * <p>此方法也检查祖先工厂.
	 *
	 * @param beanName   the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return whether the bean should be considered as autowire candidate
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * 返回指定Bean的已注册BeanDefinition，从而允许对其属性值和构造函数参数值进行访问（可以在Bean工厂后处理期间进行修改）。
	 * <p>返回的BeanDefinition对象不应是副本，而应是工厂中注册的原始定义对象。
	 * 这意味着如有必要，应将其强制转换为更具体的实现类型。
	 * <p> <b>注意：</b>此方法<i>不</i>考虑祖先工厂。 仅用于访问该工厂的本地bean定义.
	 *
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 *                                       defined in this factory
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 返回对此工厂管理的所有bean名称的统一视图。
	 * <p>包括Bean定义名称以及手动注册的*单例实例的名称，并且始终以Bean定义名称排在第一位，
	 * 类似于特定于类型/注释的Bean名称检索的工作方式.
	 *
	 * @return the composite iterator for the bean names view
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 * @since 4.1.2
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * 清除合并的bean定义缓存，删除bean的条目，这些条目尚不适合进行完整的元数据缓存。
	 * <p>通常在更改原始bean定义后触发，例如在应用{@link BeanFactoryPostProcessor}之后。
	 * 请注意，此时已创建的bean的元数据*将保留在周围.
	 *
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 * @since 4.2
	 */
	void clearMetadataCache();

	/**
	 * 冻结所有bean定义，表明已注册的bean定义不会被进一步修改或后处理。 <p>这允许工厂积极地缓存bean定义元数据.
	 */
	void freezeConfiguration();

	/**
	 * 返回此工厂的Bean定义是否被冻结，即不应被修改或进一步处理.
	 *
	 * @return {@code true} if the factory's configuration is considered frozen
	 */
	boolean isConfigurationFrozen();

	/**
	 * 确保所有非延迟初始单例都实例化，同时考虑
	 * {@link org.springframework.beans.factory.FactoryBean FactoryBeans}
	 * 如果需要，通常在出厂设置结束时调用.
	 *
	 * @throws BeansException if one of the singleton beans could not be created.
	 *                        Note: This may have left the factory with some beans already initialized!
	 *                        Call {@link #destroySingletons()} for full cleanup in this case.
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;

}
