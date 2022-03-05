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

package org.springframework.aop.aspectj;

import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;

/**
 * Utility methods for working with AspectJ proxies.
 *
 * @author Rod Johnson
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AspectJProxyUtils {

	/**
	 * 如有必要，添加特殊顾问以使用包含 AspectJ 顾问的代理链：具体而言，{@link ExposeInvocationInterceptor} 在列表的开头。
	 * <p>这将公开当前的 Spring AOP 调用（某些 AspectJ 切入点匹配所必需的）并使当前的 AspectJ JoinPoint 可用。
	 * 如果顾问链中没有 AspectJ 顾问，则调用将无效。
	 *
	 * @param advisors the advisors available
	 * @return {@code true} if an {@link ExposeInvocationInterceptor} was added to the list,
	 * otherwise {@code false}
	 */
	public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
		// Don't add advisors to an empty list; may indicate that proxying is just not required
		// 不要将顾问添加到空列表中；可能表明不需要代理
		if (!advisors.isEmpty()) {
			boolean foundAspectJAdvice = false;
			for (Advisor advisor : advisors) {
				// Be careful not to get the Advice without a guard, as this might eagerly
				// instantiate a non-singleton AspectJ aspect...
				// 小心不要在没有警卫的情况下获得建议，因为这可能会急切地实例化非单例 AspectJ 方面......
				if (isAspectJAdvice(advisor)) {
					foundAspectJAdvice = true;
					break;
				}
			}
			if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
				advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
				return true;
			}
		}
		return false;
	}

	/**
	 * 确定给定的Advisor是否包含AspectJ建议.
	 *
	 * @param advisor the Advisor to check
	 */
	private static boolean isAspectJAdvice(Advisor advisor) {
		return (advisor instanceof InstantiationModelAwarePointcutAdvisor
				|| advisor.getAdvice() instanceof AbstractAspectJAdvice
				|| (advisor instanceof PointcutAdvisor
					&& ((PointcutAdvisor) advisor).getPointcut() instanceof AspectJExpressionPointcut));
	}

}
