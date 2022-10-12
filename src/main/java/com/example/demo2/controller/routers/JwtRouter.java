package com.example.demo2.controller.routers;

import com.example.demo2.controller.JwtController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class JwtRouter {

    @Bean
    @CrossOrigin
    public RouterFunction<ServerResponse> jwtRouterInstantiation(JwtController jwtController) {
        return RouterFunctions.route()
                .POST("/v1/token", jwtController::getToken)
                .POST("/v1/refreshToken", jwtController::getRefreshToken)
                .build();
    }

}
