package com.example.demo2.service;


import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
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

    public Mono<User> findAllByIdWithPosts(String username) {
        return Mono.zip(userRepositoryCustom.findByUsername(username),
                postRepositoryCustom.findAllByUser(username).collectList(),
                (t1, t2) -> t1.withPosts(t2));
    }

    public Flux<User> findAllUsersThatStartWith(String text) {
        return userRepositoryCustom.findAllUsersThatStartWith(text);
    }

//    public Mono<User>findAllByIdWithPostsAndComments(String username) {
//        return Mono.zip(userRepositoryCustom.findByUsername(username),
//                postService.getAllPostsByUserWithComments(username).collectList(),
//                (t1, t2) -> t1.withPosts(t2));
//    }

    public Mono<User> mainPage(String token) {
        String[] str = token.split(" ");
        String username  = jwtUtil.getUsernameFromToken(str[1]);
        return Mono.zip(userRepositoryCustom.findByUsername(username),
                postRepositoryCustom.findAllByUser(username).collectList(),
                (t1, t2) -> t1.withPosts(t2));
    }



}
