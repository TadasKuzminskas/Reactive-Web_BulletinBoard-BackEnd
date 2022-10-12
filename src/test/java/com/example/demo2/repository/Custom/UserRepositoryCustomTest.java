package com.example.demo2.repository.Custom;

import com.example.demo2.model.User;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

// TODO : Leaving this as an Example on how to test the CustomRepository classes.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class UserRepositoryCustomTest {

    private User user;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.nameTest}")
    private String dbName;

    MySqlConnectionFactory factory = MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
            .host("127.0.0.1")
            .port(3306)
            .username(dbUsername)
            .password(dbPassword)
            .database(dbName)
            .build());

    DatabaseClient databaseClient =  DatabaseClient.builder().connectionFactory(factory).build();

    UserRepositoryCustom repository = new UserRepositoryCustom(databaseClient);


    @BeforeEach
    public void init() {

        user = User.builder()
                .id(1L)
                .username("mockUsername")
                .name("mockName")
                .lastname("mockLastName")
                .password("mockPassword")
                .build();

        R2dbcEntityTemplate template = new R2dbcEntityTemplate(factory);
        template.insert(User.class).using(user).then().as(StepVerifier::create).verifyComplete();
    }

    @AfterEach
    public void disassembleDatabase() {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(factory);
        template.delete(User.class).all().then().as(StepVerifier::create).verifyComplete();
    }

    @Test
    void findAllUsersThatStartWith() {

        Flux<User> findThatStartsWith = repository.findAllUsersThatStartWith("mo");

        findThatStartsWith.as(StepVerifier::create)
                .assertNext(actual -> {
                    assertThat(actual.getUsername()).isEqualTo(user.getUsername());
                    assertThat(actual.getPassword()).isEqualTo(user.getPassword());
                })
                .verifyComplete();

    }


}
