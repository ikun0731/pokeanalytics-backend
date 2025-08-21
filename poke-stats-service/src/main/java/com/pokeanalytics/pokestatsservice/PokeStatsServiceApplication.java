package com.pokeanalytics.pokestatsservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 宝可梦数据统计服务应用程序入口类
 * 
 * @SpringBootApplication Spring Boot应用程序注解
 * @EnableDiscoveryClient 启用服务发现客户端，用于注册到Nacos
 * @MapperScan 指定MyBatis Mapper接口扫描路径
 * @EnableScheduling 启用定时任务支持
 * @EnableFeignClients 启用Feign客户端，用于服务间调用
 * @EnableCaching 启用缓存支持
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.pokeanalytics.pokestatsservice.mapper")
@EnableScheduling
@EnableFeignClients
@EnableCaching
public class PokeStatsServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(PokeStatsServiceApplication.class, args);
    }

}