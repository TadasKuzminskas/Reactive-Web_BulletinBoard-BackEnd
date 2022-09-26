package com.example.demo2.service;

import com.example.demo2.config.security.jwt.JWTUtil;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
//@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepositoryCustom repository;

    @MockBean
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserService service;


    private User user;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        user = createUser(1L, "John", "Doe", "JoeDoe", "1234");
    }

    @Test
    public void testFindUserById() {
        Mockito.when(repository.findUserById(anyLong())).thenReturn(Mono.just(user));
        Mono<User> returnedUser = service.findUserById(user.getId());
        StepVerifier.create(returnedUser).expectNext(user).expectComplete().verify();
    }

    @Test
    public void testAddUser() {
        Mockito.when(repository.findByUsername(anyString())).thenReturn(Mono.empty());
        Mockito.when(repository.addUser(any())).thenReturn(Mono.just(1L));
        Mono<Long> returnedLong = service.addUser(user);
        StepVerifier.create(returnedLong).expectNext(1L).expectComplete().verify();
    }

    @Test
    public void testFindAllUsers() {
        Mockito.when(repository.findAllUsers()).thenReturn(Flux.just(user));
        Flux<User> returnedUsers = service.findAllUsers();
        StepVerifier.create(returnedUsers).expectNext(user).expectComplete().verify();
    }

   @Test
   public void testUpdateUser() {
        Mockito.when(repository.updateUser(any(), anyLong())).thenReturn(Mono.just(1));
        Mono<Integer> updatedUser = service.updateUser(user, user.getId());
        StepVerifier.create(updatedUser).expectNext(1).expectComplete();
   }

   @Test
   public void testDeleteUser() {
        Mockito.when(repository.deleteUserById(anyLong())).thenReturn(Mono.just(1));
        Mono<Integer> deletedUser = service.deleteUser(user.getId());
        StepVerifier.create(deletedUser).expectNext(1).expectComplete();
   }

   @Test
   public void testFindAllUsersThatStartWith() {
        Mockito.when(repository.findAllUsersThatStartWith(anyString())).thenReturn(Flux.just(user));
        Flux<User> returnedUsers = service.findAllUsersThatStartWith(user.getUsername());
        StepVerifier.create(returnedUsers).expectNext(user).expectComplete();
   }

   @Test
   public void testGetUserByJwt() {
        Mockito.when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(user.getUsername());
        Mockito.when(repository.findByUsername(anyString())).thenReturn(Mono.just(user));
        Mono<User> returnedUser = service.getUserByJwt("any token string"); // <- must comprise out of three strings
        StepVerifier.create(returnedUser).expectNext(user).expectComplete();

   }

    private User createUser(Long id, String name, String lastName, String username, String password) {
        return User.builder()
                .id(id)
                .name(name)
                .lastname(lastName)
                .username(username)
                .password(password)
                .build();
    }
}