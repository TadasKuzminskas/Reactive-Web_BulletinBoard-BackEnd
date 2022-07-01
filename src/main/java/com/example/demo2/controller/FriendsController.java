package com.example.demo2.controller;

import com.example.demo2.model.Friends;
import com.example.demo2.model.Post;
import com.example.demo2.service.FriendsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Slf4j
public class FriendsController {

    @Autowired
    FriendsService friendsService;

    @PostMapping("/friend")
    public Mono<Long> addPost(@RequestBody Friends friend) {
        return friendsService.addFriend(friend);
    }

    @GetMapping("/friends/{username}")
    public Flux<Friends> getFriendsByUsername(@PathVariable String username) {
        return friendsService.getFriendsByUsername(username);
    }

    @DeleteMapping("/friend/{id}")
    public Mono<Integer> deleteFriend(@PathVariable Long id) {
        return friendsService.deleteFriend(id);
    }
}
