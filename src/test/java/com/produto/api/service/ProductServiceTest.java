package com.produto.api.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper mapper;

    @InjectMocks
    ProductService productService;

    @Test
    void addProduct_ShouldReturnSuccess_WhenAllOk() {
//        Arrange
        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                new BigDecimal("1.99"),
                10);
        Product product = new Product();
        when(mapper.toEntityAdd(newProduct)).thenReturn(product);

//        Act
        productService.addProduct(newProduct);

//        Assert
        verify(productRepository).save(any(Product.class));
    }

//    NOME
    @Test
    void addProduct_ShouldReturnFail_WhenNameIsEmpty() {

        AddProductDTO newProduct = new AddProductDTO(
                "",
                new BigDecimal("1.99"),
                10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

    @Test
    void addProduct_ShouldReturnFail_WhenNameIsNull() {

        AddProductDTO newProduct = new AddProductDTO(
                null,
                new BigDecimal("1.99"),
                10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

//    PREÇO

    @Test
    void addProduct_ShouldReturnFail_WhenPriceIsLessThanZero() {

        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                new BigDecimal("-1.99"),
                10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

    @Test
    void addProduct_ShouldReturnFail_WhenPriceIsNull() {

        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                null,
                10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

//    QUANTIDADE

    @Test
    void addProduct_ShouldReturnFail_WhenQuantityIsLessThanZero() {

        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                new BigDecimal("1.99"),
                -10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

    @Test
    void addProduct_ShouldReturnFail_WhenQuantityIsNull() {

        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                new BigDecimal("1.99"),
                null);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

}