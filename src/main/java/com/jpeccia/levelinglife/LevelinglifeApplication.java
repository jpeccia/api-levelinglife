package com.jpeccia.levelinglife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "LEVELING LIFE",
		description = "API LevelingLife é uma plataforma que permite aos usuários criar e gerenciar seu perfil de evolução em um ambiente de gamificação, com foco no desenvolvimento de habilidades e no acompanhamento de progresso por meio de níveis e experiência (XP).",
					   version = "1"
	)
)
public class LevelinglifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LevelinglifeApplication.class, args);
	}

}
