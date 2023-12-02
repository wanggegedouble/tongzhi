package com.yupi.usercenter.config;

import com.yupi.usercenter.common.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author: wy
 * @CreateTime: 2023-11-13  00:21
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludePatterns = new String[]{"/swagger-resources/**", "/webjars/**", "/v3/**", "/swagger-ui.html/**",
                "/api", "/api-docs", "/api-docs/**", "/doc.html"};
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(excludePatterns);
    }
}
