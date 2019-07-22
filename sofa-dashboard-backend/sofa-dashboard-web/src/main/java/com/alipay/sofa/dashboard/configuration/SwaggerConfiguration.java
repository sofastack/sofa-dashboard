/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.dashboard.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * SwaggerUI 相关配置
 */
@EnableSwagger2
@Configuration
@ConditionalOnExpression("${swagger.enable:false}")
public class SwaggerConfiguration {

    private static final String BASE_PACKAGE = "com.alipay.sofa.dashboard";

    /**
     * 应用相关元数据
     *
     * @return api information
     */
    @Bean
    public ApiInfo getApiInfo(ObjectProvider<BuildProperties> buildProperties) {
        BuildProperties props = buildProperties.getIfAvailable();

        if (props == null) { // 使用IDE运行的场景
            return new ApiInfoBuilder().title("SOFADashboard").version("compile")
                .description("SOFADashboard API Document.").build();
        }

        //
        // 如果是非IDE环境下运行，能从swagger文档描述上直接看到构建版本，方便做兼容性定位
        //
        String license = props.get("sofa.license");
        String licenseURL = props.get("sofa.license.url");
        String description = "SOFADashboard API Document";

        String buildTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(props
            .getTime()));
        String supportVersion = props.get("support.version");
        String version = String.format("%s build(%s), with dashboard support ver. %s",
            props.getVersion(), buildTime, supportVersion);
        return new ApiInfoBuilder().title("SOFADashboard").version(version)
            .description(description).license(license).licenseUrl(licenseURL).build();
    }

    @Bean
    public Docket api(ApiInfo info) {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(info).select()
            .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE)).paths(PathSelectors.any())
            .build();
    }

}
