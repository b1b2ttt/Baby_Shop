package com.willa.babytun.commons.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private AntiRefreshInterceptor antiRefreshInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(antiRefreshInterceptor).addPathPatterns("/goods");
    }
}
