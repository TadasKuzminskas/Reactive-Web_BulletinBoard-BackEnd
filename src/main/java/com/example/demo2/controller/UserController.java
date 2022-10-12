package com.example.demo2.controller;

import com.example.demo2.model.User;
import com.example.demo2.repository.UserRepository;
import com.example.demo2.service.UserService;
import com.example.demo2.util.pojos.ErrorResponseMessage;
import com.example.demo2.util.pojos.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public Mono<ServerResponse> findAllUsers(ServerRequest serverRequest) {
        Flux<User> userFlux = userService.findAllUsers();
        return ServerResponse.ok()
                .body(userFlux, User.class);
    }


    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        try {
            Long userId = Long.parseLong(serverRequest.pathVariable("id"));
            return userService.findUserById(userId).flatMap(user -> ServerResponse.ok().body(Mono.just(user), User.class))
                    .switchIfEmpty(ServerResponse.badRequest().bodyValue(new ErrorResponseMessage("User not found", "404")));
        } catch (Exception exception) {
        }
        return ServerResponse.badRequest().bodyValue(new ErrorResponseMessage("User not found", "404"));
    }


    public Mono<ServerResponse> getActiveUser(ServerRequest serverRequest) {
        Mono<User> user = userService.getUserByJwt(serverRequest.headers().header("Authorization").get(0));
        return user.flatMap(user1 -> ServerResponse.ok().body(Mono.just(user1), User.class)
        ).switchIfEmpty(ServerResponse.ok().bodyValue(new ErrorResponseMessage("User not found", "404")));
    }


    public Mono<ServerResponse> getUsersThatStartWith(ServerRequest serverRequest) {
        Mono<ServerMessage> username = serverRequest.bodyToMono(ServerMessage.class);
        return username.flatMap(user -> {
            if (user.getServerMessage().equals("")) {
                return ServerResponse.badRequest().bodyValue(new ErrorResponseMessage("Resource not found", "404"));
            }
            return ServerResponse.ok().body(userService.findAllUsersThatStartWith(user.getServerMessage()), User.class)
                    .switchIfEmpty(ServerResponse.badRequest().bodyValue(new ErrorResponseMessage("Resource not found", "404")));
        });
    }

    public Mono<ServerResponse> addUser(ServerRequest serverRequest) {
        Mono<User> user = serverRequest.bodyToMono(User.class);
         return user.flatMap(userData -> userService.addUser(userData).flatMap(i -> {
            if (i == 0) {
                return ServerResponse.ok().bodyValue(new ErrorResponseMessage("Resource already exists", "303"));
            }
            return ServerResponse.ok().bodyValue(new ServerMessage("user created"));

        }));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class).flatMap(user -> userService.updateUser(user, user.getId()))
                .flatMap(userServiceResponse -> {
                    if (userServiceResponse >= 1) {
                        return ServerResponse.ok().bodyValue(new ServerMessage("Resource updated"));
                    }
                    return ServerResponse.ok().bodyValue(new ErrorResponseMessage("Resource not found", "404"));
                })
                .switchIfEmpty(ServerResponse.ok().bodyValue(new ErrorResponseMessage("Resource not found", "404")));
    }

        public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        Mono<User> user = userService.getUserByJwt(serverRequest.headers().header("Authorization").get(0));
        return user.flatMap(user1 -> {
            return userService.deleteUser(user1.getId()); }
        ).flatMap(userServiceResponse -> {
            if (userServiceResponse == 1) {
                return ServerResponse.ok().bodyValue(new ServerMessage("User deleted"));
            }
            return ServerResponse.ok().bodyValue(new ErrorResponseMessage("Resource not found", "404"));
                })
                .switchIfEmpty(ServerResponse.ok().bodyValue(new ErrorResponseMessage("Resource not found", "404")));
    }

}
