package com.yuanyuan.service;

import com.yuanyuan.spring.MyApplicationContext;

public class Main {
    public static void main(String[] args) {
        MyApplicationContext context = null;

        try {
            context = new MyApplicationContext(AppCofig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 单例例子
        UserService user = (UserService) context.getBean("userService");
        UserService user2 = (UserService) context.getBean("userService");
        System.out.println(user);
        System.out.println(user2);
        System.out.println(user.getBeanName());



        user.test();
        user.test();

        MyBeanPostProcessor mpp = (MyBeanPostProcessor) context.getBean("myBeanPostProcessor");
        MyBeanPostProcessor mpp1 = (MyBeanPostProcessor) context.getBean("myBeanPostProcessor");
        System.out.println(mpp);
        System.out.println(mpp1);
    }
}
