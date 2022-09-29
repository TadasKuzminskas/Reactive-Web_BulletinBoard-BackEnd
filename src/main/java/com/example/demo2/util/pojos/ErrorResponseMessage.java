package com.example.demo2.util.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseMessage {

    private String errorMessage;

    private String status;

}
