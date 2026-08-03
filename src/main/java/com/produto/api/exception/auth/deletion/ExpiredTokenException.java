package com.produto.api.exception.auth.deletion;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException() {
        super("Token expirado");
    }
}
