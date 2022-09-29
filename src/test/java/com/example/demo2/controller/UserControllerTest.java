package com.example.demo2.controller;

import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.service.UserService;
import com.example.demo2.util.pojos.ErrorResponseMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
//@WebFluxTest(UserController.class)
class UserControllerTest {

    private final String MOCK_USERNAME = "mockUsername";

    private User user;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    UserService service;

    @MockBean
    UserRepositoryCustom repository;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .username("mockUsername")
                .name("mockName")
                .lastname("mockLastName")
                .password("mockPassword")
                .build();
    }


    //Fails, because of UNAUTHORIZED status.
    @Test
    void findAllUsers() {
        Mockito.when(service.findAllUsers()).thenReturn(Flux.just(user));
        webClient
                .get().uri("/v1/users")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(User.class);
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void getUserById() {
        Mockito.when(service.findUserById(anyLong())).thenReturn(Mono.just(user));
        webClient
                .get().uri("/v1/user/1")
                .exchange()
                .expectStatus()
                .isOk();
                //.expectBody(User.class); //Look into this on later tests....

        // For some reason it does not pick up that User.class is the same one. Tried several
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void getUserByIdException() {
        Mockito.when(service.findUserById(anyLong())).thenReturn(Mono.empty());
        webClient
                .get().uri("/v1/user/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ErrorResponseMessage.class);
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void getActiveUser() {
        Mockito.when(service.getUserByJwt(anyString())).thenReturn(Mono.just(user));
        webClient
                .get().uri("/v1/activeUser")
                .header("Authorization", "foobar")
                .exchange()
                .expectStatus()
                .isOk();
                //.expectBody(UserDetails.class);
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void getUsersThatStartWith() {
        Mockito.when(service.findAllUsersThatStartWith(anyString())).thenReturn(Flux.just(user));
        webClient
                .get().uri("/v1/user/starts/foobar")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void addUser() {

    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void getToken() {
    }

    @Test
    void getRefreshToken() {
    }

    @Test
    void passwordHash() {
    }
}