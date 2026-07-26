package com.produto.api.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.entity.user.User;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductExistException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.entity.Product;
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
    @Autowired
    TokenUtils tokenUtils;

    public ResponseProductDTO addProduct(String authHeader, AddProductDTO product) {
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if (product.name() == null || product.name().isBlank()) throw new IllegalArgumentException();
        if (product.quantity() == null || product.quantity() <= 0) throw new IllegalArgumentException();
        if (product.price() == null || product.price().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException();

        User user = userRepository.findById(tokenUtils.getUUID(authHeader)).orElseThrow(() -> new RuntimeException("User Not Found"));

        if(productRepository.existsByUserIdAndName(user.getId(), product.name())) throw new ProductExistException();

        Product newProduct = mapper.toEntityAdd(product);
        newProduct.setUser(user);
        Product response = productRepository.save(newProduct);
        return mapper.toDTO(response);
    }

    public List<ResponseProductDTO> findAll(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        List<Product> products = productRepository.findAllByUserId(tokenUtils.getUUID(authHeader));
        if (products.isEmpty()){throw new ProductNotFoundException("No Products found");}
        return products.stream().map(mapper::toDTO).toList();
    }

    public ResponseProductDTO findById(String authHeader, UUID id){
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if(id == null) throw new IllegalArgumentException();
        Product result = productRepository.findByUserIdAndId(tokenUtils.getUUID(authHeader), id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        return mapper.toDTO(result);
    }

    public ResponseProductDTO deleteProduct(String authHeader, UUID id){
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();
        Product product = productRepository.findByUserIdAndId(tokenUtils.getUUID(authHeader), id).orElseThrow(() ->  new ProductNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO updateProduct(String authHeader, UUID id, UpdateProductDTO updatedProduct) {
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();
        if (updatedProduct.name() == null || updatedProduct.name().isBlank()) throw new IllegalArgumentException();
        if (updatedProduct.quantity() == null || updatedProduct.quantity() <= 0) throw new IllegalArgumentException();
        if (updatedProduct.price() == null || updatedProduct.price().compareTo(BigDecimal.ZERO) <= 0 ) throw new IllegalArgumentException();
        UUID userId = tokenUtils.getUUID(authHeader);
        Product product = productRepository.findByUserIdAndId(userId, id).orElseThrow(() ->  new ProductNotFoundException("Product with id " + id + " not found"));
        if (!product.getName().equals(updatedProduct.name())) {
            if (productRepository.existsByUserIdAndName(userId, updatedProduct.name()))  throw new ProductExistException();
        }
        mapper.toEntityUpdate(updatedProduct, product);
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO withdrawProduct(String authHeader, UUID id, WithdrawOrPutProductDTO withdrawProduct) {
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();
        if (withdrawProduct.quantity() == null || withdrawProduct.quantity() <= 0) throw new IllegalArgumentException();
        Product product = productRepository.findByUserIdAndId(tokenUtils.getUUID(authHeader), id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        if (product.getQuantity() < withdrawProduct.quantity()) throw new NotEnoghProductException("Not enough products in stock");
        product.setQuantity(product.getQuantity()-withdrawProduct.quantity());
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ResponseProductDTO putProduct(String authHeader, UUID id, WithdrawOrPutProductDTO putProduct) {
        if (authHeader == null || authHeader.isBlank()) throw new IllegalArgumentException();
        if (id == null) throw new IllegalArgumentException();
        if (putProduct.quantity() == null || putProduct.quantity() <= 0) throw new IllegalArgumentException();
        Product product = productRepository.findByUserIdAndId(tokenUtils.getUUID(authHeader), id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        product.setQuantity(product.getQuantity()+putProduct.quantity());
        productRepository.save(product);
        return mapper.toDTO(product);
    }
}