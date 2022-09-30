package com.example.demo2.util.fileStorage;

import com.example.demo2.exceptions.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

   private final Path rootDir;

    public FileSystemStorageService(StorageProperties storageProperties) {

        this.rootDir = Paths.get(storageProperties.getLocation());

    }


    @Override
    public void init() {
        try {
            Files.createDirectories(rootDir);
        } catch (Exception e) {
            throw new StorageException("Could not initiate Storage", e);
        }

    }

    @Override
    public void store(List<FilePart> files) {
        if (files.size() > 0) {

            files.forEach(file -> {

                String filename = StringUtils.cleanPath(Objects.requireNonNull(file.filename()));

                if (filename.contains("..")) {

                    throw new StorageException("Cannot store file with relative path outside current directory " + filename);

                }

                file.transferTo(Paths.get(this.rootDir.toString(), filename));

            });

        } else {

            throw new StorageException("Invalid request payload");

        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {

            return Files.walk(this.rootDir, 1)

                    .filter(path -> !path.equals(this.rootDir))

                    .map(this.rootDir::relativize);

        } catch (IOException e) {

            throw new StorageException("Failed to read stored files", e);

        }
    }

    @Override
    public Path load(String filename) {
        return this.rootDir.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {

            Path file = load(filename);

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {

                return resource;

            } else {

                throw new StorageException("Could not read file: " + filename);

            }

        } catch (MalformedURLException e) {

            throw new StorageException("Could not read file: " + filename, e);

        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootDir.toFile());
    }
}
