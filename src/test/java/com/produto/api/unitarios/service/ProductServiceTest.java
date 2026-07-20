package com.produto.api.unitarios.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductExistException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.repository.ProductRepository;
import com.produto.api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Product validEntityProduct;
    private Product validUpdatedEntityProduct;
    private AddProductDTO validAddProductDTO;
    private ResponseProductDTO validResponseProductDTO;
    private ResponseProductDTO validResponseUpdatedProductDTO;
    private UpdateProductDTO validUpdateProductDTO;
    private List<Product> productList;
    private final UUID PRODUCT_ID = UUID.randomUUID();
    private WithdrawOrPutProductDTO withdrawOrPutProductDTO;
    private WithdrawOrPutProductDTO bigWithdrawOrPutProductDTO;

    @BeforeEach
    void setUp() {
        validAddProductDTO = new AddProductDTO("Product Name", new BigDecimal("100.99"), 100);
        validEntityProduct = new Product(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 100);
        validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 100);
        validResponseUpdatedProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name Test", new BigDecimal("200.99"), 200);
        validUpdateProductDTO = new UpdateProductDTO("Product Name Test", new BigDecimal("200.99"), 200);
        validUpdatedEntityProduct = new Product(PRODUCT_ID,"Product Name Test", new BigDecimal("200.99"), 200);
        withdrawOrPutProductDTO = new WithdrawOrPutProductDTO(10);
        bigWithdrawOrPutProductDTO = new WithdrawOrPutProductDTO(200);
        productList = List.of(validEntityProduct);
    }

    @Test
    @DisplayName("Deve retornar successo quando tudo está correto")
    void addProduct_ShouldReturnSuccess() {
        when(productRepository.existsByName(validAddProductDTO.name())).thenReturn(false);
        when(mapper.toEntityAdd(validAddProductDTO)).thenReturn(validEntityProduct);
        when(productRepository.save(validEntityProduct)).thenReturn(validEntityProduct);
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        productService.addProduct(validAddProductDTO);

        verify(productRepository, times(1)).existsByName(anyString());
        verify(mapper, times(1)).toEntityAdd(any(AddProductDTO.class));
        verify(productRepository, times(1)).save(any(Product.class));
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    public void addProduct_ShouldReturnProductExistException_WhenProductWithEqualsNameExist() {
        when(productRepository.existsByName(validAddProductDTO.name())).thenReturn(true);

        assertThrows(ProductExistException.class, () -> productService.addProduct(validAddProductDTO));

        verify(productRepository, times(1)).existsByName(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    static Stream<Arguments> addInvalidProducts() {
        return Stream.of(
                Arguments.of("Product Name", new BigDecimal("100.99"), -100),
                Arguments.of("Product Name", new BigDecimal("100.99"), 0),
                Arguments.of("Product Name", new BigDecimal("100.99"), null),

                Arguments.of("Product Name", new BigDecimal("-100.99"), 100),
                Arguments.of("Product Name", new BigDecimal("0"), 100),
                Arguments.of("Product Name", null, 100),

                Arguments.of(null, new BigDecimal("100.99"), 100),
                Arguments.of("", new BigDecimal("100.99"), 100),
                Arguments.of(" ", new BigDecimal("100.99"), 100)
                );
    }

    @ParameterizedTest
    @MethodSource("addInvalidProducts")
    void addProduct_ShouldReturnFail_WhenNameIsEmpty(String name, BigDecimal price, Integer quantity) {
        AddProductDTO invalidProductDTO = new AddProductDTO(name, price, quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(invalidProductDTO));

        verify(mapper, never()).toEntityAdd(any(AddProductDTO.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void findAll_ShouldReturnSuccess_WhenAllOk() {
        when(productRepository.findAll()).thenReturn(productList);
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        List<ResponseProductDTO> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(PRODUCT_ID);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnProductNotFoundException_WhenDontHaveProducts() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ProductNotFoundException.class, () -> productService.findAll());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        ResponseProductDTO result = productService.findById(PRODUCT_ID);

        assertThat(result).isEqualTo(validResponseProductDTO);
        verify(productRepository, times(1)).findById(any());
    }

    @Test
    void findById_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.findById(null));
    }

    @Test
    void findById_ShouldReturnFail_WhenIdNotFound() {
        assertThrows(ProductNotFoundException.class, () -> productService.findById(UUID.randomUUID()));
    }

    @Test
    void deleteProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        ResponseProductDTO result = productService.deleteProduct(PRODUCT_ID);

        assertThat(result).isEqualTo(validResponseProductDTO);

        verify(productRepository, times(1)).delete(any());
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdIsNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(PRODUCT_ID));

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(null));

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void updateProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(productRepository.existsByName(validUpdateProductDTO.name())).thenReturn(false);
        when(mapper.toEntityUpdate(any(), any())).thenReturn(validUpdatedEntityProduct);
        when(productRepository.save(any(Product.class))).thenReturn(validUpdatedEntityProduct);
        when(mapper.toDTO(any(Product.class))).thenReturn(validResponseUpdatedProductDTO);

        ResponseProductDTO result = productService.updateProduct(PRODUCT_ID, validUpdateProductDTO);

        assertThat(result).isEqualTo(validResponseUpdatedProductDTO);

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(null, validUpdateProductDTO));

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toDTO(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenProductExist() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(productRepository.existsByName(validUpdateProductDTO.name())).thenReturn(true);

        assertThrows(ProductExistException.class, () -> productService.updateProduct(PRODUCT_ID, validUpdateProductDTO));

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toDTO(any(Product.class));
        verify(mapper, never()).toEntityUpdate(any(), any());
    }

    static Stream<Arguments> updateInvalidProducts() {
        return Stream.of(
                Arguments.of("", new BigDecimal("200.99"), 200),
                Arguments.of(" ", new BigDecimal("200.99"), 200),
                Arguments.of(null, new BigDecimal("200.99"), 200),

                Arguments.of("Product Name Test", new BigDecimal("-200.99"), 200),
                Arguments.of("Product Name Test", new BigDecimal("0"), 200),
                Arguments.of("Product Name Test", null, 200),

                Arguments.of("Product Name Test", new BigDecimal("200.99"), -200),
                Arguments.of("Product Name Test", new BigDecimal("200.99"), 0),
                Arguments.of("Product Name Test", new BigDecimal("200.99"), null)
        );
    }

    @ParameterizedTest
    @MethodSource("updateInvalidProducts")
    void updateProduct_ShouldReturnIllegalArgumentException(String name, BigDecimal price, Integer quantity) {
        UpdateProductDTO invalidUpdateProductDTO = new UpdateProductDTO(name, price, quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(PRODUCT_ID, invalidUpdateProductDTO));

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).existsByName(any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toEntityUpdate(any(), any());
        verify(mapper, never()).toDTO(any());
    }

    @Test
    void withdrawProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        ResponseProductDTO withdrawProductResponseDTO = validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 90);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(any(Product.class))).thenReturn(withdrawProductResponseDTO);

        ResponseProductDTO result = productService.withdrawProduct(PRODUCT_ID, withdrawOrPutProductDTO);

        assertThat(result).isEqualTo(withdrawProductResponseDTO);

        verify(productRepository, times(1)).findById(any());
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(null, withdrawOrPutProductDTO));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenProductIsNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.withdrawProduct(PRODUCT_ID, withdrawOrPutProductDTO));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenQuantityIsGreaterThanStock() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        assertThrows(NotEnoghProductException.class, () -> productService.withdrawProduct(PRODUCT_ID, bigWithdrawOrPutProductDTO));
    }

    static Stream<Arguments> withdrawOrPutInvalidProducts() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-10)
        );
    }

    @ParameterizedTest
    @MethodSource("withdrawOrPutInvalidProducts")
    void withdrawProduct_ShouldReturnIllegalArgumentExceptions(int quantity) {
        WithdrawOrPutProductDTO invalidWithdrawProductDTO = new WithdrawOrPutProductDTO(quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(PRODUCT_ID, invalidWithdrawProductDTO));
    }

    @Test
    void putProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        ResponseProductDTO putProductResponseDTO = validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 110);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(any(Product.class))).thenReturn(putProductResponseDTO);

        ResponseProductDTO result = productService.putProduct(PRODUCT_ID, withdrawOrPutProductDTO);

        assertThat(result).isEqualTo(putProductResponseDTO);

        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(null, withdrawOrPutProductDTO));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenProductIsNotFound() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.putProduct(PRODUCT_ID, withdrawOrPutProductDTO));
    }

    @ParameterizedTest
    @MethodSource("withdrawOrPutInvalidProducts")
    void putProduct_ShouldReturnIllegalArgumentExceptions(int quantity) {
        WithdrawOrPutProductDTO invalidPutProductDTO = new WithdrawOrPutProductDTO(quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(PRODUCT_ID, invalidPutProductDTO));
    }

}