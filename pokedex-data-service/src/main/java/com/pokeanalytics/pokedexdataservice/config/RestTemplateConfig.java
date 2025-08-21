package com.pokeanalytics.pokedexdataservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 用于配置HTTP客户端，支持REST API调用
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建RestTemplate实例
     * 
     * @return 配置好的RestTemplate实例，用于HTTP请求
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}