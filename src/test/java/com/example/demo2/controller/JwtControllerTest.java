package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthResponse;
import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class JwtControllerTest {

    private User user;

    private String token;

    @MockBean
    UserRepositoryCustom repository;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    UtilMethods utilMethods;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void init() {

        user = User.builder()
                .id(1L)
                .username("mockUsername")
                .name("mockName")
                .lastname("mockLastName")
                .password("mockPassword")
                .build();
        token = jwtUtil.generateToken(user);
    }

    // TODO : I don't get this at all. Why is the output correct no matter the variables?

    @Test
    void testGetToken() {
        Mockito.when(repository.findByUsername(anyString())).thenReturn(Mono.just(user));

        webTestClient
                .post().uri("/v1/token")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ServerRequest.class);

    }

    @Test
    void testGetTokenFail() {
       // Mockito.when(repository.findByUsername(anyString())).thenReturn(Mono.empty());
        //Mockito.when(utilMethods.hashingFunction(anyString())).thenReturn("text");


        webTestClient
                .post().uri("/v1/token")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthResponse.class);

    }


}