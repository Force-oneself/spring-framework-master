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

package org.springframework.transaction;

import java.io.Flushable;

/**
 * 交易状态的表示。<p>事务代码可以使用它来检索状态信息，并以编程方式请求回滚（而不是引发导致隐式回滚的异常）。
 * <p>包括{@link SavepointManager}接口，以提供对保存点管理工具的访问。请注意，只有在基础事务管理器支持的情况下，
 * 保存点管理才可用.
 *
 * @author Juergen Hoeller
 * @see #setRollbackOnly()
 * @see PlatformTransactionManager#getTransaction
 * @see org.springframework.transaction.support.TransactionCallback#doInTransaction
 * @see org.springframework.transaction.interceptor.TransactionInterceptor#currentTransactionStatus()
 * @since 27.03.2003
 */
public interface TransactionStatus extends TransactionExecution, SavepointManager, Flushable {

	/**
	 * 返回此事务是否在内部携带一个保存点，即是否已基于保存点将其创建为嵌套事务。
	 * <p>此方法主要用于诊断目的，以及* {@link #isNewTransaction（）}。
	 * 要以编程方式处理自定义保存点，请使用{@link SavepointManager}提供的操作.
	 *
	 * @see #isNewTransaction()
	 * @see #createSavepoint()
	 * @see #rollbackToSavepoint(Object)
	 * @see #releaseSavepoint(Object)
	 */
	boolean hasSavepoint();

	/**
	 * 将基础会话刷新到数据存储（如果适用）：例如，所有受影响的Hibernate / JPA会话。
	 * <p>这实际上只是一个提示，如果底层的事务管理器没有刷新概念，则可能是无操作。
	 * 取决于基础资源，刷新信号可能应用于主资源或事务同步*.
	 */
	@Override
	void flush();

}
