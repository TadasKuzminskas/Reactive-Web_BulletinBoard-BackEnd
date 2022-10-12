package com.example.demo2.service;


import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Post;
import com.example.demo2.repository.Custom.FriendRepositoryCustom;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Slf4j
public class PostService {

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

    @Autowired
    FriendRepositoryCustom friendRepositoryCustom;

    @Autowired
    private JWTUtil jwtUtil;

    public Mono<Long> addPost(Post post) {
        post.setDate(Instant.now());
        return postRepositoryCustom.save(post);
    }

    public Flux<Post> getAllPublicPosts(int offset) {
        return postRepositoryCustom.findWhereIsPublic(offset);
    }

    public Flux<Post> getAllPosts() {
        return postRepositoryCustom.findAll();
    }

    public Mono<Integer> updatePost(Post post) {return  postRepositoryCustom.update(post);}

    public Mono<Integer> deletePostById(Long id) {return postRepositoryCustom.deleteById(id);}

    public Flux<Post> getPrivateByUsers(String token, int offset) {
        String[] str = token.split(" ");
        String username  = jwtUtil.getUsernameFromToken(str[1]);
        Flux<Post> followed = friendRepositoryCustom.getFriendsByUsername(username)
                .flatMap(friends -> postRepositoryCustom.findWhereIsPrivateByUser(friends.getFriend(), offset));
        return followed;
    }

}
