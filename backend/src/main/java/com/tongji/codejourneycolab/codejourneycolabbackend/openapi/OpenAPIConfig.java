package com.tongji.codejourneycolab.codejourneycolabbackend.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Code Journey CoLab API")
                        .description("Swagger3")
                        .version("0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("项目API文档")
                        .url("/"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))) // 设置 Bearer token
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")); // 配置 Security Requirement
    }
}
