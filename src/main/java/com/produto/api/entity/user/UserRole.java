package com.produto.api.entity.user;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");
    private String role;
    UserRole(String role) {
        this.role = role;
    }
}
