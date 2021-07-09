package com.alipay.sofa.dashboard.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@Configuration
@EnableSwagger2WebMvc
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return  new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .groupName("1.0版本")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.alipay.sofa.dashboard.controller"))
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SOFADashboard APIs")
                .description("SOFADashboard 接口对接文档")
                .termsOfServiceUrl("http://localhost:8099/")
                .version("1.0")
                .build();
    }



}
