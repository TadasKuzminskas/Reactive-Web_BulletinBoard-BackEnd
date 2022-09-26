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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/v1")
@CrossOrigin
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

    @GetMapping("/activeUser")
    public Mono<User> getActiveUser(@RequestHeader (name="Authorization") String token) {
        return userService.getUserByJwt(token);
    }

    @GetMapping("/user/starts/{text}")
    public Flux<User> getUsersThatStartWith(@PathVariable String text) {
        if(text == null) {
            return null;
        }
        return userService.findAllUsersThatStartWith(text);
    }

    //Do I use this?

//    @GetMapping("/userWithPosts/{id}")
//    public Mono<User> getUserWithPosts(@PathVariable String username) {
//        return userService.findAllByIdWithPosts(username);
//    }

//    @GetMapping("/main")
//    public Mono<User> main(@RequestHeader (name="Authorization") String token) {
//       return userService.mainPage(token);
//    }

    @PostMapping("/userRegistration")
    public Mono<Long> addUser(@RequestBody User user) {
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
                    if(passwordHash(user.getPassword()).equals(userDetails.getPassword())) {
                        return ServerResponse.ok().bodyValue(new AuthResponse(jwtUtil.generateToken(user), jwtUtil.generateRefreshToken(user)));
                    } else {
                        return  ServerResponse.badRequest().build();
                    }
                }).switchIfEmpty(ServerResponse.badRequest().build()));
    }

    public Mono<ServerResponse> getRefreshToken(ServerRequest serverRequest) {
        Mono<AuthResponse> authMono = serverRequest.bodyToMono(AuthResponse.class);
        return authMono.flatMap(tokens -> {
                if (jwtUtil.isRefreshTokenValidated(tokens.getRefresh())) {
                    String username  = jwtUtil.getUsernameFromRefreshToken(tokens.getRefresh());
                    return userRepositoryCustom.findByUsername(username).flatMap(user -> {
                        String newToken = jwtUtil.generateToken(user);
                        return ServerResponse.ok().bodyValue(new AuthResponse(newToken, tokens.getRefresh()));
                    }).switchIfEmpty(ServerResponse.badRequest().build());
                }
                    return ServerResponse.badRequest().build();
        }).switchIfEmpty(ServerResponse.badRequest().build());
    }

    public String passwordHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes(StandardCharsets.UTF_8));

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
