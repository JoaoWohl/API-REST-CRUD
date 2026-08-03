package com.produto.api.exception.auth.deletion;

public class NonexistentTokenException extends RuntimeException {
    public NonexistentTokenException(String message) {
        super(message);
    }

    public NonexistentTokenException() {
        super("Token inexistente");
    }
}
