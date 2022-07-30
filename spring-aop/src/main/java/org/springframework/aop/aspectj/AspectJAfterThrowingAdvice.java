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

package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.AfterAdvice;
import org.springframework.lang.Nullable;

/**
 * Spring AOP advice wrapping an AspectJ after-throwing advice method.
 *
 * @author Rod Johnson
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
		implements MethodInterceptor, AfterAdvice, Serializable {

	public AspectJAfterThrowingAdvice(
			Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

		super(aspectJBeforeAdviceMethod, pointcut, aif);
	}


	@Override
	public boolean isBeforeAdvice() {
		return false;
	}

	@Override
	public boolean isAfterAdvice() {
		return true;
	}

	@Override
	public void setThrowingName(String name) {
		setThrowingNameNoCheck(name);
	}

	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			// 这里又调回递归方法 ReflectiveMethodInvocation.proceed()
			return mi.proceed();
		}
		catch (Throwable ex) {
			// 发生异常将会执行该操作
			if (shouldInvokeOnThrowing(ex)) {
				invokeAdviceMethod(getJoinPointMatch(), null, ex);
			}
			throw ex;
		}
	}

	/**
	 * 在AspectJ语义中，仅当抛出的异常是给定抛出类型的子类型时，才调用指定抛出子句的建议之后.
	 */
	private boolean shouldInvokeOnThrowing(Throwable ex) {
		return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
	}

}
