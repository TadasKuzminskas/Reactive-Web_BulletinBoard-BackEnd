package com.example.demo2.controller;

import com.example.demo2.model.Post;
import com.example.demo2.service.PostService;
import com.example.demo2.util.UtilMethods;
import com.example.demo2.util.pojos.PostResponse;
import com.example.demo2.util.pojos.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    //Todo: cannot set the method in a reactive way, because the ServerRequest does not pick up "fileparts"

    @PostMapping("/post")
    public Mono<ServerResponse> addPost(@RequestPart("json") Post post, @RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            String imageName = utilMethods.imageHashString(filePart.filename());
            post.setImage(imageName);
            return postService.addPost(post)
                    .flatMap(n -> filePart.transferTo(basePath.resolve(imageName)));

        }).flatMap(r -> ServerResponse.ok().bodyValue(new ServerMessage("imageName")));
                //.flatMap(response -> ServerResponse.ok().body(response, PostResponse.class));
        //.flatMap(response -> ServerResponse.ok().body(new ServerMessage("hey"), ServerMessage.class));
        //.flatMap(response -> Mono.just(ResponseEntity.ok().body(new ServerMessage("hey"))));
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


//Tried refactoring to a functional router, but the ServerRequest was unable to extract the body.
//See bottom of the class for possible implementations.



//    @PostMapping("/upload")
//    public Mono<Void> uploadFile(@RequestPart("file")
//                                 Mono<FilePart> filePartMono){
//        return filePartMono
//                .doOnNext(fp -> System.out.println
//                        ("Received File : " + fp.filename()))
//                .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())));
//    }

//    File savedFile = new File(basePath+fp.filename());
//    File rename = new File(basePath+"Something");
//    boolean flag = savedFile.renameTo(rename);
//                    if (flag) {
//        System.out.println("File Successfully Rename");
//    }
//                    else {
//        System.out.println("Operation Failed");
//    }
//                    return Mono.empty();

//    public Mono<ServerResponse> uploadFile(ServerRequest serverRequest){
//
//        System.out.println(serverRequest.headers().header("Authorization").get(0));
////
////        serverRequest.multipartData().map(part -> part.get("file"))
////                .flatMapMany(Flux::fromIterable)
////                .cast(File.class)
////                .flatMap(it -> it.transferTo.)
//
////        serverRequest.body(BodyExtractors.toParts())
////                .cast(FilePart.class)
////                .doOnNext(filePart -> {
////                    System.out.println("Received File : " + filePart.filename());
////                })
////                .flatMap(filePart -> {
////                    return filePart.transferTo(basePath.resolve(filePart.filename()));
////                });
//
//        serverRequest.formData().map(part -> part.get("File"))
//                .flatMapMany(Flux::fromIterable)
//                .cast(FilePart.class)
//                .doOnNext(filePart -> {
//                    System.out.println("Received File : " + filePart.filename());
//                })
//                .flatMap(filePart -> {
//                    return filePart.transferTo(basePath.resolve(filePart.filename()));
//                });
//
//        serverRequest.body(BodyExtractors.toParts()).cast(FilePart.class).doOnNext(System.out::println);
//
//        serverRequest.formData().map(part -> part.get("File")).cast(FilePart.class).doOnNext(FilePart::filename);
//
//        serverRequest.multipartData().map(part -> part.get("file")).cast(FilePart.class).doOnNext(FilePart::filename);
//
//        serverRequest.body(BodyExtractors.toParts()).map(part -> part.content()).cast(FilePart.class).doOnNext(FilePart::filename);
//
//        serverRequest.bodyToMono(FilePart.class).doOnNext(body -> System.out.println(body.filename()));
//
//        return  ServerResponse.ok().bodyValue(new ServerMessage("image added"));
//    }


//    @PostMapping("/post")
//    public Mono<Long> addPost(@RequestBody Post post) {
//        log.info("POST_CONTROLLER: addPost()");
//            return postService.addPost(post);
//    }


//    public Mono<Void> uploadImage(Mono<FilePart> filePartMono) {
//        return filePartMono
//                .doOnNext(fp -> {
//                    System.out.println("Received File : " + fp.filename());
//                })
//                .flatMap(fp -> fp.transferTo(basePath.resolve("Something")));
//    }

//    @GetMapping("/image/{filename}")
//    public ResponseEntity<byte[]> getImage1(@PathVariable("filename") String filename) {
//        byte[] image = new byte[0];
//        try {
//            image = FileUtils.readFileToByteArray(new File(FILE_PATH_ROOT+filename));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
//    }