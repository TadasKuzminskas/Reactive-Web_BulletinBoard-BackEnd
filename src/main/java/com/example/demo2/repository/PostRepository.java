package com.example.demo2.repository;

import com.example.demo2.model.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PostRepository extends R2dbcRepository<Post, Long> {
}
