package com.example.ReactiveWeb.controller;

import com.example.ReactiveWeb.config.security.JWTUtil;

import com.example.ReactiveWeb.config.security.encoder.PBKDF2Encoder;
import com.example.ReactiveWeb.config.security.securityModels.AuthRequest;
import com.example.ReactiveWeb.config.security.securityModels.AuthResponse;
import com.example.ReactiveWeb.model.User;
import com.example.ReactiveWeb.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AuthenticationREST {

    private JWTUtil jwtUtil;
    private PBKDF2Encoder passwordEncoder;
    private UserRepository userService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest ar) {
        return userService.findByUsername(ar.getUsername())
                .filter(userDetails -> passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword()))
                .map(userDetails -> {
                    User user = new User();
                    user.setPassword(userDetails.getPassword());
                    user.setUsername(userDetails.getUsername());
                    return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(user)));
    })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

}
