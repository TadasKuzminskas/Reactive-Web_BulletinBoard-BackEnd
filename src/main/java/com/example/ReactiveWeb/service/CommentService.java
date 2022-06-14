package com.example.ReactiveWeb.service;


import com.example.ReactiveWeb.model.Comment;
import com.example.ReactiveWeb.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CommentService {

    @Autowired
    CommentRepository commentRepository;
    public Flux<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Mono<Comment> postComment(Comment comment) {
        return commentRepository.save(comment);
    }

}
