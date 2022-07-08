package com.example.demo2.service;


import com.example.demo2.model.Comment;
import com.example.demo2.repository.CommentRepository;
import com.example.demo2.repository.Custom.CommentRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Slf4j
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentRepositoryCustom commentRepositoryCustom;


    public Flux<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Mono<Long> saveComment(Comment comment) {
        comment.setDate(Instant.now());
        return commentRepositoryCustom.saveComment(comment);}

    public Flux<Comment> getAllCommentsByPost(Long id) {return commentRepositoryCustom.getCommentsByPost(id);}

    public Mono<Integer> updateComment(Comment comment) {return  commentRepositoryCustom.updateComment(comment);}

    public Mono<Integer> deleteComment(Long id) {return commentRepositoryCustom.deleteComment(id);}

}
