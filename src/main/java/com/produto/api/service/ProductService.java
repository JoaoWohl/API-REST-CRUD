package com.produto.api.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.Product;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductExistException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.repository.ProductRepository;
import com.produto.api.repository.UserRepository;
import com.produto.api.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper mapper;
    @Autowired
    UserRepository userRepository;

    public ResponseProductDTO addProduct(UUID userId, AddProductDTO product) {
        if (userId == null) throw new IllegalArgumentException();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        if(productRepository.existsByUserIdAndName(user.getId(), product.name())) throw new ProductExistException();

        Product newProduct = mapper.toEntityAdd(product);
        newProduct.setUser(user);
        Product response = productRepository.save(newProduct);
        return mapper.toDTO(response);
    }

    public List<ResponseProductDTO> findAll(UUID userId) {
        if (userId == null) throw new IllegalArgumentException();
        List<Product> products = productRepository.findAllByUserId(userId);
        if (products.isEmpty()){throw new ProductNotFoundException("No Products found");}
        return products.stream().map(mapper::toDTO).toList();
    }

    public ResponseProductDTO findById(UUID userId, UUID id){
        if (userId == null) throw new IllegalArgumentException();
        if(id == null) throw new IllegalArgumentException();
        Product result = productRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        return mapper.toDTO(result);
    }

    public ResponseProductDTO deleteProduct(UUID userId, UUID id){
        if (userId == null) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();
        Product product = productRepository.findByUserIdAndId(userId, id).orElseThrow(() ->  new ProductNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO updateProduct(UUID userId, UUID id, UpdateProductDTO updatedProduct) {
        if (userId == null) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();

        Product product = productRepository.findByUserIdAndId(userId, id).orElseThrow(() ->  new ProductNotFoundException("Product with id " + id + " not found"));

        if (!product.getName().equals(updatedProduct.name())) {
            if (productRepository.existsByUserIdAndName(userId, updatedProduct.name()))  throw new ProductExistException();
        }

        mapper.toEntityUpdate(updatedProduct, product);
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO withdrawProduct(UUID userId, UUID id, WithdrawOrPutProductDTO withdrawProduct) {
        if (userId == null) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();

        Product product = productRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        if (product.getQuantity() < withdrawProduct.quantity()) throw new NotEnoghProductException("Not enough products in stock");
        product.setQuantity(product.getQuantity()-withdrawProduct.quantity());
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO putProduct(UUID userId, UUID id, WithdrawOrPutProductDTO putProduct) {
        if (userId == null) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();

        Product product = productRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        product.setQuantity(product.getQuantity()+putProduct.quantity());
        productRepository.save(product);
        return mapper.toDTO(product);
    }
}