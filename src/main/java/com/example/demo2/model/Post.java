package com.example.demo2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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

}
