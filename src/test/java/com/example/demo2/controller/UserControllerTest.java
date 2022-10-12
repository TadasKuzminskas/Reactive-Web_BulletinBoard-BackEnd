package com.example.demo2.controller;

import com.example.demo2.config.security.jwt.AuthenticationManager;
import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.service.UserService;
import com.example.demo2.util.pojos.ErrorResponseMessage;
import com.example.demo2.util.pojos.ServerMessage;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    private final String MOCK_USERNAME = "mockUsername";

    private User user;

    private String token;


    @MockBean
    UserService service;

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
    void testFindAllUsers() {
        Mockito.when(service.findAllUsers()).thenReturn(Flux.just(user));
        webTestClient
                .get().uri("/v1/users")
                .headers(http -> http.setBearerAuth(token))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testGetUserById() {
        Mockito.when(service.findUserById(anyLong())).thenReturn(Mono.just(user));
        webTestClient
                .get().uri("/v1/user/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testGetUserByIdFail() {
        Mockito.when(service.findUserById(anyLong())).thenReturn(Mono.empty());
        webTestClient
                .get().uri("/v1/user/1")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ErrorResponseMessage.class);
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testGetActiveUser() {
        Mockito.when(service.getUserByJwt(anyString())).thenReturn(Mono.just(user));
        webTestClient
                .get().uri("/v1/activeUser")
                .header("Authorization", "foobar")
                .exchange()
                .expectStatus()
                .isOk();
                //.expectBody(UserDetails.class);
    }


    // TODO : for some reason the service.findAllUsersThatStartWith returns a bad request,
    //  that comes form the database. Service class covers it well, so I'm not sure what
    //  the reason is. Probably the test configuration itself.

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testGetUsersThatStartWith() {
        Mockito.when(service.findAllUsersThatStartWith(anyString())).thenReturn(Flux.just(user));
        webTestClient
                .get().uri("/v1/user/starts")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }



    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testAddUser() {
        Mockito.when(service.addUser(any())).thenReturn(Mono.just(1L));
        webTestClient
                .post().uri("/v1/userRegistration")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ServerMessage.class);
    }

    @Test
    @WithMockUser(username = MOCK_USERNAME)
    void testAddUserFail() {
        Mockito.when(service.addUser(any())).thenReturn(Mono.just(0L));
        webTestClient
                .post().uri("/v1/userRegistration")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ErrorResponseMessage.class);
    }

    @Test
    void testUpdateUser() {
        Mockito.when(service.updateUser(any(), anyLong())).thenReturn(Mono.just(1));
        webTestClient
                .put().uri("/v1/user")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ServerMessage.class);
    }

    @Test
    void testUpdateUserFail() {
        Mockito.when(service.updateUser(any(), anyLong())).thenReturn(Mono.just(0));
        webTestClient
                .put().uri("/v1/user")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ErrorResponseMessage.class);
    }

    @Test
    void testDeleteUserById() {
        Mockito.when(service.deleteUser(anyLong())).thenReturn(Mono.just(1));
        Mockito.when(service.getUserByJwt(anyString())).thenReturn(Mono.just(user));
        webTestClient
                .delete().uri("/v1/user")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ServerMessage.class);
    }

    @Test
    void testDeleteUserByIdFail() {
        Mockito.when(service.deleteUser(anyLong())).thenReturn(Mono.just(0));
        Mockito.when(service.getUserByJwt(anyString())).thenReturn(Mono.empty());
        webTestClient
                .delete().uri("/v1/user")
                .headers(http -> http.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ErrorResponseMessage.class);
    }

}