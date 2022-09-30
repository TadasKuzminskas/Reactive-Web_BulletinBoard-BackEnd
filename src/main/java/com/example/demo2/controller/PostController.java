package com.example.demo2.controller;

import com.example.demo2.model.Post;
import com.example.demo2.service.PostService;
import com.example.demo2.util.fileStorage.FileSystemStorageService;
import com.example.demo2.util.fileStorage.StorageService;
import com.example.demo2.util.pojos.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Slf4j
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    FileSystemStorageService storageService;

    private final Path basePath = Paths.
            get("./src/main/resources/files/");



    private final Path imagePath = Paths.get("./src/main/resources/images/");

    @PostMapping("/post")
    public Mono<Long> addPost(@RequestBody Post post) {
        log.info("POST_CONTROLLER: addPost()");
        return postService.addPost(post);
    }

    public Mono<ServerResponse> uploadFileTest(ServerRequest serverRequest) {
        return serverRequest.multipartData().flatMap(parts -> {
            try {
                storageService
                        .store(parts.get("data").parallelStream().map(part -> (FilePart) part).collect(Collectors.toList()));
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                        BodyInserters.fromValue(Map.of("status", "Successfully uploaded"))
                );
            } catch (Exception e) {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(
                        BodyInserters.fromValue(Map.of("status", e.getLocalizedMessage())));
            }
        });
    }


    private String FILE_PATH_ROOT = "D:\\Development\\IdeaProjects\\BulletinBoard\\src\\main\\resources\\files\\";
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename) {
        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File(FILE_PATH_ROOT+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }



    @PostMapping("single/upload")
    public Mono<Void> uploadFile(@RequestPart("file")
                                 Mono<FilePart> filePartMono){
        return  filePartMono
                .doOnNext(fp -> System.out.println
                        ("Received File : " + fp.filename()))
                .flatMap(fp -> fp.
                        transferTo(basePath.resolve(fp.filename())))
                .then();
    }




    @GetMapping("/isPublic/{offset}")
    public Flux<Post> getAllPublicPosts(@PathVariable int offset) {
        return postService.getAllPublicPosts(offset);
    }

    @GetMapping("/posts")
    public Flux<Post> getAllPosts() {
        return  postService.getAllPosts();
    }

    @GetMapping("/isPrivate/{offset}")
    public Flux<Post> getAllPrivatePostsByUsers(@RequestHeader (name="Authorization") String token, @PathVariable int offset) {
        return postService.getPrivateByUsers(token, offset);
    }

    @DeleteMapping("/post/{id}")
    public Mono<Integer> deletePostById(@PathVariable Long id) {return  postService.deletePostById(id);}

    @PutMapping("/post")
    public Mono<Integer> updatePostById(@RequestBody Post post) {return postService.updatePost(post);}

}
