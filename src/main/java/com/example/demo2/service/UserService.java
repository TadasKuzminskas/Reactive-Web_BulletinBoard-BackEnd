package com.example.demo2.service;


import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Post;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepositoryCustom userRepository;

    @Autowired
    UserRepositoryCustom userRepositoryCustom;

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

    @Autowired
    PostService postService;

    @Autowired
    private JWTUtil jwtUtil;

    public Mono<User> findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public Mono<Long> addUser(User user) {
        return userRepository.addUser(user);
    }

    public Flux<User> findAllUsers() {
        return userRepositoryCustom.findAllUsers();
    }

    public Mono<Integer> updateUser(User user, Long id) {return userRepositoryCustom.updateUser(user, id);}

    public Mono<Integer> deleteUser(Long id) {return userRepositoryCustom.deleteUserById(id);}

    public Mono<User> findAllByIdWithPosts(Long id) {
        return Mono.zip(userRepositoryCustom.findUserById(id),
                postRepositoryCustom.findAllByUser(id).collectList(),
                (t1, t2) -> t1.withPosts(t2));
    }

    public Mono<User>findAllByIdWithPostsAndComments(Long id) {
        return Mono.zip(userRepositoryCustom.findUserById(id),
                postService.getAllByUserWithComments(id).collectList(),
                (t1, t2) -> t1.withPosts(t2));
    }

//    public Mono<User> mainPage(String token) {
//        String[] str = token.split(" ");
//        String username  = jwtUtil.getUsernameFromToken(str[1]);
//
//
//    }



}
