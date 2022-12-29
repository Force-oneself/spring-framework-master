package org.springframework.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.demo.aop.tx.TransactionalService;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Force-oneself
 * @Description TxConfig
 * @date 2021-10-06
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({"org.springframework.demo.aop.tx"})
public class TransactionConfig {

	@Bean
	public TransactionalService aopBean() {
		return new TransactionalService();
	}
}
