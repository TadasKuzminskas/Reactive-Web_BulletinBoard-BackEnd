package com.example.demo2.exceptions.handler;

import com.example.demo2.exceptions.UserNotFoundException;
import com.example.demo2.util.pojos.ErrorResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class RestControllerExceptions {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity userNotFound(UserNotFoundException ex) {
        log.debug("handling exception::" + ex);
        ErrorResponseMessage errorResponse = new ErrorResponseMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
        return ResponseEntity.ok(errorResponse);
    }

    @Bean
    public WebExceptionHandler exceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof UserNotFoundException) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }
            return Mono.error(ex);
        };
    }

}
