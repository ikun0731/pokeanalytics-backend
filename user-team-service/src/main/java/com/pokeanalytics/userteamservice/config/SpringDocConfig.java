package com.pokeanalytics.userteamservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // 定义安全方案的名称

        return new OpenAPI()
                // 1. 定义全局安全方案
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // 类型为HTTP
                                                .scheme("bearer")               // 方案为bearer
                                                .bearerFormat("JWT")            // 格式为JWT
                                )
                )
                // 2. 将安全方案应用到所有需要认证的API
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 3. 定义API的基本信息
                .info(new Info()
                        .title("PokeAnalytics - User Team Service API")
                        .version("v1.0")
                        .description("用户队伍微服务API文档，提供用户认证、管理及队伍创建等功能。")
                );
    }
}