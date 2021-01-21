package org.springframework.test.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description:
 * @Author heyq
 * @Date 2021-01-21
 **/
public class BeanLifeCycle {

	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-${username}.xml");
		Object person = ac.getBean("person");

	}
}
