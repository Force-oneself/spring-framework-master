package org.springframework.demo.bean;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Force-oneself
 * @description AopBean
 * @date 2021-07-12
 **/
public class AopBean {

	@Transactional(rollbackFor = Exception.class)
    public void aop() {
        System.out.println("target method");
    }
}
