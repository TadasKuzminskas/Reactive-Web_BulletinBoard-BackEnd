package com.example.ReactiveWeb.repository.Custom;

import com.example.ReactiveWeb.model.User;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserRepositoryCustom {

    //@Resource
    private final DatabaseClient databaseClient;

    public static final BiFunction<Row, RowMetadata, User> MAPPING_FUNCTION = (row, rowMetaData) -> User.builder()
            .id(row.get("id", Long.class))
            .username(row.get("username", String.class))
            .name(row.get("name", String.class))
            .lastname(row.get("lastname", String.class))
            .password(row.get("password", String.class))
            .build();

    public Flux<User> findAllUsers() {
        return this.databaseClient
                .sql("SELECT * FROM user")
                .filter((statement, executeFunction) -> statement.fetchSize(10).execute())
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<User> findByUsername(String username) {
        return this.databaseClient
                .sql("SELECT * FROM user WHERE username=:username")
                .bind("username", username)
                .map(MAPPING_FUNCTION)
                .one();
    }


    public Mono<User> findUserById(Long id) {
        return this.databaseClient
                .sql("SELECT * FROM user WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Mono<Long> addUser(User user) {

        //user.setPassword(passwordHash(user.getPassword()));

        return databaseClient.sql("INSERT INTO  user (username, name, lastname, password) VALUES (:username, :name, :lastname, :password)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("username", user.getUsername())
                .bind("name", user.getName())
                .bind("lastname", user.getLastname())
                .bind("password", user.getPassword())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));

    }

    public String passwordHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes(StandardCharsets.UTF_8));

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
