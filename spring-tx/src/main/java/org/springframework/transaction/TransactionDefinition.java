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

import org.springframework.lang.Nullable;

/**
 * Interface that defines Spring-compliant transaction properties.
 * Based on the propagation behavior definitions analogous to EJB CMT attributes.
 *
 * <p>Note that isolation level and timeout settings will not get applied unless
 * an actual new transaction gets started. As only {@link #PROPAGATION_REQUIRED},
 * {@link #PROPAGATION_REQUIRES_NEW} and {@link #PROPAGATION_NESTED} can cause
 * that, it usually doesn't make sense to specify those settings in other cases.
 * Furthermore, be aware that not all transaction managers will support those
 * advanced features and thus might throw corresponding exceptions when given
 * non-default values.
 *
 * <p>The {@link #isReadOnly() read-only flag} applies to any transaction context,
 * whether backed by an actual resource transaction or operating non-transactionally
 * at the resource level. In the latter case, the flag will only apply to managed
 * resources within the application, such as a Hibernate {@code Session}.
 *
 * @author Juergen Hoeller
 * @see PlatformTransactionManager#getTransaction(TransactionDefinition)
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 * @since 08.05.2003
 */
public interface TransactionDefinition {

	/**
	 * S支持当前交易；如果不存在，请创建一个新的。 类似于同名的EJB事务属性。
	 * <p>这通常是事务定义的默认设置，*并且通常定义事务同步范围.
	 */
	int PROPAGATION_REQUIRED = 0;

	/**
	 * 支持当前交易；如果不存在，则以非事务方式执行。类似于同名的EJB事务属性。
	 * <p> <b>注意：</ b>对于具有事务同步的事务管理器，{@code PROPAGATION_SUPPORTS}
	 * 与没有事务稍有不同，因为它定义了同步可能适用的事务范围。
	 * 因此，将为整个指定范围共享相同的资源（JDBC {@code连接}，Hibernate {@code会话}等）。
	 * 请注意，确切的行为取决于事务管理器的实际同步配置！
	 * <p>通常，请小心使用{@code PROPAGATION_SUPPORTS}！特别是，不要依赖{@code PROPAGATION_SUPPORTS}
	 * 范围内的{@code PROPAGATION_REQUIRED}或{@code PROPAGATION_REQUIRES_NEW}
	 * （可能会在运行时导致同步冲突）。如果无法避免此类嵌套，请确保适当地配置您的事务管理器（通常切换到*“实际事务的同步”）.
	 *
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
	 */
	int PROPAGATION_SUPPORTS = 1;

	/**
	 * 支持当前交易；如果当前事务不存在，则引发异常。类似于同名的EJB事务属性。
	 * <p>请注意，{@code PROPAGATION_MANDATORY} 范围内的事务同步将始终由周围的事务驱动.
	 */
	int PROPAGATION_MANDATORY = 2;

	/**
	 * 创建一个新事务，如果存在则暂停当前事务。 类似于同名的EJB事务属性。
	 * <p> <b>注意：</ b>并非在所有交易管理器上开箱即用进行实际的交易暂停。
	 * 这尤其适用于{@link org.springframework.transaction.jta.JtaTransactionManager}，
	 * 要求{@code javax.transaction.TransactionManager}对其可用（这是标准Java中特定于服务器的） EE）。
	 * <p> {@code PROPAGATION_REQUIRES_NEW}范围始终定义自己的事务同步。现有同步将被暂停并适当恢复.
	 *
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_REQUIRES_NEW = 3;

	/**
	 * 不支持当前交易；而是始终以非事务方式执行。类似于同名的EJB事务属性。
	 * <p> <b>注意：</ b>并非在所有交易管理器上开箱即用*进行实际的交易暂停。这尤其适用于
	 * {@link org.springframework.transaction.jta.JtaTransactionManager}，
	 * 要求{@code javax.transaction.TransactionManager}对其可用（这是标准Java中特定于服务器的） EE）。
	 * <p>请注意，在{{code PROPAGATION_NOT_SUPPORTED}范围内，<i>不能</i>使用事务同步。现有同步将被暂停并适当恢复.
	 *
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_NOT_SUPPORTED = 4;

	/**
	 * 不支持当前交易；如果当前事务存在，则引发异常。类似于同名的EJB事务属性。
	 * <p>请注意，在{{code PROPAGATION_NEVER}范围内<i>不</i>不能进行事务同步.
	 */
	int PROPAGATION_NEVER = 5;

	/**
	 * 如果当前事务存在，则在嵌套事务中执行，否则的行为类似于{@link #PROPAGATION_REQUIRED}。
	 * EJB中没有*类似功能。<p> <b>注意：</b>嵌套事务的实际创建仅适用于特定的事务管理器。
	 * 开箱即用，仅在使用JDBC 3.0驱动程序时才适用于JDBC
	 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}。
	 * 一些JTA提供程序可能也支持*嵌套事务.
	 *
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 */
	int PROPAGATION_NESTED = 6;


	/**
	 * 使用基础数据存储的默认隔离级别。所有其他级别对应于JDBC隔离级别.
	 *
	 * @see java.sql.Connection
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * 表示可能发生脏读，不可重复读和幻像读。<p>此级别允许在提交该行的任何更改（“脏读”）之前，
	 * 由一个事务更改的行由另一*事务读取。如果任何更改被回滚，则第二个事务将检索到无效的行.
	 *
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 */
	int ISOLATION_READ_UNCOMMITTED = 1;  // same as java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

