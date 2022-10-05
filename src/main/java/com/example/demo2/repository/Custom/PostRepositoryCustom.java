package com.example.demo2.repository.Custom;

import com.example.demo2.model.Comment;
import com.example.demo2.model.Post;
import com.example.demo2.util.pojos.PostResponse;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            .username(row.get("username", String.class))
            .image(row.get("image", String.class))
            .date(row.get("date", Instant.class))
            .build();


    private final DatabaseClient databaseClient;

    public Flux<Post> findWhereIsPublic(int offset) {

        return this.databaseClient
                .sql("SELECT * FROM post WHERE ispublic=true ORDER BY date DESC LIMIT " + offset + ", 5")
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Flux<Post> findWhereIsPrivateByUser(String user, int offset) {

        return this.databaseClient
                .sql("SELECT * FROM post WHERE ispublic=false AND username=:username ORDER BY date DESC LIMIT " + offset + ", 2")
                .bind("username", user)
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Flux<Post> findWhereIsPrivate(String users) {
        System.out.println(users);
        return this.databaseClient
                .sql("SELECT * FROM post WHERE ispublic=0 AND username IN ( "+ users +" )")
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

    public Flux<Post> findAllByUser(String username) {
        return  this.databaseClient
                .sql("SELECT * FROM post WHERE username=:username")
                .bind("username", username)
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<Post> findById(Long id) {
        return this.databaseClient
                .sql("SELECT * FROM post WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Mono<Long> save(Post p) {
        return this.databaseClient
                .sql("INSERT INTO post (name, content, ispublic, username, image, date) VALUES (:name, :content, :ispublic, :username, :image, :date)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("name", p.getName())
                .bind("content", p.getContent())
                .bind("ispublic", p.getIsPublic())
                .bind("username", p.getUsername())
                .bind("image", p.getImage())
                .bind("date", p.getDate())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));
    }

    public Mono<Integer> update(Post p) {
        return this.databaseClient.sql("UPDATE post set name=:name, content=:content, image=:image WHERE id=:id")
                .bind("name", p.getName())
                .bind("content", p.getContent())
                .bind("image", p.getImage())
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