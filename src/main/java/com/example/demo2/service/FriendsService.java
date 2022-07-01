package com.example.demo2.service;

import com.example.demo2.model.Friends;
import com.example.demo2.repository.Custom.FriendRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FriendsService {

    @Autowired
    FriendRepositoryCustom friendRepositoryCustom;

    public Flux<Friends> getFriendsByUsername(String username) {
        return friendRepositoryCustom.getFriendsByUsername(username);
    }

    public Mono<Long> addFriend(Friends friend) {
        return friendRepositoryCustom.saveFriend(friend);
    }

    public Mono<Integer> deleteFriend(Long id) {
        return friendRepositoryCustom.deleteFriendById(id);
    }

}
