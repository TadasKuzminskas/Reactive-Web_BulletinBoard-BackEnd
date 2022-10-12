package com.example.demo2.controller;

import com.example.demo2.model.Post;
import com.example.demo2.service.PostService;
import com.example.demo2.util.UtilMethods;
import com.example.demo2.util.pojos.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Slf4j
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    UtilMethods utilMethods;

    private final Path basePath = Paths.
            get("./src/main/resources/files/");

    private String FILE_PATH_ROOT = "D:\\Development\\IdeaProjects\\BulletinBoard\\src\\main\\resources\\files\\";


    //Todo 10/5/22: cannot set the method in a reactive way, because the ServerRequest does not pick up "fileparts"

    @PostMapping("/post")
    public Mono<ServerResponse> addPost(@RequestPart(MediaType.APPLICATION_JSON_VALUE) Post post, @RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            String imageName = utilMethods.imageHashString(filePart.filename());
            post.setImage(imageName);
            return postService.addPost(post)
                    .flatMap(n -> filePart.transferTo(basePath.resolve(imageName)));

        }).flatMap(r -> ServerResponse.ok().bodyValue(new ServerMessage("Image Added")));
    }

    public Mono<ServerResponse> getImage(ServerRequest serverRequest) {
        String filename = serverRequest.pathVariable("filename");
        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File(FILE_PATH_ROOT+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ServerResponse.ok().contentType(MediaType.IMAGE_JPEG).bodyValue(image);
    }

    @GetMapping("/isPublic/{offset}")
    public Flux<Post> getAllPublicPosts(@PathVariable int offset) {
        return postService.getAllPublicPosts(offset);
    }

//

    @GetMapping("/isPrivate/{offset}")
    public Flux<Post> getAllPrivatePostsByUsers(@RequestHeader (name="Authorization") String token, @PathVariable int offset) {
        return postService.getPrivateByUsers(token, offset);
    }

    @DeleteMapping("/post/{id}")
    public Mono<Integer> deletePostById(@PathVariable Long id) {return  postService.deletePostById(id);}

    @PutMapping("/post")
    public Mono<Integer> updatePostById(@RequestBody Post post) {return postService.updatePost(post);}

}