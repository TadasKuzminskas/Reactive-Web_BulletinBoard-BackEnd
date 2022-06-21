package com.example.demo2.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private Long Id;

    private String name;

    private String content;

    private Boolean isPublic;

    private Long user;

    @Transient
    @With
    private List<Comment> comments;

}
