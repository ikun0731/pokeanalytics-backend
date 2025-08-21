package com.pokeanalytics.pokedexdataservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 宝可梦数据服务应用程序入口类
 * 
 * @EnableAsync 启用异步方法支持
 * @EnableDiscoveryClient 启用服务发现客户端，用于注册到Nacos
 * @SpringBootApplication Spring Boot应用程序注解
 * @MapperScan 指定MyBatis Mapper接口扫描路径
 * @EnableCaching 启用缓存支持
 */
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.pokeanalytics.pokedexdataservice.mapper")
@EnableCaching
public class PokedexDataServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(PokedexDataServiceApplication.class, args);
    }

}
