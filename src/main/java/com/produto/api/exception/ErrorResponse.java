package com.produto.api.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Getter
public class ErrorResponse {
    private Integer status; // codigo HTTP
    private OffsetDateTime timestamp; // momento do erro
    private String type; // URI ou titulo curto do erro
    private String title; // Descrição legível do erro
    private String detail; // Mensagem detalhada do erro para o DEV

    private List<Field> fields; // Para erros de validação

    public static class Field {
        private String name;
        private String userMessage;
    }
}