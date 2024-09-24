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
        UserInterface user = (UserInterface) context.getBean("userService");
//        System.out.println(user);
        user.test();




    }
}
