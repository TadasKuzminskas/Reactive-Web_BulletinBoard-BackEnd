package com.example.demo2.service;

import com.example.demo2.model.Post;
import com.example.demo2.model.User;
import com.example.demo2.repository.Custom.UserRepositoryCustom;
import com.example.demo2.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepositoryCustom repository;

//    @Mock
//    private UserRepositoryCustom repository = mock(UserRepositoryCustom.class, RETURNS_DEEP_STUBS);

    @InjectMocks
    private UserService service;

//    @Autowired
//    ApplicationContext context;

    private User user;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        user = createUser(1L, "John", "Doe", "JoeDoe", "1234");
    }

    @Test
    public void testFindUserById() {
        //when(repository.findUserById(anyLong())).thenReturn(Optional.of(user));
//        Mono<User> returnedUser = service.findUserById(user.getId());
//        assertEquals(Mono.just(user), returnedUser);

        //System.out.println(repository.findUserById(1L).block().getPassword());



        //repository = mock(UserRepositoryCustom.class, Mockito.RETURNS_DEEP_STUBS);

        //UserRepository userRepoFromContext = context.getBean(UserRepository.class);

        Mockito.when(repository.findUserById(anyLong())).thenReturn(Mono.just(user));




        Mono<User> returnedUser = service.findUserById(user.getId());

        //System.out.println(Objects.requireNonNull(returnedUser.block()).getPassword());

        //assertEquals(returnedUser, Mono.just(user));

        StepVerifier.create(returnedUser).expectNext(user).expectComplete().verify();


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