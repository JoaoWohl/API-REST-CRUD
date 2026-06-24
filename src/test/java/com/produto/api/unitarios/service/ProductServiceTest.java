package com.produto.api.unitarios.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.repository.ProductRepository;
import com.produto.api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        AddProductDTO newProduct = new AddProductDTO(
                "ProductTest",
                new BigDecimal("1.99"),
                10);
        Product product = new Product();
        when(mapper.toEntityAdd(newProduct)).thenReturn(product);

        productService.addProduct(newProduct);

        verify(productRepository).save(any(Product.class));
    }

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

    @Test
    void addProduct_ShouldReturnFail_WhenNameIsBlank() {
        AddProductDTO newProduct = new AddProductDTO(
                " ",
                new BigDecimal("1.99"),
                10);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(newProduct));
    }

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

    @Test
    void findAll_ShouldReturnSuccess_WhenAllOk() {
        Product product = new Product(
                1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10);
        List<Product> products = List.of(product);

        ResponseProductDTO dto = new ResponseProductDTO(1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10);
        List<ResponseProductDTO> dtos = List.of(dto);

        when(mapper.toDTO(product)).thenReturn(dto);
        when(productRepository.findAll()).thenReturn(products);

        List<ResponseProductDTO> result = productService.findAll();

        assertEquals(dtos, result);
    }

    @Test
    void findById_ShouldReturnSuccess_WhenIdOk() {
        Product product = new Product(
              1L,
              "ProductTest",
              new BigDecimal("1.99"),
              10);

        ResponseProductDTO dto = new ResponseProductDTO(
                1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10);

        when(mapper.toDTO(product)).thenReturn(dto);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseProductDTO result = productService.findById(1L);

        assertEquals(dto, result);
    }

    @Test
    void findById_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.findById(null));
    }

    @Test
    void findById_ShouldReturnFail_WhenIdNotFound() {
        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
    }

    @Test
    void deleteProduct_ShouldReturnSuccess_WhenAllOk(){
         Product product = new Product(
                 1L,
                 "ProductTest",
                 new BigDecimal("1.99"),
                 10
         );
         when(productRepository.findById(1L)).thenReturn(Optional.of(product));

         productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdIsNull(){
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(null));
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdNotFound(){
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void updateProduct_ShouldReturnSuccess_WhenAllOk(){
        Product product = new Product(
                1L,
                "ProductTestOldName",
                new BigDecimal("1.99"),
                10
        );
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("2.99"),
                20
        );
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProduct(1L, update);

        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenIdIsNull(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("2.99"),
                20
        );
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(null, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenIdIsNotFound(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("2.99"),
                20
        );
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenNameIsNull(){
        UpdateProductDTO update = new UpdateProductDTO(
                null,
                new BigDecimal("2.99"),
                20
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenNameIsEmpty(){
        UpdateProductDTO update = new UpdateProductDTO(
                "",
                new BigDecimal("2.99"),
                20
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenPriceIsNull(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                null,
                20
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenPriceIsLessThanZero(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("-1.99"),
                20
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenQuantityIsLessThanZero(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("1.99"),
                -20
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenQuantityIsNull(){
        UpdateProductDTO update = new UpdateProductDTO(
                "ProductTestNewName",
                new BigDecimal("1.99"),
                null
        );

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, update));
    }

    @Test
    void withdrawProduct_ShouldReturnSuccess_WhenAllOk(){
        Product product = new Product(
                1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10
        );
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.withdrawProduct(1L, withdrawProduct);

        verify(productRepository).save(argThat(p ->
                p.getId() == 1L
                && p.getName().equals("ProductTest")
                && p.getPrice().equals(new BigDecimal("1.99"))
                && p.getQuantity() == 9
        ));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenIdIsNull(){
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(1);
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(null, withdrawProduct));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenIdNotFound(){
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(1);
        assertThrows(ProductNotFoundException.class, () -> productService.withdrawProduct(1L, withdrawProduct));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenQuantityIsLessThanZero(){
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(-1);
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(1L, withdrawProduct));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenQuantityIsNull(){
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(null);
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(1L, withdrawProduct));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenQuantityIsGreaterThanStock(){
        Product product = new Product(
                1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10
        );
        WithdrawOrPutProductDTO withdrawProduct = new WithdrawOrPutProductDTO(11);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotEnoghProductException.class, () -> productService.withdrawProduct(1L, withdrawProduct));
    }

    @Test
    void putProduct_ShouldReturnSuccess_WhenAllOk(){
        Product product = new Product(
                1L,
                "ProductTest",
                new BigDecimal("1.99"),
                10
        );
        WithdrawOrPutProductDTO putProduct = new WithdrawOrPutProductDTO(1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.putProduct(1L,putProduct);

        verify(productRepository).save(argThat(p ->
                p.getId() == 1L
                && p.getName().equals("ProductTest")
                && p.getPrice().equals(new BigDecimal("1.99"))
                && p.getQuantity() == 11
        ));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenIdIsNull(){
        WithdrawOrPutProductDTO putProduct = new WithdrawOrPutProductDTO(1);
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(null, putProduct));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenIdNotFound(){
        WithdrawOrPutProductDTO putProduct = new WithdrawOrPutProductDTO(1);
        assertThrows(ProductNotFoundException.class, () -> productService.putProduct(1L, putProduct));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenQuantityIsLessThanZero(){
        WithdrawOrPutProductDTO putProduct = new WithdrawOrPutProductDTO(-1);

        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(1L, putProduct));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenQuantityIsNull(){
        WithdrawOrPutProductDTO putProduct = new WithdrawOrPutProductDTO(null);

        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(1L, putProduct));
    }

}