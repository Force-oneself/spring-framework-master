package org.springframework.demo.aop;

/**
 * AopJdkBean
 *
 * @author Force-oneself
 * @date 2022-07-29
 */
public class AopJdkServiceImpl implements AopJdkService {

	@Override
	public void aop() {
		System.out.println("JDK 目标方法执行");
	}
}
