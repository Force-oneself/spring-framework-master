package org.springframework.demo.aop.tx;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Force-oneself
 * @description AopBean
 * @date 2021-07-12
 **/
public class TransactionalService {

	@Transactional(rollbackFor = Exception.class)
    public void aop() {
        System.out.println("target method");
    }
}
