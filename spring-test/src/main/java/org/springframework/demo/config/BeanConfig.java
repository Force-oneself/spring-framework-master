package org.springframework.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.demo.aop.AopCGLibService;
import org.springframework.demo.aop.AopJdkServiceImpl;
import org.springframework.demo.aop.AopJdkService;
import org.springframework.demo.aop.tx.TransactionalService;
import org.springframework.demo.bean.*;

/**
 * @author Force-oneself
 * @description BeanConfig.java
 * @date 2021-07-06 18:26
 */
@Configuration
public class BeanConfig {

//    @Bean
    public ExpandBean expandBean() {
        ExpandBean expandBean = new ExpandBean();
        expandBean.setAge(123);
        expandBean.setName("ExpandBean");
        return expandBean;
    }

//    @Bean
    public AutowiredBean autowiredBean() {
        AutowiredBean autowiredBean = new AutowiredBean();
        autowiredBean.setName("AutowiredBean");
        return autowiredBean;
    }

//    @Bean
    public ChildBean childBean() {
        return new ChildBean(autowiredBean());
    }





//    @Bean
    public DemoFactoryBean demoFactoryBean() {
        return new DemoFactoryBean();
    }
}
