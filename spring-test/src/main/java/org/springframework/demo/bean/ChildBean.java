package org.springframework.demo.bean;

/**
 * @author Force-oneself
 * @description ChildBean
 * @date 2021-07-08
 **/
public class ChildBean extends ParentBean{

    private AutowiredBean autowiredBean;

    public ChildBean(AutowiredBean autowiredBean) {
        this.autowiredBean = autowiredBean;
    }
}
