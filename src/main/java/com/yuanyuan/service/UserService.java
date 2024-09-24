package com.yuanyuan.service;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.yuanyuan.spring.*;

@Component()
@Scope(ScopeEnum.SINGLETON)
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OtherService otherService;

    private String beanName;

    public void test() {
        System.out.println(otherService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Initializing bean ...");
    }
}
