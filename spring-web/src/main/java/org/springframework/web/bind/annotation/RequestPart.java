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

package org.springframework.web.bind.annotation;

import java.beans.PropertyEditor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

/**
 * 可用于将“multipart/form-data”请求的一部分与方法参数相关联的注释。
 * <p>支持的方法参数类型包括 {@link MultipartFile} 结合 Spring 的 {@link MultipartResolver} 抽象，
 * {@code javax.servlet.http.Part} 结合 Servlet 3.0 多部分请求，或其他任何方法参数，
 * 考虑到请求部分的“Content-Type”标头，部分的内容通过 {@link HttpMessageConverter} 传递。
 * 这类似于@{@link RequestBody} 根据非多部分常规请求的内容解析参数所做的工作。
 * <p>请注意，@{@link RequestParam} 注释也可用于将“multipart/form-data”请求的一部分与支持相同方法参数类型的方法参数相关联。
 * 主要区别在于，当方法参数不是字符串或原始 {@code MultipartFile} {@code Part} 时，
 * {@code @RequestParam} 依赖于通过注册的 {@link Converter} 或 {@link PropertyEditor}
 * 进行类型转换而 {@link RequestPart} 依赖于 {@link HttpMessageConverter HttpMessageConverters}
 * 考虑到请求部分的“Content-Type”标头。 {@link RequestParam} 可能用于名称-值表单字段，而 {@link RequestPart}
 * 可能用于包含更复杂内容的部分，例如JSON、XML）。
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @see RequestParam
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @since 3.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPart {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the part in the {@code "multipart/form-data"} request to bind to.
	 *
	 * @since 4.2
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 是否需要该部分。
	 * <p>默认为 {@code true}，如果请求中缺少该部分，则会导致抛出异常。
	 * 如果请求中不存在该部分，则如果您更喜欢 {@code null} 值，请将其切换为 {@code false}。
	 */
	boolean required() default true;

}
