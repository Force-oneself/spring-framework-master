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

package org.springframework.core;

/**
 * {@link ParameterNameDiscoverer} 策略接口的默认实现，使用 Java 8 标准反射机制（如果可用），
 * 并回退到基于 ASM 的 {@link LocalVariableTableParameterNameDiscoverer} 用于检查类文件中的调试信息。
 * <p>如果存在 Kotlin 反射实现，{@link KotlinReflectionParameterNameDiscoverer}
 * 将首先添加到列表中并用于 Kotlin 类和接口。作为 GraalVM 本机映像编译或运行时，
 * 不使用 {@code KotlinReflectionParameterNameDiscoverer}。
 * <p>可以通过 {@link addDiscoverer(ParameterNameDiscoverer)} 添加更多发现者。
 *
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @author Sam Brannen
 * @see StandardReflectionParameterNameDiscoverer
 * @see LocalVariableTableParameterNameDiscoverer
 * @see KotlinReflectionParameterNameDiscoverer
 * @since 4.0
 */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

	/**
	 * Whether this environment lives within a native image.
	 * Exposed as a private static field rather than in a {@code NativeImageDetector.inNativeImage()} static method due to https://github.com/oracle/graal/issues/2594.
	 *
	 * @see <a href="https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.nativeimage/src/org/graalvm/nativeimage/ImageInfo.java">ImageInfo.java</a>
	 */
	private static final boolean IN_NATIVE_IMAGE = (System.getProperty("org.graalvm.nativeimage.imagecode") != null);

	public DefaultParameterNameDiscoverer() {
		if (KotlinDetector.isKotlinReflectPresent() && !IN_NATIVE_IMAGE) {
			addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
		}
		// 添加使用JDK8的反射工具内省参数名（基于'-parameters'编译器发现）的参数名发现器
		addDiscoverer(new StandardReflectionParameterNameDiscoverer());
		// 添加基于ASM库对Class文件的解析获取LocalVariableTable信息来发现参数名发现器
		addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
	}

}
