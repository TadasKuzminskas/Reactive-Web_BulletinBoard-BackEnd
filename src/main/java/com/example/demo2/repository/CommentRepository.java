package com.example.demo2.repository;


import com.example.demo2.model.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CommentRepository extends R2dbcRepository<Comment, String> {
}
