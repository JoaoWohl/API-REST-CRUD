package com.produto.api.exception.auth;

public class EmailOrPasswordWrongException extends RuntimeException {
    public EmailOrPasswordWrongException(String message) {
        super(message);
    }
    public EmailOrPasswordWrongException(){
        super("Email ou senha inválido");
    }
}
