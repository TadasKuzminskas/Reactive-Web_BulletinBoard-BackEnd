package com.example.ReactiveWeb.controller;

import com.example.ReactiveWeb.model.User;
import com.example.ReactiveWeb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        log.info("USER_CONTROLLER: getAllUsers()");
        return userService.findAllUsers();
    }

    @GetMapping("/user/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        log.info("USER_CONTROLLER: getUserById({})", id);
        return userService.findUserById(id);
    }

    @PostMapping("/user")
    public Mono<Long> addUser(@RequestBody User user) {
        log.info("USER_CONTROLLER: addUser()");
        return userService.addUser(user);
    }


}
