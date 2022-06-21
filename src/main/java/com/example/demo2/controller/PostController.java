package com.example.demo2.controller;

import com.example.demo2.model.Post;
import com.example.demo2.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@Slf4j
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/post")
    public Mono<Long> addPost(@RequestBody Post post) {
        log.info("POST_CONTROLLER: addPost()");
        return postService.addPost(post);
    }

    @CrossOrigin("http://localhost:3000")
    @GetMapping("/isPublic")
    public Flux<Post> getAllPublicPosts() {
        return postService.getAllPublicPosts();
    }

    @GetMapping("/posts")
    public Flux<Post> getAllPosts() {
        return  postService.getAllPosts();
    }

    @GetMapping("/postWithComments/{id}")
    public Mono<Post> getPostWithComments(@PathVariable Long id) {
        return postService.getByIdWithComments(id);
    }

    @GetMapping("/postsByUser/{id}")
    public Flux<Post> getAllByUser(@PathVariable Long id) {return postService.getAllByUser(id);}

    @DeleteMapping("/post/{id}")
    public Mono<Integer> deletePostById(@PathVariable Long id) {return  postService.deletePostById(id);}

    @PutMapping("/post")
    public Mono<Integer> updatePostById(@RequestBody Post post) {return postService.updatePost(post);}



}
