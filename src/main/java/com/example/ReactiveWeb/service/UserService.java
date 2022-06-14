package com.example.ReactiveWeb.service;


import com.example.ReactiveWeb.config.security.encoder.PBKDF2Encoder;
import com.example.ReactiveWeb.model.User;
import com.example.ReactiveWeb.repository.Custom.PostRepositoryCustom;
import com.example.ReactiveWeb.repository.Custom.UserRepositoryCustom;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepositoryCustom userRepository;

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

    public Mono<User> findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public Mono<Long> addUser(User user) {
        PBKDF2Encoder encoder = new PBKDF2Encoder();
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.addUser(user);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

}
