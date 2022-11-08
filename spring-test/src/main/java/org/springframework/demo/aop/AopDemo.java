package org.springframework.demo.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.demo.aop.tx.TxBean;
import org.springframework.demo.aop.tx.TxConfig;
import org.springframework.demo.bean.AopBean;
import org.springframework.demo.bean.AopJdkService;
import org.springframework.demo.config.BeanConfig;

/**
 * @author Force-oneself
 * @description AopDemo
 * @date 2021-07-12
 **/
public class AopDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
		AopJdkService bean = context.getBean(AopJdkService.class);
        bean.aop();
//		TxBean bean = context.getBean(TxBean.class);
//		bean.tx();
	}
}
