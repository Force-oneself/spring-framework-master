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

package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface that maps from URLs to beans with names that start with a slash ("/"),
 * similar to how Struts maps URLs to action names.
 *
 * <p>This is the default implementation used by the
 * {@link org.springframework.web.servlet.DispatcherServlet}, along with
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * Alternatively, {@link SimpleUrlHandlerMapping} allows for customizing a
 * handler mapping declaratively.
 *
 * <p>The mapping is from URL to bean name. Thus an incoming URL "/foo" would map
 * to a handler named "/foo", or to "/foo /foo2" in case of multiple mappings to
 * a single handler.
 *
 * <p>Supports direct matches (given "/test" -&gt; registered "/test") and "*"
 * matches (given "/test" -&gt; registered "/t*"). Note that the default is
 * to map within the current servlet mapping if applicable; see the
 * {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property for details.
 * For details on the pattern options, see the
 * {@link org.springframework.util.AntPathMatcher} javadoc.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

	/**
	 * 检查给定 bean 的名称和别名以获取 URL，以“/”开头。
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		List<String> urls = new ArrayList<>();
		// 看Bean的名字是不是带'/'开头的
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		// 别名是不是也带'/ '
		String[] aliases = obtainApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (alias.startsWith("/")) {
				urls.add(alias);
			}
		}
		return StringUtils.toStringArray(urls);
	}

}
