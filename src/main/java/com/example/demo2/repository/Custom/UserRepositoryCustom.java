package com.example.demo2.repository.Custom;

import com.example.demo2.model.User;
import com.example.demo2.util.UtilMethods;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserRepositoryCustom {

    // TODO : Might be bad code, but in order to change the DBClient for tests, I had to make
    //  this variable public.
    private final DatabaseClient databaseClient;

//    public UserRepositoryCustom(DatabaseClient client) {
//        this.databaseClient = client;
//    }

    @Autowired
    private UtilMethods utilMethods;

    public static final BiFunction<Row, RowMetadata, User> MAPPING_FUNCTION = (row, rowMetaData) -> User.builder()
            .id(row.get("id", Long.class))
            .username(row.get("username", String.class))
            .name(row.get("name", String.class))
            .lastname(row.get("lastname", String.class))
            .password(row.get("password", String.class))
            .build();

    public Flux<User> findAllUsers() {
        return this.databaseClient
                .sql("SELECT user.id, user.username, user.name, user.lastname, user.password FROM user")
                .filter((statement, executeFunction) -> statement.fetchSize(10).execute())
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Flux<User> findAllUsersThatStartWith(String text) {
        return this.databaseClient
                .sql("SELECT user.id, user.username, user.name, user.lastname, user.password FROM user WHERE user.username LIKE :text")
                .bind("text", text+"%")
                .filter((statement, executeFunction) -> statement.fetchSize(10).execute())
                .map(MAPPING_FUNCTION)
                .all();
    }


    public Mono<User> findUserById(Long id) {
        return this.databaseClient
                .sql("SELECT * FROM user WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Mono<User> findByUsername(String username) {
         Mono<User> user = this.databaseClient
                .sql("SELECT * FROM user WHERE username=:username")
                .bind("username", username)
                .map(MAPPING_FUNCTION)
                .one();
         return user;
    }

    public Mono<Long> addUser(User user) {

        user.setPassword(utilMethods.hashingFunction(user.getPassword()));

        return databaseClient.sql("INSERT INTO user (username, name, lastname, password) VALUES (:username, :name, :lastname, :password)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("username", user.getUsername())
                .bind("name", user.getName())
                .bind("lastname", user.getLastname())
                .bind("password", user.getPassword())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));

    }

    public Mono<Integer> updateUser(User user, Long id) {
        return this.databaseClient.sql("UPDATE user set name=:name, lastname=:lastname, password=:password, username=:username WHERE id=:id")
                .bind("id", id)
                .bind("name", user.getName())
                .bind("lastname", user.getLastname())
                .bind("password", utilMethods.hashingFunction(user.getPassword()))
                .bind("username", user.getUsername())
                .fetch()
                .rowsUpdated();
    }

    public Mono<Integer> deleteUserById(Long id) {
        return this.databaseClient.sql("DELETE FROM user WHERE id=:id")
                .bind("id", id)
                .fetch()
                .rowsUpdated();
    }

}
