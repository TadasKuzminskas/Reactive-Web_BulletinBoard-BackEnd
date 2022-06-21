package com.example.demo2.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> userRouter(UserController userController) {
        return RouterFunctions.route()
                .GET("/v1/users", userController::findAllUsers)
               // .GET("/v1/user/{id}", userController::findUserById)
                .POST("/v1/token", userController::getToken)
                .build();
    }

}
