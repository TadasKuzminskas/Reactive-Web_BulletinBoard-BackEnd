package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Post;
import com.example.demo2.model.User;
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

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class PostControllerTest {

    private User user;

    private Post post;

    private String token;

    @MockBean
    PostService postService;

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

        post = Post.builder().build();

        user = User.builder()
                .id(1L)
                .username("mockUsername")
                .name("mockName")
                .lastname("mockLastName")
                .password("mockPassword")
                .build();
        token = jwtUtil.generateToken(user);
    }

    // TODO : how to change so that the test would accept request part "json"?
    //  Tried changing it to application/json. No methods under webTestClient that
    //  would support such a test..

    @Test
    void addPost() {
        Mockito.when(postService.addPost(any())).thenReturn(Mono.just(1L));

        webTestClient
                .post().uri("/v1/post")
                .headers(http -> http.setBearerAuth(token))
                //.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(user)
                //.accept(MediaType.APPLICATION_JSON)
                //.bodyValue(user)
//                .attribute(MediaType.APPLICATION_JSON_VALUE, user)
                //.bodyValue(FilePart.class)
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getImage() {
       // Mockito.when(postService.addPost(any())).thenReturn(Mono.just(1L));

    }

    @Test
    void getAllPublicPosts() {

        Mockito.when(postService.getAllPublicPosts(1)).thenReturn(Flux.just(post));

        webTestClient
                .get().uri("/v1/isPublic/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();

    }

    @Test
    void getAllPrivatePostsByUsers() {
        Mockito.when(postService.getPrivateByUsers(token, 1)).thenReturn(Flux.just(post));

        webTestClient
                .get().uri("/v1/isPrivate/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void deletePostById() {
        Mockito.when(postService.deletePostById(anyLong())).thenReturn(Mono.just(1));

        webTestClient
                .delete().uri("/v1/post/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void updatePostById() {
        Mockito.when(postService.updatePost(any())).thenReturn(Mono.just(1));

        webTestClient
                .put().uri("/v1/post")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(post))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }
}