package com.produto.api.exception.auth;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {super("User Not Found");}
    public UserNotFoundException(String message) {
        super(message);
    }
}
