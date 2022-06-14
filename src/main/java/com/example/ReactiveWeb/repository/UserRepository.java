package com.example.ReactiveWeb.repository;


import com.example.ReactiveWeb.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<UserDetails> findByUsername(String username);
}
