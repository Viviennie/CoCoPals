package com.tongji.codejourneycolab.codejourneycolabbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CodeJourneyCoLabBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeJourneyCoLabBackendApplication.class, args);
	}

}
