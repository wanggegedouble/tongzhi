package com.yupi.usercenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wy
 * @CreateTime: 2023-11-22  21:44
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        OpenAPI api = new OpenAPI();
        api.info(new Info()
                .description("TongZhiHome")
                .title("TongZhi")
                .version("0.0.1"));
        return api;
    }
}
