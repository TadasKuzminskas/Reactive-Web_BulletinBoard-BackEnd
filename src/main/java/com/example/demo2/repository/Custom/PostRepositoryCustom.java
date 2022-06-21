package com.example.demo2.repository.Custom;

import com.example.demo2.model.Post;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import javafx.geometry.Pos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Component
@Slf4j
public class PostRepositoryCustom {

    public static final BiFunction<Row, RowMetadata, Post> MAPPING_FUNCTION = (row, rowMetaData) -> Post.builder()
            .Id(row.get("id", Long.class))
            .name(row.get("name", String.class))
            .content(row.get("content", String.class))
            .isPublic(row.get("ispublic", Boolean.class))
            .user(row.get("user", Long.class))
            .build();

    private final DatabaseClient databaseClient;

    public Flux<Post> findWhereIsPublic(Boolean ispublic) {

        return this.databaseClient
                .sql("SELECT * FROM post WHERE ispublic=true")
                //.filter((statement, executeFunction) -> statement.fetchSize(10).execute())
                .map(MAPPING_FUNCTION)
                .all()
                .log();
    }

    public Flux<Post> findByTitleContains(String name) {
        return this.databaseClient
                .sql("SELECT * FROM post WHERE name LIKE :title")
                .bind("name", "%" + name + "%")
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Flux<Post> findAll() {
        return this.databaseClient
                .sql("SELECT * FROM post")
                .filter((statement, executeFunction) -> statement.fetchSize(10).execute())
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Flux<Post> findAllByUser(Long id) {
        return  this.databaseClient
                .sql("SELECT * FROM post WHERE user=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<Post> findById(UUID id) {
        return this.databaseClient
                .sql("SELECT * FROM post WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Mono<Long> save(Post p) {
        return this.databaseClient
                .sql("INSERT INTO post (name, content, ispublic, user) VALUES (:name, :content, :ispublic, :user)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("name", p.getName())
                .bind("content", p.getContent())
                .bind("ispublic", p.getIsPublic())
                .bind("user", p.getUser())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));
    }

    public Mono<Integer> update(Post p) {
        return this.databaseClient.sql("UPDATE post set name=:name, content=:content WHERE id=:id")
                .bind("name", p.getName())
                .bind("content", p.getContent())
                .bind("id", p.getId())
                .fetch()
                .rowsUpdated();
    }

    public Mono<Integer> deleteById(Long id) {
        return this.databaseClient.sql("DELETE FROM post WHERE id=:id")
                .bind("id", id)
                .fetch()
                .rowsUpdated();
    }
}