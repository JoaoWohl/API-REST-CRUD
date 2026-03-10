package com.produto.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    private ResponseEntity<ErrorResponse> productNotFoundHandler(ProductNotFoundException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse erro = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Produto Não Encontrado")
                .detail(ex.getMessage())
                .build();
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Endpoint Not Found")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.Field> fields = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    ErrorResponse.Field field = new ErrorResponse.Field();
                    field.setName(error.getField());
                    field.setUserMessage(error.getDefaultMessage());
                    return field;
                })
                .toList();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse erro = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Invalid Arguments")
                .detail("One or more Invalid Arguments")
                .fields(fields)
                .build();
        return ResponseEntity.status(status).body(erro);
    }
}