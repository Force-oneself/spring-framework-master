/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework.autoproxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for retrieving standard Spring Advisors from a BeanFactory,
 * for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @see AbstractAdvisorAutoProxyCreator
 * @since 2.0.2
 */
public class BeanFactoryAdvisorRetrievalHelper {

    private static final Log logger = LogFactory.getLog(BeanFactoryAdvisorRetrievalHelper.class);

    private final ConfigurableListableBeanFactory beanFactory;

    @Nullable
    private volatile String[] cachedAdvisorBeanNames;


    /**
     * Create a new BeanFactoryAdvisorRetrievalHelper for the given BeanFactory.
     *
     * @param beanFactory the ListableBeanFactory to scan
     */
    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
        this.beanFactory = beanFactory;
    }


    /**
     * 在当前 bean 工厂中查找所有符合条件的 Advisor bean，忽略 FactoryBeans 并排除当前正在创建的 bean。
     *
     * @return {@link org.springframework.aop.Advisor} bean 的列表
     * @see #isEligibleBean
     */
    public List<Advisor> findAdvisorBeans() {
        // 确定 Advisor bean 名称列表（如果尚未缓存）.
        String[] advisorNames = this.cachedAdvisorBeanNames;
        if (advisorNames == null) {
            // 不要在此处初始化 FactoryBeans：我们需要保留所有常规 bean 未初始化以让自动代理创建者应用于它们！
            advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory,
                    Advisor.class, true, false);
            this.cachedAdvisorBeanNames = advisorNames;
        }
        if (advisorNames.length == 0) {
            return new ArrayList<>();
        }

        List<Advisor> advisors = new ArrayList<>();
        for (String name : advisorNames) {
            if (isEligibleBean(name)) {
                // Bean正在创建
                if (this.beanFactory.isCurrentlyInCreation(name)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Skipping currently created advisor '" + name + "'");
                    }
                } else {
                    try {
                        advisors.add(this.beanFactory.getBean(name, Advisor.class));
                    } catch (BeanCreationException ex) {
                        Throwable rootCause = ex.getMostSpecificCause();
                        if (rootCause instanceof BeanCurrentlyInCreationException) {
                            BeanCreationException bce = (BeanCreationException) rootCause;
                            String bceBeanName = bce.getBeanName();
                            if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("Skipping advisor '" + name +
                                            "' with dependency on currently created bean: " + ex.getMessage());
                                }
                                // Ignore: indicates a reference back to the bean we're trying to advise.
                                // We want to find advisors other than the currently created bean itself.
                                continue;
                            }
                        }
                        throw ex;
                    }
                }
            }
        }
        return advisors;
    }

    /**
     * 确定具有给定名称的方面 bean 是否合格。<p>默认实现总是返回 {@code true}。
     *
     * @param beanName the name of the aspect bean
     * @return whether the bean is eligible
     */
    protected boolean isEligibleBean(String beanName) {
        return true;
    }

}
