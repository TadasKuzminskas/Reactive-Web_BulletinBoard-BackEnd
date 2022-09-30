package com.example.demo2.controller.routers;

import com.example.demo2.controller.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration

public class UserRouter {

    @Bean
    @CrossOrigin
    public RouterFunction<ServerResponse> userRouterInstantiation(UserController userController) {
        return RouterFunctions.route()
                .GET("/v1/users", userController::findAllUsers)
                .GET("/v1/activeUser", userController::getActiveUser)
                .POST("/v1/token", userController::getToken)
                .POST("/v1/refreshToken", userController::getRefreshToken)
                .POST("/v1/userRegistration", userController::addUser)
                .GET("/v1/user/starts", userController::getUsersThatStartWith)
                .DELETE("/v1/user", userController::deleteUser)
                .PUT("/v1/user", userController::updateUser)
                .build();
    }

}
