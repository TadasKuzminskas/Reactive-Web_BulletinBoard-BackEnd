package com.example.demo2.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Router {

    @CrossOrigin
    @Bean
    public RouterFunction<ServerResponse> userRouter(UserController userController) {
        return RouterFunctions.route()
                .GET("/v1/users", userController::findAllUsers)
                .POST("/v1/token", userController::getToken)
                .POST("v1/refreshToken", userController::getRefreshToken)
                .build();
    }

}
