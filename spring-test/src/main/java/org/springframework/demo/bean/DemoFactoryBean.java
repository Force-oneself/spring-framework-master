package org.springframework.demo.bean;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author Force-oneself
 * @description DemoFactoryBean
 * @date 2021-07-13
 **/
public class DemoFactoryBean implements FactoryBean<MyFactoryBean> {

    @Override
    public MyFactoryBean getObject() throws Exception {
        return new MyFactoryBean();
    }

    @Override
    public Class<?> getObjectType() {
        return MyFactoryBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
