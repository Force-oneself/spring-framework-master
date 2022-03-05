package org.springframework.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.demo.bean.AopBean;
import org.springframework.demo.bean.AutowiredBean;
import org.springframework.demo.bean.ChildBean;
import org.springframework.demo.bean.DemoFactoryBean;
import org.springframework.demo.bean.ExpandBean;

/**
 * @author Force-oneself
 * @description BeanConfig.java
 * @date 2021-07-06 18:26
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"org.springframework.demo.aop"})
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

    @Bean
    public AopBean aopBean() {
        return new AopBean();
    }

//    @Bean
    public DemoFactoryBean demoFactoryBean() {
        return new DemoFactoryBean();
    }
}
