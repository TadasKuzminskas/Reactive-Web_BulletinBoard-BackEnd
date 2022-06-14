package com.example.ReactiveWeb.repository;

import com.example.ReactiveWeb.model.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PostRepository extends R2dbcRepository<Post, Long> {
}
