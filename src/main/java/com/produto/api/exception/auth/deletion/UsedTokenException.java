package com.produto.api.exception.auth.deletion;

public class UsedTokenException extends RuntimeException {
    public UsedTokenException(String message) {
        super(message);
    }

    public UsedTokenException() {
        super("Token utilizado");
    }
}