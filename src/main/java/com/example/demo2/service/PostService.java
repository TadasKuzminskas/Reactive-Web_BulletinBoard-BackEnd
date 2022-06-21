package com.example.demo2.service;


import com.example.demo2.model.Post;
import com.example.demo2.repository.Custom.CommentRepositoryCustom;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

    @Autowired
    CommentRepositoryCustom commentRepositoryCustom;

    public Mono<Long> addPost(Post post) {
        return postRepositoryCustom.save(post);
    }

    public Flux<Post> getAllPublicPosts() {
        return postRepositoryCustom.findWhereIsPublic(true);
    }

    public Flux<Post> getAllPosts() {
        return postRepositoryCustom.findAll();
    }

    public Flux<Post> getAllByUser(Long id) { return postRepositoryCustom.findAllByUser(id);}

    public Mono<Integer> deletePostById(Long id) {return postRepositoryCustom.deleteById(id);}

    public Mono<Integer> updatePost(Post post) {return  postRepositoryCustom.update(post);}


    public Mono<Post> getByIdWithComments(Long id) {
        return Mono.zip(postRepositoryCustom.findById(id),
                commentRepositoryCustom.getCommentsByPost(id).collectList(),
                (t1, t2) -> t1.withComments(t2));
    }



    public Flux<Post> getAllByUserWithComments(Long userId) {
        Flux<Long> postIdListByUser = postRepositoryCustom.findAllByUser(userId)
                .map(Post::getUser);
        return Flux.zip(postRepositoryCustom.findAllByUser(userId),
                postIdListByUser.flatMap(t -> commentRepositoryCustom.getCommentsByPost(t)).collectList(),
                (t1, t2) -> t1.withComments(t2));
    }

}
