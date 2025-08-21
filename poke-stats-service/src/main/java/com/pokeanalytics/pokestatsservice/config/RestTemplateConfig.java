package com.pokeanalytics.pokestatsservice.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 用于配置HTTP客户端，支持REST API调用，包含超时设置和请求头配置
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建并配置RestTemplate实例
     * 
     * @param builder RestTemplate构建器
     * @return 配置好的RestTemplate实例，用于HTTP请求
     */
    @Bean
    @SuppressWarnings("deprecation") // 抑制setConnectTimeout方法的过时警告
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 创建HttpClient的请求配置，设置各种超时参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接建立超时时间，10秒
                .setConnectTimeout(Timeout.ofSeconds(10))
                // 从连接池获取连接的超时时间，10秒
                .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                // 数据传输超时时间，60秒
                .setResponseTimeout(Timeout.ofSeconds(60))
                .build();

        // 创建HttpClient实例，应用请求配置
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();

        // 创建基于配置好的HttpClient的请求工厂
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // 使用RestTemplateBuilder创建RestTemplate，并应用自定义配置
        return builder
                // 使用配置好的请求工厂
                .requestFactory(() -> requestFactory)
                // 设置浏览器User-Agent头，模拟正常浏览器请求
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                .build();
    }
}