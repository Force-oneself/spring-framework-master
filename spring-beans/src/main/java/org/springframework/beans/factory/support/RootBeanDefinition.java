/*
 * Copyright 2002-2020 the original author or authors.
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 根bean定义表示合并的bean定义，该定义在运行时支持Spring BeanFactory中的特定bean。
 * 它可能是由*彼此继承的多个原始bean定义创建的，
 * 通常注册为{@link GenericBeanDefinition GenericBeanDefinitions}。
 * 根bean定义本质上是运行时的“统一” bean定义视图.
 *
 * <p>在配置阶段，根Bean定义也可以用于注册单个Bean定义。但是，从Spring 2.5开始，
 * 以编程方式注册 bean定义的首选方法是{@link GenericBeanDefinition}类。
 * GenericBeanDefinition的优点是它允许动态定义父依赖关系，而不是将角色“硬编码”为根bean定义.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

	@Nullable
	private BeanDefinitionHolder decoratedDefinition;

	@Nullable
	private AnnotatedElement qualifiedElement;

	/**
	 * 确定是否需要重新合并定义.
	 */
	volatile boolean stale;

	boolean allowCaching = true;

	boolean isFactoryMethodUnique;

	@Nullable
	volatile ResolvableType targetType;

	/**
	 * 包可见的字段，用于缓存给定bean定义的确定的Class.
	 */
	@Nullable
	volatile Class<?> resolvedTargetType;

	/**
	 * 如果bean是FactoryBean，则为软件包可见的字段用于缓存.
	 */
	@Nullable
	volatile Boolean isFactoryBean;

	/**
	 * 程序包可见的字段，用于缓存通用类型的工厂方法的返回类型.
	 */
	@Nullable
	volatile ResolvableType factoryMethodReturnType;

	/**
	 * 程序包可见的字段，用于缓存唯一的工厂方法以供内省.
	 */
	@Nullable
	volatile Method factoryMethodToIntrospect;

	/**
	 * 以下四个构造函数字段的通用锁.
	 */
	final Object constructorArgumentLock = new Object();

	/**
	 * 程序包可见的字段，用于缓存已解析的构造函数或工厂方法.
	 */
	@Nullable
	Executable resolvedConstructorOrFactoryMethod;

	/**
	 * 包可见的字段，将构造函数参数标记为已解析.
	 */
	boolean constructorArgumentsResolved = false;

	/**
	 * 程序包可见的字段，用于缓存完全解析的构造函数参数.
	 */
	@Nullable
	Object[] resolvedConstructorArguments;

	/**
	 * 包可见的字段，用于缓存部分准备好的构造函数参数.
	 */
	@Nullable
	Object[] preparedConstructorArguments;

	/**
	 * 以下两个post-processing字段的通用锁.
	 */
	final Object postProcessingLock = new Object();

	/**
	 * 包可见的字段，指示已应用MergedBeanDefinitionPostProcessor.
	 */
	boolean postProcessed = false;

	/**
	 * 程序包可见的字段，指示实例化之前的后处理器已启动.
	 */
	@Nullable
	volatile Boolean beforeInstantiationResolved;

	@Nullable
	private Set<Member> externallyManagedConfigMembers;

	@Nullable
	private Set<String> externallyManagedInitMethods;

	@Nullable
	private Set<String> externallyManagedDestroyMethods;


	/**
	 * 创建一个新的RootBeanDefinition，通过其bean 属性和配置方法进行配置.
	 *
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * 为单例创建一个新的RootBeanDefinition.
	 *
	 * @param beanClass the class of the bean to instantiate
	 * @see #setBeanClass
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * 为单例bean创建一个新的RootBeanDefinition，通过调用给定的提供者（可能是lambda或方法引用）来构造每个实例*.
	 *
	 * @param beanClass        the class of the bean to instantiate
	 * @param instanceSupplier the supplier to construct a bean instance,
	 *                         as an alternative to a declaratively specified factory method
	 * @see #setInstanceSupplier
	 * @since 5.0
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * 为有作用域的bean创建一个新的RootBeanDefinition，通过调用给定的提供者（可能是lambda或方法引用）来构造每个实例*.
	 *
	 * @param beanClass        the class of the bean to instantiate
	 * @param scope            the name of the corresponding scope
	 * @param instanceSupplier the supplier to construct a bean instance,
	 *                         as an alternative to a declaratively specified factory method
	 * @see #setInstanceSupplier
	 * @since 5.0
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setScope(scope);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * 使用给定的自动装配模式为单例创建一个新的RootBeanDefinition.
	 *
	 * @param beanClass       the class of the bean to instantiate
	 * @param autowireMode    by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 *                        (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * 为单例创建一个新的RootBeanDefinition，提供构造函数参数和属性值.
	 *
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs     the constructor argument values to apply
	 * @param pvs       the property values to apply
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs,
							  @Nullable MutablePropertyValues pvs) {

		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * 为单例创建一个新的RootBeanDefinition，提供构造函数参数和属性值。  <p>采用一个bean类名以避免急于加载该bean类.
	 *
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * 为单例创建一个新的RootBeanDefinition，提供构造函数参数和属性值。 <p>采用一个bean类名以避免急于加载该bean类.
	 *
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs         the constructor argument values to apply
	 * @param pvs           the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * 创建一个新的RootBeanDefinition作为给定bean定义的深层副本.
	 *
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		super(original);
		this.decoratedDefinition = original.decoratedDefinition;
		this.qualifiedElement = original.qualifiedElement;
		this.allowCaching = original.allowCaching;
		this.isFactoryMethodUnique = original.isFactoryMethodUnique;
		this.targetType = original.targetType;
		this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
	}

	/**
	 * 创建一个新的RootBeanDefinition作为给定 bean定义的深层副本.
	 *
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
	}


	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setParentName(@Nullable String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}

	/**
	 * 注册由此bean定义修饰的目标定义.
	 */
	public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	/**
	 * 返回由此bean定义修饰的目标定义（如果有）.
	 */
	@Nullable
	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}

	/**
	 * 指定{@link AnnotatedElement}定义限定符，以代替目标类或工厂方法.
	 *
	 * @see #setTargetType(ResolvableType)
	 * @see #getResolvedFactoryMethod()
	 * @since 4.3.3
	 */
	public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
		this.qualifiedElement = qualifiedElement;
	}

	/**
	 * 返回定义限定符的{@link AnnotatedElement}（如果有）。 否则，将检查工厂方法和目标类.
	 *
	 * @since 4.3.3
	 */
	@Nullable
	public AnnotatedElement getQualifiedElement() {
		return this.qualifiedElement;
	}

	/**
	 * 指定此Bean定义的包含泛型的目标类型，如果事先知道的话.
	 *
	 * @since 4.3.3
	 */
	public void setTargetType(ResolvableType targetType) {
		this.targetType = targetType;
	}

	/**
	 * 指定此Bean定义的目标类型（如果事先知道）.
	 *
	 * @since 3.2.2
	 */
	public void setTargetType(@Nullable Class<?> targetType) {
		this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
	}

	/**
	 * 返回此bean定义的目标类型（如果已知）（预先指定或在第一次实例化时解析）.
	 *
	 * @since 3.2.2
	 */
	@Nullable
	public Class<?> getTargetType() {
		if (this.resolvedTargetType != null) {
			return this.resolvedTargetType;
		}
		ResolvableType targetType = this.targetType;
		return (targetType != null ? targetType.resolve() : null);
	}

	/**
	 * 从运行时缓存的类型信息或从配置时返回一个针对此bean定义的{@link ResolvableType}
	 * {@link #setTargetType（ResolvableType）}或{@link #setBeanClass（Class）}，还考虑了已解决工厂方法定义.
	 *
	 * @see #setTargetType(ResolvableType)
	 * @see #setBeanClass(Class)
	 * @see #setResolvedFactoryMethod(Method)
	 * @since 5.1
	 */
	@Override
	public ResolvableType getResolvableType() {
		ResolvableType targetType = this.targetType;
		if (targetType != null) {
			return targetType;
		}
		ResolvableType returnType = this.factoryMethodReturnType;
		if (returnType != null) {
			return returnType;
		}
		Method factoryMethod = this.factoryMethodToIntrospect;
		if (factoryMethod != null) {
			return ResolvableType.forMethodReturnType(factoryMethod);
		}
		return super.getResolvableType();
	}

	/**
	 * 确定用于默认构造的首选构造函数（如果有）。 如有必要，构造函数参数将自动装配.
	 *
	 * @return one or more preferred constructors, or {@code null} if none
	 * (in which case the regular no-arg default constructor will be called)
	 * @since 5.1
	 */
	@Nullable
	public Constructor<?>[] getPreferredConstructors() {
		return null;
	}

	/**
	 * 指定引用非重载方法的工厂方法名称.
	 */
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	/**
	 * 指定引用重载方法的工厂方法名称.
	 *
	 * @since 5.2
	 */
	public void setNonUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = false;
	}

	/**
	 * 检查给定的候选人是否有资格作为工厂方法.
	 */
	public boolean isFactoryMethod(Method candidate) {
		return candidate.getName().equals(getFactoryMethodName());
	}

	/**
	 * 在此bean定义上为工厂方法设置一个已解析的Java方法.
	 *
	 * @param method the resolved factory method, or {@code null} to reset it
	 * @since 5.2
	 */
	public void setResolvedFactoryMethod(@Nullable Method method) {
		this.factoryMethodToIntrospect = method;
	}

	/**
	 * 返回解析的工厂方法作为Java方法对象（如果有）.
	 *
	 * @return the factory method, or {@code null} if not found or not resolved yet
	 */
	@Nullable
	public Method getResolvedFactoryMethod() {
		return this.factoryMethodToIntrospect;
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedConfigMembers == null) {
				this.externallyManagedConfigMembers = new HashSet<>(1);
			}
			this.externallyManagedConfigMembers.add(configMember);
		}
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedConfigMembers != null &&
					this.externallyManagedConfigMembers.contains(configMember));
		}
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedInitMethods == null) {
				this.externallyManagedInitMethods = new HashSet<>(1);
			}
			this.externallyManagedInitMethods.add(initMethod);
		}
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedInitMethods != null &&
					this.externallyManagedInitMethods.contains(initMethod));
		}
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedDestroyMethods == null) {
				this.externallyManagedDestroyMethods = new HashSet<>(1);
			}
			this.externallyManagedDestroyMethods.add(destroyMethod);
		}
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedDestroyMethods != null &&
					this.externallyManagedDestroyMethods.contains(destroyMethod));
		}
	}


	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}

}
