package com.jpeccia.levelinglife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
        .info(new Info().title("LevelingLife").description("API LevelingLife é uma plataforma que permite aos usuários criar e gerenciar seu perfil de evolução em um ambiente de gamificação, com foco no desenvolvimento de habilidades e no acompanhamento de progresso por meio de níveis e experiência (XP).").version("1"))
        .schemaRequirement("jwt_auth", creaSecurityScheme());
    }

    private SecurityScheme creaSecurityScheme(){
        return new SecurityScheme().name("jwt_auth").type(SecurityScheme.Type.HTTP).scheme("Bearer ").bearerFormat("JWT");
    }
}