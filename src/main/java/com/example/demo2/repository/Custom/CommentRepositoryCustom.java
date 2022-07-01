package com.example.demo2.repository.Custom;

import com.example.demo2.model.Comment;

import com.example.demo2.model.Post;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
public class CommentRepositoryCustom {

    public static final BiFunction<Row, RowMetadata, Comment> MAPPING_FUNCTION = (row, rowMetaData) -> Comment.builder()
            .id(row.get("id", Long.class))
            .content(row.get("content", String.class))
            .post(row.get("post", Long.class))
            .username(row.get("username", String.class))
            .build();


    private final DatabaseClient databaseClient;

    public Flux<Comment> getCommentsByPost(Long id) {
        return this.databaseClient
                .sql("SELECT * FROM comment WHERE post=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .all();
    }

    public Mono<Long> saveComment(Comment comment) {
        return this.databaseClient
                .sql("INSERT INTO  comment (content, post, username) VALUES (:content, :post, :username)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("content", comment.getContent())
                .bind("post", comment.getPost())
                .bind("username", comment.getUsername())
                .fetch()
                .first()
                .map(r -> (Long) r.get("id"));
    }

    public Mono<Integer> updateComment(Comment comment) {
        return this.databaseClient.sql("UPDATE comment set content=:content WHERE id=:id")
                .bind("content", comment.getContent())
                .bind("id", comment.getId())
                .fetch()
                .rowsUpdated();
    }

    public Mono<Integer> deleteComment(Long id) {
        return  this.databaseClient.sql("DELETE FROM comment WHERE id=:id")
                .bind("id", id)
                .fetch()
                .rowsUpdated();
    }

}
