package com.wy.TongZhi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @Author: wy
 * @CreateTime: 2023-11-22  21:44
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev","test"})
public class Knife4jConfig {
    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.wy.TongZhi.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("TongZhi")
                .description("TongZhi home")
                .contact(new Contact("wanno","https://github.com/wanggegedouble/tongzhi.git","xxx@qq.com"))
                .version("1.0")
                .build();
    }
}
