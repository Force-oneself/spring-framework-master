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
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring-bean.xml");
		Object person = ac.getBean("person");
		System.out.println(person);
	}
}
