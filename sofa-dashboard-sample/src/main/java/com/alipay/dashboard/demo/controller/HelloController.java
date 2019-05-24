package com.alipay.dashboard.demo.controller;

import com.alipay.dashboard.demo.service.HelloService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo Service Invoker
 */
@RestController
public class HelloController {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt"))
    private HelloService service;

    @GetMapping
    public String hello(@RequestParam("name") String name) {
        return service.sayHello(name);
    }

}
