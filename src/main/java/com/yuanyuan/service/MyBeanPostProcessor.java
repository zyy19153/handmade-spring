package com.yuanyuan.service;

import com.yuanyuan.spring.BeanPostProcessor;
import com.yuanyuan.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        if (beanName.startsWith("user"))
            System.out.println(beanName + " postProcess Before Initialization");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        if (beanName.startsWith("user")) {
            System.out.println(beanName + " postProcess After Initialization");
            // 这里是代理对象返回，也就是用代理对象取代原始 bean 对象
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), (proxy, method, args) -> {
                System.out.println("proxy aspect before ========================");
                // 这里第一个参数是 被代理的对象，这一步就是执行被代理对象的代码，所以不是传 proxy 进来！！！
                Object res = method.invoke(bean, args);
                System.out.println("proxy aspect after ========================");
                /*
                这里是 Proxy.newProxyInstance 方法第三个参数的 lambda 方法体，
                也就是当被代理对象执行方法时，我们会如何处理，这里就是写处理逻辑。
                所以，这里的返回值是方法执行的返回值
                        */
                return res;
            });
        }
        return bean;
    }
}

