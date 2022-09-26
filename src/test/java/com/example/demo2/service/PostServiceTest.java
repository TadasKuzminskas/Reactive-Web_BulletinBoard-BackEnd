package com.example.demo2.service;

import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Friends;
import com.example.demo2.model.Post;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.FriendRepositoryCustom;
import com.example.demo2.repository.Custom.PostRepositoryCustom;
import io.r2dbc.spi.Parameter;
import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class PostServiceTest {

    @MockBean
    private PostRepositoryCustom repository;

    @MockBean
    private FriendRepositoryCustom friendRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @InjectMocks
    private PostService service;

    private Post post;

    private Post createPost(Long id, String name, String content, Boolean isPublic, String username, String image, Instant date) {
        return Post.builder()
                .Id(id)
                .name(name)
                .content(content)
                .isPublic(isPublic)
                .username(username)
                .image(image)
                .date(date)
                .build();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Instant instant = Instant.now();
        post = createPost(1L, "PostName", "PostContent", true, "PostUsername", "PostImage", instant);
    }

    @Test
    public void testAddPost() {
        Mockito.when(repository.save(any())).thenReturn(Mono.just(1L));
        Mono<Long> isPostReturned = service.addPost(post);
        StepVerifier.create(isPostReturned).expectNext(1L).expectComplete();
    }

    @Test
    public void testGetAllPublicPosts() {
        Mockito.when(repository.findWhereIsPublic(anyInt())).thenReturn(Flux.just(post));
        Flux<Post> returnedPosts = service.getAllPublicPosts(anyInt());
        StepVerifier.create(returnedPosts).expectNext(post).expectComplete();
    }

    @Test
    public void testGetAllPosts() {
        Mockito.when(repository.findAll()).thenReturn(Flux.just(post));
        Flux<Post> returnedPosts = service.getAllPosts();
        StepVerifier.create(returnedPosts).expectNext(post).expectComplete();
    }

    @Test
    public void testUpdatePost() {
        Mockito.when(repository.update(any())).thenReturn(Mono.just(1));
        Mono<Integer> isPostUpdated = service.updatePost(post);
        StepVerifier.create(isPostUpdated).expectNext(1).expectComplete();
    }

    @Test
    public void testDeletePostById() {
        Mockito.when(repository.deleteById(anyLong())).thenReturn(Mono.just(1));
        Mono<Integer> isPostDeleted = service.deletePostById(post.getId());
        StepVerifier.create(isPostDeleted).expectNext(1).expectComplete();
    }

    @Test
    public void testGetPrivateByUsers() {
        Mockito.when(friendRepository.getFriendsByUsername(anyString())).thenReturn(Flux.just(Friends.builder().build()));
        Mockito.when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(User.builder().username("anyUsername").build().getUsername());
        Mockito.when(repository.findWhereIsPrivateByUser(anyString(), anyInt())).thenReturn(Flux.just(post));
        Flux<Post> returnedPosts = service.getPrivateByUsers("any token string", 1);
        StepVerifier.create(returnedPosts).expectNext(post).expectComplete();
    }


}