package com.yuanyuan.service;

import com.yuanyuan.spring.Autowired;
import com.yuanyuan.spring.Component;
import com.yuanyuan.spring.Scope;
import com.yuanyuan.spring.ScopeEnum;

@Component("user")
@Scope(ScopeEnum.SINGLETON)
public class UserService {

    @Autowired
    private OtherService otherService;

    public OtherService getOtherService() {
        return otherService;
    }

    public void setOtherService(OtherService otherService) {
        this.otherService = otherService;
    }
}
