package com.produto.api.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductExistException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.entity.Product;
import com.produto.api.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    ProductRepository repository;
    @Autowired
    ProductMapper mapper;

    public ResponseProductDTO addProduct(AddProductDTO product) {
        if(product.name() == null || product.name().isBlank()) throw new IllegalArgumentException();
        if (product.quantity() == null || product.quantity() <= 0) throw new IllegalArgumentException();
        if(product.price() == null || product.price().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException();
        if(repository.existsByName(product.name())) throw new ProductExistException();
        Product newProduct = mapper.toEntityAdd(product);
        Product response = repository.save(newProduct);
        return mapper.toDTO(response);
    }

    public List<ResponseProductDTO> findAll() {
        List<Product> products = repository.findAll();
        if (products.isEmpty()){throw new ProductNotFoundException("No Products found");}
        return products.stream().map(mapper::toDTO).toList();
    }

    public ResponseProductDTO findById(UUID id){
        if(id == null) throw new IllegalArgumentException();
        Product result = repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        return mapper.toDTO(result);
    }

    public ResponseProductDTO deleteProduct(UUID id){
        if (id == null) throw new IllegalArgumentException();
        Product product = repository.findById(id).orElseThrow(() ->  new ProductNotFoundException("Product with id " + id + " not found"));
        repository.delete(product);
        return  mapper.toDTO(product);
    }

    public ResponseProductDTO updateProduct(UUID id, UpdateProductDTO updatedProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (updatedProduct.name() == null || updatedProduct.name().isEmpty()) throw new IllegalArgumentException();
        if (updatedProduct.quantity() == null || updatedProduct.quantity() < 0) throw new IllegalArgumentException();
        if (updatedProduct.price() == null || updatedProduct.price().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        if (repository.existsByName(updatedProduct.name())) throw new ProductExistException();

        Product produto = repository.findById(id).get();
        mapper.toEntityUpdate(updatedProduct, produto);
        repository.save(produto);
        return mapper.toDTO(produto);
    }

    public ResponseProductDTO withdrawProduct(UUID id, WithdrawOrPutProductDTO withdrawProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (withdrawProduct.quantity() == null || withdrawProduct.quantity() < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        Product produto = repository.findById(id).get();
        if (produto.getQuantity() < withdrawProduct.quantity()) throw new NotEnoghProductException("Not enough products in stock");
        produto.setQuantity(produto.getQuantity()-withdrawProduct.quantity());
        repository.save(produto);
        return mapper.toDTO(produto);
    }

    public ResponseProductDTO putProduct(UUID id, @Valid WithdrawOrPutProductDTO putProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (putProduct.quantity() == null || putProduct.quantity() < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        Product produto = repository.findById(id).get();
        produto.setQuantity(produto.getQuantity()+putProduct.quantity());
        repository.save(produto);
        return mapper.toDTO(produto);
    }
}