package com.example.ReactiveWeb.repository.Custom;

import com.example.ReactiveWeb.model.Comment;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentRepositoryCustom {

    public static final BiFunction<Row, RowMetadata, Comment> MAPPING_FUNCTION = (row, rowMetaData) -> Comment.builder()
            .id(row.get("id", UUID.class))
            .content(row.get("content", String.class))
            .post(row.get("post", Long.class))
            .user(row.get("name", Long.class))
            .build();

    private final DatabaseClient databaseClient;

}
