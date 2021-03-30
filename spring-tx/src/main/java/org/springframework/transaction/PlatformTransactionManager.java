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

package org.springframework.transaction;

import org.springframework.lang.Nullable;

/**
 * 这是Spring命令式事务基础结构的中心接口。应用程序可以直接使用它，但是它并不是主要用作API：
 * 通常，应用程序可以使用TransactionTemplate或通过AOP进行声明式事务划分。
 *
 * <p>For implementors, it is recommended to derive from the provided
 * {@link org.springframework.transaction.support.AbstractPlatformTransactionManager}
 * class, which pre-implements the defined propagation behavior and takes care
 * of transaction synchronization handling. Subclasses have to implement
 * template methods for specific states of the underlying transaction,
 * for example: begin, suspend, resume, commit.
 *
 * <p>The default implementations of this strategy interface are
 * {@link org.springframework.transaction.jta.JtaTransactionManager} and
 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager},
 * which can serve as an implementation guide for other transaction strategies.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.interceptor.TransactionInterceptor
 * @see org.springframework.transaction.ReactiveTransactionManager
 * @since 16.05.2003
 */
public interface PlatformTransactionManager extends TransactionManager {

	/**
	 * 根据指定的传播行为，返回当前活动的事务或创建新的事务.
	 * <p>请注意，诸如隔离级别或超时之类的参数将仅应用于新事务，因此在参与活动事务时将被忽略。
	 * <p>此外，并不是每个事务管理器都支持所有事务定义设置：适当的事务管理器实现*当遇到不受支持的设置时，
	 * 将引发异常。 <p>上述规则的一个例外是只读标志，如果不支持显式只读模式，则应忽略该标志。
	 * 本质上，只读标志只是潜在优化的提示.
	 *
	 * @param definition the TransactionDefinition instance (can be {@code null} for defaults),
	 *                   describing propagation behavior, isolation level, timeout etc.
	 * @return transaction status object representing the new or current transaction
	 * @throws TransactionException             in case of lookup, creation, or system errors
	 * @throws IllegalTransactionStateException if the given transaction definition
	 *                                          cannot be executed (for example, if a currently active transaction is in
	 *                                          conflict with the specified propagation behavior)
	 * @see TransactionDefinition#getPropagationBehavior
	 * @see TransactionDefinition#getIsolationLevel
	 * @see TransactionDefinition#getTimeout
	 * @see TransactionDefinition#isReadOnly
	 */
	TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
			throws TransactionException;

	/**
	 * 根据其状态提交给定的交易。如果已通过编程将事务*标记为仅回滚，请执行回滚。<p>如果交易不是新的交易，
	 * 则忽略提交*以适当地参与周围的交易。如果先前的事务*已被暂停以能够创建新的事务，则在提交新事务之后恢复上一个事务。
	 * <p>请注意，当commit调用完成时，无论正常运行还是抛出异常，都必须完全完成事务并进行清理。在这种情况下，不应进行回滚调用。
	 * <p>如果此方法引发了非TransactionException的异常，则提交前的一些错误导致提交尝试失败。
	 * 例如，一个O / R映射工具可能试图在提交之前立即刷新对数据库的更改，从而导致DataAccessException导致事务失败。
	 * 在这种情况下，原始异常将传播到此commit方法的调用者.
	 *
	 * @param status object returned by the {@code getTransaction} method
	 * @throws UnexpectedRollbackException      in case of an unexpected rollback
	 *                                          that the transaction coordinator initiated
	 * @throws HeuristicCompletionException     in case of a transaction failure
	 *                                          caused by a heuristic decision on the side of the transaction coordinator
	 * @throws TransactionSystemException       in case of commit or system errors
	 *                                          (typically caused by fundamental resource failures)
	 * @throws IllegalTransactionStateException if the given transaction
	 *                                          is already completed (that is, committed or rolled back)
	 * @see TransactionStatus#setRollbackOnly
	 */
	void commit(TransactionStatus status) throws TransactionException;

	/**
	 * 执行给定事务的回滚。<p>如果该交易不是新交易，则仅将其设置为仅回滚，以便适当地参与周围的交易。如果先前的交易
	 * 已被暂停以能够创建新的交易，则在回滚新交易之后，恢复先前的*交易。<p> <b>如果提交引发异常，请勿在事务上调用回滚。</b>
	 * 即使提交异常，即使提交返回，事务也已经完成并清除。因此，提交失败后的回滚调用将导致IllegalTransactionStateException.
	 *
	 * @param status object returned by the {@code getTransaction} method
	 * @throws TransactionSystemException       in case of rollback or system errors
	 *                                          (typically caused by fundamental resource failures)
	 * @throws IllegalTransactionStateException if the given transaction
	 *                                          is already completed (that is, committed or rolled back)
	 */
	void rollback(TransactionStatus status) throws TransactionException;

}
