package com.wy.TongZhi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wy
 * @CreateTime: 2023-12-06  05:03
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class BeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
