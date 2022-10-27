package org.springframework.demo.aop.tx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Force-oneself
 * @Description TxConfig
 * @date 2021-10-06
 */
@Configuration
@EnableTransactionManagement
@ComponentScan
public class TxConfig {
}
