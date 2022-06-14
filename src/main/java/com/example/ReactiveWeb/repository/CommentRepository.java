package com.example.ReactiveWeb.repository;


import com.example.ReactiveWeb.model.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CommentRepository extends R2dbcRepository<Comment, String> {
}
