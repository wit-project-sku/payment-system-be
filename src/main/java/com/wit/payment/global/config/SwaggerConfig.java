/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "💳 WIT Global 결제 API 명세서",
            description =
                ("""
                ## 주의사항
                - 파일 업로드 크기 제한: 5MB (1개 파일 크기)

                ## 문의
                - 기술 문의: 1030n@naver.com
                - 일반 문의: unijun0109@gmail.com, 1030n@naver.com
                """)),
    security = @SecurityRequirement(name = "Authorization"),
    servers = {
      @Server(url = "http://localhost:8080", description = "로컬 서버"),
      @Server(url = "https://api.witteria.com", description = "운영 서버")
    })
@SecurityScheme(
    name = "Authorization",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("Swagger API")
        .pathsToMatch("/api/**", "/swagger-ui/**", "/v3/api-docs/**")
        .build();
  }
}
