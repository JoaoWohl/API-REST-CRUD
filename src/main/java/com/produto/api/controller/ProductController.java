package com.produto.api.controller;

import com.produto.api.dto.AddProductDTO;
import com.produto.api.dto.ResponseProductDTO;
import com.produto.api.dto.UpdateProductDTO;
import com.produto.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService service;

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody @Valid AddProductDTO product){
        service.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Produto adicionado com sucesso");
    }

    @GetMapping
    public ResponseEntity<List<ResponseProductDTO>> getAllProducts(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseProductDTO> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id){
        service.deleteProduct(id);
        return ResponseEntity.ok("Produto Excluido com sucesso");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductDTO product){
        service.updateProduct(id, product);
        return ResponseEntity.ok("Produto Atualizado com sucesso");
    }
}
