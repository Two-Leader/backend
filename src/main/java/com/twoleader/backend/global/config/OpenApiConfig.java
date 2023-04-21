package com.twoleader.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger springdoc-ui 구성 파일 */
@OpenAPIDefinition(
        info = @Info(
                title = "TwoLeader API Document",
                description = "TwoLeader 프로젝트의 API 명세서",
                version = "v1"
        )
)
@Configuration
public class OpenApiConfig {
  @Bean
  public GroupedOpenApi StudyApi(){
    String[] ptahs = {"/api/v1/**"};

    return GroupedOpenApi.builder()
            .group("Study API v1")
            .pathsToMatch(ptahs)
            .build();
  }
}