package com.example.demo2.controller;

import com.example.demo2.model.Comment;
import com.example.demo2.service.CommentService;
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

    @GetMapping("/commentsByPost/{id}")
    public Flux<Comment> getCommentsByPost(@PathVariable Long id) {
        return commentService.getAllCommentsByPost(id);
    }

    @PostMapping("/comment")
    public Mono<Long> saveComment(@RequestBody Comment comment) {return commentService.saveComment(comment);}

    @PutMapping("/comment")
    public Mono<Integer> updateComment(@RequestBody Comment comment) {return commentService.updateComment(comment);}

    @DeleteMapping("/comment/{id}")
    public Mono<Integer> deleteComment(@PathVariable Long id) {return  commentService.deleteComment(id);}
}
