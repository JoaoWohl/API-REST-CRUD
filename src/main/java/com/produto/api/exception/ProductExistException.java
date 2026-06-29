package com.produto.api.exception;

public class ProductExistException extends RuntimeException {
  public ProductExistException(String message) {
    super(message);
  }
  public ProductExistException() {
    super("Produto Já Cadastrado");
  }
}
