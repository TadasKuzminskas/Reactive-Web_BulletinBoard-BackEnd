package com.example.demo2.exceptions;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User " + id + " is not found.");
    }
}
