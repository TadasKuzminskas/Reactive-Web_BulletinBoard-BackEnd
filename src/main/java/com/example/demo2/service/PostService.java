package com.example.demo2.service;


import com.example.demo2.model.Post;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PostService {

    @Autowired
    PostRepositoryCustom postRepositoryCustom;

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

}
