package com.produto.api.exception;

public class NotEnoghProductException extends RuntimeException {
    public NotEnoghProductException(String message) {
        super(message);
    }
}
