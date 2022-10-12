package com.example.demo2.controller;


import com.example.demo2.config.security.jwt.AuthResponse;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.util.UtilMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Slf4j
public class JwtController {

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    UtilMethods utilMethods;



    public Mono<ServerResponse> getToken(ServerRequest serverRequest) {
        Mono<User> userMono = serverRequest.bodyToMono(User.class);

        return userMono.flatMap(user -> userRepositoryCustom.findByUsername(user.getUsername())
                .flatMap(userDetails -> {
                    if(utilMethods.hashingFunction(user.getPassword()).equals(userDetails.getPassword())) {
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


}
