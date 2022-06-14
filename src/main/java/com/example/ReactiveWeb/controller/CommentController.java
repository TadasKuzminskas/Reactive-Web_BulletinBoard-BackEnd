package com.example.ReactiveWeb.controller;

import com.example.ReactiveWeb.model.Comment;
import com.example.ReactiveWeb.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/comments")
    public Flux<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @PostMapping("/comment")
    public Mono<Comment> postComment(@RequestBody Comment comment) {
        return commentService.postComment(comment);
    }
}
