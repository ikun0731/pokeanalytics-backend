package com.pokeanalytics.userteamservice.config;

import com.pokeanalytics.userteamservice.interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    public WebConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将我们的速率限制拦截器注册到 /ai/** 路径下
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/ai/**");
    }
}