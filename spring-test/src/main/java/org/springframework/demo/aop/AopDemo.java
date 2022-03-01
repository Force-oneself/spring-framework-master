package org.springframework.demo.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.demo.aop.tx.TxConfig;
import org.springframework.demo.bean.AopBean;
import org.springframework.demo.config.BeanConfig;

/**
 * @author Force-oneself
 * @description AopDemo
 * @date 2021-07-12
 **/
public class AopDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
        AopBean bean = context.getBean(AopBean.class);
        bean.aop();
    }
}
