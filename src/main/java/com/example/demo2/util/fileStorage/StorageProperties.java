package com.example.demo2.util.fileStorage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageProperties {

    public String getLocation() {
        return "./src/main/resources/images/";
    }
}


//FIX THIS CLASS!!!!!