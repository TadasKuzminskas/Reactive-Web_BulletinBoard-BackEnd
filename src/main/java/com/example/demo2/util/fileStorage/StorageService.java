package com.example.demo2.util.fileStorage;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(List<FilePart> files);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}

// file storage solution link: https://mflash.dev/post/2019/11/08/uploading-files-with-spring-boot-and-express/
