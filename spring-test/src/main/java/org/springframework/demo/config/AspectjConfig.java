package org.springframework.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.demo.aop.AopCGLibService;
import org.springframework.demo.aop.AopJdkService;
import org.springframework.demo.aop.AopJdkServiceImpl;

/**
 * AspectjConfig
 *
 * @author Force-oneself
 * @date 2022-12-29
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"org.springframework.demo.aop.aspectj"})
public class AspectjConfig {

	@Bean
	public AopJdkService aopJdkBean() {
		return new AopJdkServiceImpl();
	}

	@Bean
	public AopCGLibService aopCGLibService() {
		return new AopCGLibService();
	}

}
