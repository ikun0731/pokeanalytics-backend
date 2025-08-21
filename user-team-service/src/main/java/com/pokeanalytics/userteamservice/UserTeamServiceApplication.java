package com.pokeanalytics.userteamservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.pokeanalytics.userteamservice.mapper")
@EnableFeignClients
public class UserTeamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserTeamServiceApplication.class, args);
	}

}
