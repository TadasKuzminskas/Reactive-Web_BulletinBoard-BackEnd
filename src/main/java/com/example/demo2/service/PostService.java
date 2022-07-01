package com.example.demo2.service;


import com.example.demo2.model.Comment;
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

    public Flux<Post> getAllByUser(String username) { return postRepositoryCustom.findAllByUser(username);}

    public Mono<Integer> deletePostById(Long id) {return postRepositoryCustom.deleteById(id);}

    public Mono<Integer> updatePost(Post post) {return  postRepositoryCustom.update(post);}


    public Mono<Post> getByIdWithComments(Long id) {
        return Mono.zip(postRepositoryCustom.findById(id),
                commentRepositoryCustom.getCommentsByPost(id).collectList(),
                (t1, t2) -> t1.withComments(t2));
    }



//    public Flux<Post> getAllPostsByUserWithComments(String username) {
//
////        Flux<Long> postIdListByUser = postRepositoryCustom.findAllByUser(username)
////                .map(Post::getId);
//
//
////        Flux<Long> postIdListByUser = postRepositoryCustom.findAllByUser(username)
////                .map(t -> t.getUsername());
//
//        Flux<Long> postIdListByUser = postRepositoryCustom.findAllByUser(username)
//                .map(t -> {
//                    System.out.println(t.getId() + t.getUsername());
//                    return t.getId();
//                });
//
//        Flux<Post> postsWithComments = Flux.zip(postRepositoryCustom.findAllByUser(username),
//                postIdListByUser.flatMap(t -> commentRepositoryCustom.getCommentsByPost(t)).collectList(),
//                (t1, t2) -> t1.withComments(t2)
//        );
//
//
//        //working with this now....
//        Flux<Post> post = Flux.zip(postRepositoryCustom.findAllByUser(username),
//                postIdListByUser.flatMap(t -> commentRepositoryCustom.getCommentsByPost(t)).collectList(),
//                (t1, t2) -> {
//
//            List<Comment> comments = new ArrayList<>();
//
//                    for (Comment comment: t1.getComments())
//                    {
//                        comments.add(t2.get(Math.toIntExact(t1.getId())));
//                    }
//
//                    return t1.withComments(comments);
//        });
//
//
//
//       // Flux<Comment> commentByPost = commentRepositoryCustom.getCommentsByPost();
//
////        Flux<Comment> list = postIdListByUser.flatMap(t -> {
////            System.out.println("within postIdListByUser: " + t);
////            return commentRepositoryCustom.getCommentsByPost(t);}).log();
////
////        list.log();
//
//        Flux<Post> postFlux  = Flux.zip(postRepositoryCustom.findAllByUser(username),
//                postIdListByUser.flatMap(postId -> commentRepositoryCustom.getCommentsByPost(postId)).collectList()
//                ).flatMap(p ->{
//                return Flux.just(new Post(
//                        p.getT1().getId(),
//                        p.getT1().getName(),
//                        p.getT1().getContent(),
//                        p.getT1().getIsPublic(),
//                        p.getT1().getUsername(),
//                        p.getT2()
//
//        ));});
//
//        postFlux.subscribe(System.out::println);
//
//
//
//        postRepositoryCustom.findAllByUser(username).subscribe(System.out::println);
//
//        Flux<Post> postFlux2 = Flux.zip(postRepositoryCustom.findAllByUser(username),
//                postIdListByUser.flatMap(t -> {
//                    System.out.println("within postIdListByUser: " + t);
//
//                    return commentRepositoryCustom.getCommentsByPost(t);}).collectList().log(),
//                (t1, t2) -> t1.withComments(t2));
//
//
//        return post ;
//    }

}
