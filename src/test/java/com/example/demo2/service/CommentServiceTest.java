package com.example.demo2.service;

import com.example.demo2.model.Comment;
import com.example.demo2.repository.CommentRepository;
import com.example.demo2.repository.Custom.CommentRepositoryCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @MockBean
    private CommentRepositoryCustom repository;

    @MockBean
    private CommentRepository r2dbcRepository;

    @InjectMocks
    private CommentService service;

    private Comment comment;

    private Comment createComment(Long id, String content, String username, Long post, Instant instant) {
        return Comment.builder()
                .id(id)
                .content(content)
                .username(username)
                .post(post)
                .date(instant)
                .build();
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Instant instant = Instant.now();
        comment = createComment(1L, "anyContent", "anyUsername", 1L, instant);
    }

    @Test
    void getAllComments() {
        Mockito.when(r2dbcRepository.findAll()).thenReturn(Flux.just(comment));
        Flux<Comment> returnedComments = service.getAllComments();
        StepVerifier.create(returnedComments).expectNext(comment).expectComplete();
    }

    @Test
    void saveComment() {
        Mockito.when(repository.saveComment(any())).thenReturn(Mono.just(1L));
        Mono<Long> isCommentAdded = service.saveComment(comment);
        StepVerifier.create(isCommentAdded).expectNext(1L).expectComplete();
    }

    @Test
    void getAllCommentsByPost() {
        Mockito.when(repository.getCommentsByPost(anyLong())).thenReturn(Flux.just(comment));
        Flux<Comment> returnedCommentsByPost = service.getAllCommentsByPost(anyLong());
        StepVerifier.create(returnedCommentsByPost).expectNext(comment).expectComplete();
    }

    @Test
    void updateComment() {
        Mockito.when(repository.updateComment(any())).thenReturn(Mono.just(1));
        Mono<Integer> isCommentUpdated = service.updateComment(comment);
        StepVerifier.create(isCommentUpdated).expectNext(1).expectComplete();
    }

    @Test
    void deleteComment() {
        Mockito.when(repository.deleteComment(any())).thenReturn(Mono.just(1));
        Mono<Integer> isCommentDeleted = service.deleteComment(anyLong());
        StepVerifier.create(isCommentDeleted).expectNext(1).expectComplete();
    }
}