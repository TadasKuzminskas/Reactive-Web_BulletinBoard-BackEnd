package com.example.demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.config.CorsRegistry;

import java.util.Arrays;


@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class BulletinBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulletinBoardApplication.class, args);
	}


}
