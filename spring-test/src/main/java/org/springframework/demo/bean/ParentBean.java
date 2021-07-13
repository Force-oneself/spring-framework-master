package org.springframework.demo.bean;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Force-oneself
 * @description ParentBean
 * @date 2021-07-08
 **/
public class ParentBean {

    protected ExpandBean expandBean;

    @Autowired
    public void setExpandBean(ExpandBean expandBean) {
        this.expandBean = expandBean;
    }
}
