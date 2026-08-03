package com.produto.api.exception;

import com.produto.api.exception.auth.EmailOrPasswordWrongException;
import com.produto.api.exception.auth.UserExistException;
import com.produto.api.exception.auth.deletion.ExpiredTokenException;
import com.produto.api.exception.auth.deletion.NonexistentTokenException;
import com.produto.api.exception.auth.deletion.UsedTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            ExpiredTokenException.class,
            NonexistentTokenException.class,
            UsedTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleDeleteUserTokenException(RuntimeException ex, HttpServletRequest request) {

        log.warn("Falha no processamento de token para uri [{}]: {}", request.getRequestURI(), ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Invalid Token")
                .detail("O token fornecido é inválido, expirou ou já foi utilizado. Por favor, solicite uma nova confirmação.")
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(NotEnoghProductException.class)
    public ResponseEntity<ErrorResponse>  notEnoghProductHandler(NotEnoghProductException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Not Enough Product")
                .detail("Não há produtos suficientes em estoque")
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundHandler(ProductNotFoundException ex, HttpServletRequest request){
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
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
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

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ErrorResponse> userExistHandler(UserExistException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Usuário Cadastrado")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(EmailOrPasswordWrongException.class)
    public ResponseEntity<ErrorResponse>  emailOrPasswordWrongHandler(EmailOrPasswordWrongException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Email ou Senha Incorreto")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ProductExistException.class)
    public ResponseEntity<ErrorResponse>  productExistException(ProductExistException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .timestamp(OffsetDateTime.now())
                .type(request.getRequestURI())
                .title("Produto já cadastrado")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(error);
    }

}