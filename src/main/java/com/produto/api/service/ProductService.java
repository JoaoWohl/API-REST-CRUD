package com.produto.api.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.entity.Product;
import com.produto.api.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository repository;
    @Autowired
    ProductMapper mapper;

    public void addProduct(AddProductDTO product) {
        if (product.quantity() == null || product.quantity() < 0) throw new IllegalArgumentException();
        if (product.name() == null || product.name().isEmpty() || product.name().isBlank()) throw new IllegalArgumentException();
        if(product.price() == null || product.price().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
        repository.save(mapper.toEntityAdd(product));
    }

    public List<ResponseProductDTO> findAll() {
        if (repository.findAll().isEmpty()){throw new ProductNotFoundException("No Products found");}
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public ResponseProductDTO findById(Long id){
        if(id == null) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        return mapper.toDTO(repository.findById(id).get());
    }

    public void deleteProduct(Long id){
        if (id == null) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        repository.deleteById(id);
    }

    public void updateProduct(Long id, UpdateProductDTO updatedProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (updatedProduct.name() == null || updatedProduct.name().isEmpty()) throw new IllegalArgumentException();
        if (updatedProduct.quantity() == null || updatedProduct.quantity() < 0) throw new IllegalArgumentException();
        if (updatedProduct.price() == null || updatedProduct.price().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");

        Product produto = repository.findById(id).get();
        mapper.toEntityUpdate(updatedProduct, produto);
        repository.save(produto);
    }

    public void withdrawProduct(Long id, WithdrawOrPutProductDTO withdrawProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (withdrawProduct.quantity() == null || withdrawProduct.quantity() < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        Product produto = repository.findById(id).get();
        if (produto.getQuantity() == 0 || produto.getQuantity() < withdrawProduct.quantity()) throw new NotEnoghProductException("Not enough products in stock");
        produto.setQuantity(produto.getQuantity()-withdrawProduct.quantity());
        repository.save(produto);
    }

    public void putProduct(Long id, @Valid WithdrawOrPutProductDTO putProduct) {
        if (id == null) throw new IllegalArgumentException();
        if (putProduct.quantity() == null || putProduct.quantity() < 0) throw new IllegalArgumentException();
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException("Product with id " + id + " not found");
        Product produto = repository.findById(id).get();
        produto.setQuantity(produto.getQuantity()+putProduct.quantity());
        repository.save(produto);
    }
}