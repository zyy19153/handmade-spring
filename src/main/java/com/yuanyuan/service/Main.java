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
        UserService user = (UserService) context.getBean("user");
        UserService user2 = (UserService) context.getBean("user");
        System.out.println(user);
        System.out.println(user2);


//        System.out.println("simpleName = " + MyApplicationContext.class.getSimpleName());
//        System.out.println("Name = " + MyApplicationContext.class.getName());
    }
}
