package com.example.demo2.service;


import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepositoryCustom userRepositoryCustom;

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

    @Autowired
    PostService postService;

    @Autowired
    private JWTUtil jwtUtil;

    public Mono<User> findUserById(Long id) {
        return userRepositoryCustom.findUserById(id);
    }

    public Mono<Long> addUser(User user) {
            //Checks if the user is present. If so returns a bad request. If not then populates the db and returns 200
        return userRepositoryCustom.findByUsername(user.getUsername())
                .map(user1 -> 0L)
                .switchIfEmpty(userRepositoryCustom.addUser(user));

    }

    public Flux<User> findAllUsers() {
        return userRepositoryCustom.findAllUsers();
    }

    public Mono<Integer> updateUser(User user, Long id) {return userRepositoryCustom.updateUser(user, id);}

    public Mono<Integer> deleteUser(Long id) {return userRepositoryCustom.deleteUserById(id);}

    public Flux<User> findAllUsersThatStartWith(String text) {
        return userRepositoryCustom.findAllUsersThatStartWith(text);
    }

    public Mono<User> getUserByJwt(String token) {
        String[] str = token.split(" ");
        String username  = jwtUtil.getUsernameFromToken(str[1]);

        return userRepositoryCustom.findByUsername(username);
    }

    //Do I use this??

//    public Mono<User> findAllByIdWithPosts(String username) {
//        return Mono.zip(userRepositoryCustom.findByUsername(username),
//                postRepositoryCustom.findAllByUser(username).collectList(),
//                (t1, t2) -> t1.withPosts(t2));
//    }

    //    public Mono<User> mainPage(String token) {
//        String[] str = token.split(" ");
//        String username  = jwtUtil.getUsernameFromToken(str[1]);
//        return Mono.zip(userRepositoryCustom.findByUsername(username),
//                postRepositoryCustom.findAllByUser(username).collectList(),
//                (t1, t2) -> t1.withPosts(t2));
//    }

}
