package com.example.demo2.controller.routers;

import com.example.demo2.controller.PostController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PostRouter {

    @CrossOrigin
    @Bean
    public RouterFunction<ServerResponse> postRouterInstantiation(PostController postController) {
        return RouterFunctions.route()
                .GET("/v1/image/{filename}", postController::getImage)
                .build();
    }
}
