package org.springframework.demo.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.demo.config.AspectjConfig;

/**
 * @author Force-oneself
 * @description AopDemo
 * @date 2021-07-12
 **/
public class AopDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AspectjConfig.class);
		// jdk
		AopJdkService aopJdkService = context.getBean(AopJdkService.class);
		aopJdkService.aop();

		// cglib
//		AopCGLibService aopCGLibService = context.getBean(AopCGLibService.class);
//		aopCGLibService.aop();
	}
}
