package com.produto.api.exception.auth;

public class UserExistException extends RuntimeException {
    public UserExistException() {super("");}
    public UserExistException(String message) {
        super(message);
    }
}
