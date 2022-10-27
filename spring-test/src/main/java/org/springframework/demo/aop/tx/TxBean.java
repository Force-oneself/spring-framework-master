package org.springframework.demo.aop.tx;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TxBean
 *
 * @author Force-oneself
 * @date 2022-10-27
 */
@Component
public class TxBean {

	@Transactional(rollbackFor = Exception.class)
	public boolean tx() {
		return true;
	}
}
