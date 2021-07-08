package org.springframework.mock.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.demo.bean.AutowiredBean;
import org.springframework.mock.demo.bean.ChildBean;
import org.springframework.mock.demo.bean.ExpandBean;

/**
 * @author Force-oneself
 * @description BeanConfig.java
 * @date 2021-07-06 18:26
 */
@Configuration
@ComponentScan({"org.springframework.mock.demo"})
public class BeanConfig {

    @Bean
    public ExpandBean expandBean() {
        ExpandBean expandBean = new ExpandBean();
        expandBean.setAge(123);
        expandBean.setName("ExpandBean");
        return expandBean;
    }

    @Bean
    public AutowiredBean autowiredBean() {
        AutowiredBean autowiredBean = new AutowiredBean();
        autowiredBean.setName("AutowiredBean");
        return autowiredBean;
    }

    @Bean
    public ChildBean childBean() {
        return new ChildBean(autowiredBean());
    }
}
