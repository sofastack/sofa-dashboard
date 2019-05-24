package com.alipay.dashboard.demo.service;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import org.springframework.stereotype.Service;

/**
 * Demo Service Provider
 */
@Service
@SofaService(bindings = @SofaServiceBinding(bindingType = "bolt"))
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return String.format("Hello, %s", name);
    }

}
