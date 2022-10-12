package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.Friends;
import com.example.demo2.model.User;
import com.example.demo2.service.FriendsService;
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
class FriendsControllerTest {

    private final String MOCK_USERNAME = "mockUsername";

    private User user;

    private String token;

    private Friends friends;


    @MockBean
    FriendsService service;

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

        friends = Friends.builder().build();

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
    void testAddPost() {
        Mockito.when(service.addFriend(any())).thenReturn(Mono.just(1L));
        webTestClient
                .post().uri("/v1/friend")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(friends))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testGetFriendsByUsername() {
        Mockito.when(service.getFriendsByUsername(anyString())).thenReturn(Flux.just(friends));
        webTestClient
                .get().uri("/v1/friends/mockUsername")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void testDeleteFriend() {
        Mockito.when(service.deleteFriend(anyLong())).thenReturn(Mono.just(1));
        webTestClient
                .delete().uri("/v1/friend/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }
}