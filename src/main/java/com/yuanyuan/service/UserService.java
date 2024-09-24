package com.yuanyuan.service;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.yuanyuan.spring.Autowired;
import com.yuanyuan.spring.Component;
import com.yuanyuan.spring.Scope;
import com.yuanyuan.spring.ScopeEnum;

@Component("user")
@Scope(ScopeEnum.SINGLETON)
public class UserService {

    @Autowired
    private OtherService otherService;

    public void test() {
        System.out.println(otherService);
    }
}
