package com.produto.api.controller;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.exception.ErrorResponse;
import com.produto.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Produtos", description = "Endpoints de gerenciamento de produtos")
@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService service;

    @Operation(summary = "Adiciona produto no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto Adicionado")
    })
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody @Valid AddProductDTO product){
        service.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Produto adicionado com sucesso");
    }

    @Operation(summary = "Lista todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
            @ApiResponse(responseCode = "404", description = "Produtos não encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ResponseProductDTO>> getAllProducts(){
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseProductDTO> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Deleta produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id){
        service.deleteProduct(id);
        return ResponseEntity.ok("Produto Excluido com sucesso");
    }

    @Operation(summary = "Atualiza produto de acordo com ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductDTO product){
        service.updateProduct(id, product);
        return ResponseEntity.ok("Produto Atualizado com sucesso");
    }

    @Operation(summary = "Retira quantidade informada do produto de acordo com ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto retirado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<?> buyProduct(@PathVariable Long id, @RequestBody @Valid WithdrawOrPutProductDTO product){
        service.withdrawProduct(id,product);
        return ResponseEntity.ok("Produto Retirado do Estoque com Sucesso");
    }

    @Operation(summary = "Adiciona quantidade informada do produto de acordo com ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}/put")
    public ResponseEntity<?> putProduct(@PathVariable Long id, @RequestBody @Valid WithdrawOrPutProductDTO product){
        service.putProduct(id,product);
        return ResponseEntity.ok("Produto Colocado no Estoque com Sucesso");
    }
}
