package com.produto.api.service;

import com.produto.api.dto.AddProductDTO;
import com.produto.api.dto.ResponseProductDTO;
import com.produto.api.dto.UpdateProductDTO;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.model.Product;
import com.produto.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository repository;
    @Autowired
    ProductMapper mapper;

    public void addProduct(AddProductDTO product) {
        repository.save(mapper.toEntityAdd(product));
    }

    public List<ResponseProductDTO> findAll() {
        if (repository.findAll().isEmpty()){throw new ProductNotFoundException();}
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public ResponseProductDTO findById(Long id){
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException();
        return mapper.toDTO(repository.findById(id).get());
    }

    public void deleteProduct(Long id){
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException();
        repository.deleteById(id);
    }

    public void updateProduct(Long id, UpdateProductDTO updatedProduct) {
        if (repository.findById(id).isEmpty()) throw new ProductNotFoundException();

        Product produto = repository.findById(id).get();

        mapper.toEntityUpdate(updatedProduct, produto);

        repository.save(produto);
    }
}