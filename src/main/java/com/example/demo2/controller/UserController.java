package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthResponse;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.repository.UserRepository;
import com.example.demo2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private JWTUtil jwtUtil;


    public Mono<ServerResponse> findAllUsers(ServerRequest serverRequest) {
        Flux<User> userFlux = userService.findAllUsers();
        return ServerResponse.ok()
                .body(userFlux, User.class);
    }


    @GetMapping("/user/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        log.info("USER_CONTROLLER: getUserById({})", id);
        return userService.findUserById(id);
    }

    @GetMapping("/userWithPosts/{id}")
    public Mono<User> getUserWithPosts(@PathVariable Long id) {
        return userService.findAllByIdWithPosts(id);
    }

    @GetMapping("/userLoadUp/{id}")
    public Mono<User> getUserLoadUp(@PathVariable Long id) {
        return userService.findAllByIdWithPostsAndComments(id);
    }

    @PostMapping("/user")
    public Mono<Long> addUser(@RequestBody User user) {
        log.info("USER_CONTROLLER: addUser()");
        return userService.addUser(user);
    }

    @PutMapping("/user/{id}")
    public Mono<Integer> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("USER_CONTROLLER: updating user: {}", user);
        return userService.updateUser(user, id);
    }

    @DeleteMapping("user/{id}")
    public Mono<Integer> deleteUserById(@PathVariable Long id) {
        log.info("USER_CONTROLLER: deleting user by id: {}", id);
        return userService.deleteUser(id);
    }

    public Mono<ServerResponse> getToken(ServerRequest serverRequest) {
        Mono<User> userMono = serverRequest.bodyToMono(User.class);

        return userMono.flatMap(user -> userRepositoryCustom.findByUsername(user.getUsername())
                .flatMap(userDetails -> {
                    if(user.getPassword().equals(userDetails.getPassword())) {
                        return ServerResponse.ok().bodyValue(new AuthResponse(jwtUtil.generateToken(user)));
                    } else {
                        return  ServerResponse.badRequest().build();
                    }
                }).switchIfEmpty(ServerResponse.badRequest().build()));
    }

}
