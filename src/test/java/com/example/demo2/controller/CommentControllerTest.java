package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Comment;
import com.example.demo2.model.Post;
import com.example.demo2.model.User;
import com.example.demo2.service.CommentService;
import com.example.demo2.service.PostService;
import com.example.demo2.util.UtilMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CommentControllerTest {

    private User user;
    private Comment comment;

    private String token;

    @MockBean
    CommentService service;

    @Autowired
    UtilMethods utilMethods;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    @BeforeEach
    public void init() {

        Mono<Authentication> authentication = Mono.just(
                new UsernamePasswordAuthenticationToken(
                        token,
                        token,
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER"))));

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);

        comment = Comment.builder().build();
        user = User.builder()
                .id(1L)
                .username("mockUsername")
                .name("mockName")
                .lastname("mockLastName")
                .password("mockPassword")
                .build();
        token = jwtUtil.generateToken(user);
    }

    @Test
    void testGetAllComments() {
        Mockito.when(service.getAllComments()).thenReturn(Flux.just(comment));
        webTestClient
                .get().uri("/v1/comments")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testGetCommentsByPost() {
        Mockito.when(service.getAllCommentsByPost(anyLong())).thenReturn(Flux.just(comment));
        webTestClient
                .get().uri("/v1/commentsByPost/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testSaveComment() {
        Mockito.when(service.saveComment(any())).thenReturn(Mono.just(1L));
        webTestClient
                .post().uri("/v1/comment")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(comment))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testUpdateComment() {
        Mockito.when(service.updateComment(any())).thenReturn(Mono.just(1));
        webTestClient
                .put().uri("/v1/comment")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(comment))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testDeleteComment() {
        Mockito.when(service.deleteComment(any())).thenReturn(Mono.just(1));
        webTestClient
                .delete().uri("/v1/comment/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();

    }
}