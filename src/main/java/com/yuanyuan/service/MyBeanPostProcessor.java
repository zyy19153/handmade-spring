package com.yuanyuan.service;

import com.yuanyuan.spring.BeanPostProcessor;
import com.yuanyuan.spring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postProcessBeforeInitialization(String beanName, Object bean) {
        if (beanName.startsWith("user"))
            System.out.println(beanName + " postProcess Before Initialization");
    }

    @Override
    public void postProcessAfterInitialization(String beanName, Object bean) {
        if (beanName.startsWith("user"))
            System.out.println(beanName + " postProcess After Initialization");
    }
}