	/**
	 * 指示防止脏读；可能会发生不可重复的读取和幻像读取。<p>此级别仅禁止事务读取行包含未提交的更改.
	 *
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 */
	int ISOLATION_READ_COMMITTED = 2;  // same as java.sql.Connection.TRANSACTION_READ_COMMITTED;

	/**
	 * 指示防止脏读和不可重复读；可能会发生幻像读取。<p>此级别禁止事务读取其中包含未提交更改的行，
	 * 并且还禁止以下情况：
	 * 一个事务读取一行，第二个事务更改该行，第一个事务重新读取该行，第二次获得不同的值（“不可重复读取”）.
	 *
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 */
	int ISOLATION_REPEATABLE_READ = 4;  // same as java.sql.Connection.TRANSACTION_REPEATABLE_READ;

	/**
	 * 表示防止脏读，不可重复读和幻像读。<p>该级别包括{@link #ISOLATION_REPEATABLE_READ} 中的禁止条件，
	 * 并进一步禁止一种事务读取满足@ {WHERE}条件的所有行，第二个事务插入满足以下条件的行* @code WHERE}条件，
	 * 第一个事务*将重新读取相同的条件，并在第二次读取中检索附加的“幻像”行.
	 *
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 */
	int ISOLATION_SERIALIZABLE = 8;  // same as java.sql.Connection.TRANSACTION_SERIALIZABLE;


	/**
	 * 使用基础事务系统的默认超时，如果不支持超时，则使用或不使用.
	 */
	int TIMEOUT_DEFAULT = -1;


	/**
	 * 返回传播行为。<p>必须返回在{@link TransactionDefinition此接口}上定义的
	 * {@code PROPAGATION_XXX}常量之一。<p>默认值为{@link #PROPAGATION_REQUIRED}.
	 *
	 * @return the propagation behavior
	 * @see #PROPAGATION_REQUIRED
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
	 */
	default int getPropagationBehavior() {
		return PROPAGATION_REQUIRED;
	}

	/**
	 * 返回隔离级别。<p>必须返回在{@link TransactionDefinition此接口}上定义的{@code ISOLATION_XXX}常量之一。
	 * 这些常量旨在*匹配{@link java.sql.Connection}上相同常量的值。<p>专门设计用于{@link #PROPAGATION_REQUIRED}
	 * 或{@link #PROPAGATION_REQUIRES_NEW}，因为它仅适用于新近开始的交易。如果希望隔离级别声明在参与具有不同隔离级别
	 * 的现有事务时被拒绝，请考虑在事务管理器上将“ validateExistingTransactions”标志切换为*“ true”。
	 * <p>默认值为{@link #ISOLATION_DEFAULT}。请注意，当给定{{link #ISOLATION_DEFAULT}以外的任何其他级别时，
	 * 不支持自定义隔离级别的事务管理器将抛出异常。.
	 *
	 * @return the isolation level
	 * @see #ISOLATION_DEFAULT
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 */
	default int getIsolationLevel() {
		return ISOLATION_DEFAULT;
	}

	/**
	 * 返回事务超时。<p>必须返回几秒钟，即{@link #TIMEOUT_DEFAULT}。
	 * <p>专门设计用于{@link #PROPAGATION_REQUIRED}或{@link #PROPAGATION_REQUIRES_NEW}，
	 * 因为它仅适用于新近开始的交易。<p>请注意，不支持超时的事务管理器在给定{{link #TIMEOUT_DEFAULT}
	 * 以外的任何其他超时时，将引发异常。<p>默认值为{@link #TIMEOUT_DEFAULT}.
	 *
	 * @return the transaction timeout
	 */
	default int getTimeout() {
		return TIMEOUT_DEFAULT;
	}

	/**
	 * 返回是否优化为只读事务。<p>只读标志适用于任何事务上下文，无论是由实际资源事务
	 * （{@link #PROPAGATION_REQUIRED} / {@link #PROPAGATION_REQUIRES_NEW}）支持还是在资源级别以非事务方式
	 * 操作（ {@link #PROPAGATION_SUPPORTS}）。在后一种情况下，标志仅适用于应用程序内的托管资源，
	 * 例如Hibernate {@code Session}。 <p>这仅是实际交易子系统的提示； 它<i>不一定</i>会导致写访问尝试失败。
	 * 不能解释只读提示的事务管理器*当被要求进行只读事务时，<i>不会</i>引发异常.
	 *
	 * @return {@code true} if the transaction is to be optimized as read-only
	 * ({@code false} by default)
	 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 */
	default boolean isReadOnly() {
		return false;
	}

	/**
	 * 返回此交易的名称。可以为{@code null}。<p>这将用作事务名称，以显示在事务监视器中（如果适用）
	 * （例如，WebLogic的名称）。<p>对于Spring的声明式事务，公开的名称将是{@code完全限定的类名称+“。 +方法名称}（默认情况下）.
	 *
	 * @return the name of this transaction ({@code null} by default}
	 * @see org.springframework.transaction.interceptor.TransactionAspectSupport
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
	 */
	@Nullable
	default String getName() {
		return null;
	}


	// Static builder methods

	/**
	 * 返回带有默认值的不可修改的{@code TransactionDefinition}。<p>出于自定义目的，请使用可修改的
	 * {@link org.springframework.transaction.support.DefaultTransactionDefinition}
	 * instead.
	 *
	 * @since 5.2
	 */
	static TransactionDefinition withDefaults() {
		return StaticTransactionDefinition.INSTANCE;
	}

}
