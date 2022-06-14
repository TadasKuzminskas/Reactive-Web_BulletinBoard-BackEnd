package com.example.ReactiveWeb.service;


import com.example.ReactiveWeb.model.Post;
import com.example.ReactiveWeb.repository.Custom.PostRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PostService {

    @Autowired
    PostRepositoryCustom postRepository;

    public Mono<Long> addPost(Post post) {
        return postRepository.save(post);
    }

    public Flux<Post> getAllPublicPosts() {
        return postRepository.findWhereIsPublic(true);
    }

    public Flux<Post> getAllPosts() {



        return postRepository.findAll();
    }

}
