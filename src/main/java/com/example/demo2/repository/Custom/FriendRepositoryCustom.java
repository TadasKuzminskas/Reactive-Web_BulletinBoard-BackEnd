package com.example.demo2.repository.Custom;

import com.example.demo2.model.Friends;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Component
@Slf4j
public class FriendRepositoryCustom {

    private final DatabaseClient databaseClient;

    public static final BiFunction<Row, RowMetadata, Friends> MAPPING_FUNCTION = (row, rowMetaData) -> Friends.builder()
            .id(row.get("id", Long.class))
            .username(row.get("username", String.class))
            .friend(row.get("friend", String.class))
            .build();

    public Flux<Friends> getFriendsByUsername(String username) {
        return this.databaseClient
                .sql("SELECT * FROM friends WHERE username=:username")
                .bind("username", username)
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<Long> saveFriend(Friends p) {
        return this.databaseClient
                .sql("INSERT INTO friends (username, friend) VALUES (:username, :friend)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("username", p.getUsername())
                .bind("friend", p.getFriend())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));
    }

    public Mono<Integer> deleteFriendById(Long id) {
        return this.databaseClient.sql("DELETE FROM friends WHERE id=:id")
                .bind("id", id)
                .fetch()
                .rowsUpdated();
    }

}
