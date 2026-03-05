package com.produto.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    private ResponseEntity<ErrorResponse> productNotFoundHandler(ProductNotFoundException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erro = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .title("Produto Não Encontrado")
                .detail(ex.getMessage())
                .build();
        return ResponseEntity.status(status).body(erro);
    }

}