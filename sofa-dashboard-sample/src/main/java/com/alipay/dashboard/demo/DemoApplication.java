package com.alipay.dashboard.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * 样例应用入口, Idea用户请激活Profile-sample
 * <br>
 * 关于运行：
 * 启动时分别声明如下启动参数即可启动多个样例
 * <pre>
 * --spring.profiles.active=app1
 * --spring.profiles.active=app2
 * --spring.profiles.active=app3
 * </pre>
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
