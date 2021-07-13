package org.springframework.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.demo.config.BeanConfig;

/**
* @description SpringExpandDemo.java
* @author Force-oneself
* @date 2021-07-06 18:26
*/
public class SpringExpandDemo {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        annotationStart();
    }

    private static void xmlStart() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-bean");
    }

    private static void annotationStart() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
    }
}
