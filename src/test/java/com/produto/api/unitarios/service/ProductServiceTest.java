package com.produto.api.unitarios.service;

import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.dto.response.product.ResponseProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.exception.NotEnoghProductException;
import com.produto.api.exception.ProductExistException;
import com.produto.api.exception.ProductNotFoundException;
import com.produto.api.mapper.ProductMapper;
import com.produto.api.repository.ProductRepository;
import com.produto.api.repository.UserRepository;
import com.produto.api.service.ProductService;
import com.produto.api.utils.TokenUtils;
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
    @Mock
    UserRepository userRepository;
    @Mock
    TokenUtils tokenUtils;

    @InjectMocks
    ProductService productService;

    private Product validEntityProduct;
    private User validUser;
    private Product validUpdatedEntityProduct;
    private AddProductDTO validAddProductDTO;
    private ResponseProductDTO validResponseProductDTO;
    private ResponseProductDTO validResponseUpdatedProductDTO;
    private UpdateProductDTO validUpdateProductDTO;
    private List<Product> productList;
    private final static String authHeader = "Bearer sadasfgwtwwdsag";
    private final static UUID PRODUCT_ID = UUID.randomUUID();
    private final static UUID USER_ID = UUID.randomUUID();
    private WithdrawOrPutProductDTO withdrawOrPutProductDTO;
    private WithdrawOrPutProductDTO bigWithdrawOrPutProductDTO;

    @BeforeEach
    void setUp() {
        validUser = new User(USER_ID, "User Name", "testlogin@example.com", "TestPassword", UserRole.USER);
        validAddProductDTO = new AddProductDTO("Product Name", new BigDecimal("100.99"), 100);
        validEntityProduct = new Product(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 100, validUser);
        validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 100);
        validResponseUpdatedProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name Test", new BigDecimal("200.99"), 200);
        validUpdateProductDTO = new UpdateProductDTO("Product Name Test", new BigDecimal("200.99"), 200);
        validUpdatedEntityProduct = new Product(PRODUCT_ID,"Product Name Test", new BigDecimal("200.99"), 200, validUser);
        withdrawOrPutProductDTO = new WithdrawOrPutProductDTO(10);
        bigWithdrawOrPutProductDTO = new WithdrawOrPutProductDTO(200);
        productList = List.of(validEntityProduct);
    }

    static Stream<Arguments> invalidAuthHeaders() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of((Object) null),
                Arguments.of(" ")
        );
    }

    @Test
    @DisplayName("Deve retornar successo quando tudo está correto")
    void addProduct_ShouldReturnSuccess() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));
        when(productRepository.existsByUserIdAndName(USER_ID, validAddProductDTO.name())).thenReturn(false);
        when(mapper.toEntityAdd(validAddProductDTO)).thenReturn(validEntityProduct);
        when(productRepository.save(validEntityProduct)).thenReturn(validEntityProduct);
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        productService.addProduct(authHeader, validAddProductDTO);

        verify(tokenUtils, times(1)).getUUID(anyString());
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(productRepository, times(1)).existsByUserIdAndName(any(UUID.class), anyString());
        verify(mapper, times(1)).toEntityAdd(any(AddProductDTO.class));
        verify(productRepository, times(1)).save(any(Product.class));
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    public void addProduct_ShouldReturnProductExistException_WhenProductWithEqualsNameExist() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));
        when(productRepository.existsByUserIdAndName(validUser.getId(), validAddProductDTO.name())).thenReturn(true);

        assertThrows(ProductExistException.class, () -> productService.addProduct(authHeader, validAddProductDTO));

        verify(productRepository, times(1)).existsByUserIdAndName(any(UUID.class), anyString());
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
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(authHeader, invalidProductDTO));

        verify(mapper, never()).toEntityAdd(any(AddProductDTO.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void addProduct_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(invalidAuthHeader, validAddProductDTO));
    }

    @Test
    void findAll_ShouldReturnSuccess_WhenAllOk() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findAllByUserId(USER_ID)).thenReturn(productList);
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        List<ResponseProductDTO> result = productService.findAll(authHeader);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(PRODUCT_ID);

        verify(productRepository, times(1)).findAllByUserId(any(UUID.class));
    }

    @Test
    void findAll_ShouldReturnProductNotFoundException_WhenNotHaveProducts() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());

        assertThrows(ProductNotFoundException.class, () -> productService.findAll(authHeader));

        verify(productRepository, times(1)).findAllByUserId(any());
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void findAll_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.findAll(invalidAuthHeader));
    }

    @Test
    void findById_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        ResponseProductDTO result = productService.findById(authHeader, PRODUCT_ID);

        assertThat(result).isEqualTo(validResponseProductDTO);
        verify(productRepository, times(1)).findByUserIdAndId(any(UUID.class), any(UUID.class));
    }

    @Test
    void findById_ShouldReturnFail_WhenProductIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.findById(authHeader, null));
    }

    @Test
    void findById_ShouldReturnFail_WhenIdNotFound() {
        assertThrows(ProductNotFoundException.class, () -> productService.findById(authHeader, PRODUCT_ID));
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void findById_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.findById(invalidAuthHeader, PRODUCT_ID));
    }

    @Test
    void deleteProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(validEntityProduct)).thenReturn(validResponseProductDTO);

        ResponseProductDTO result = productService.deleteProduct(authHeader, PRODUCT_ID);

        assertThat(result).isEqualTo(validResponseProductDTO);

        verify(productRepository, times(1)).delete(any());
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdIsNotFound() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(authHeader, PRODUCT_ID));

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(authHeader, null));

        verify(productRepository, never()).deleteById(any());
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void deleteProduct_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(invalidAuthHeader, PRODUCT_ID));
    }

    @Test
    void updateProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(productRepository.existsByUserIdAndName(USER_ID, validUpdateProductDTO.name())).thenReturn(false);
        when(mapper.toEntityUpdate(any(), any())).thenReturn(validUpdatedEntityProduct);
        when(productRepository.save(any(Product.class))).thenReturn(validUpdatedEntityProduct);
        when(mapper.toDTO(any(Product.class))).thenReturn(validResponseUpdatedProductDTO);

        ResponseProductDTO result = productService.updateProduct(authHeader, PRODUCT_ID, validUpdateProductDTO);

        assertThat(result).isEqualTo(validResponseUpdatedProductDTO);

        verify(productRepository, times(1)).findByUserIdAndId(any(UUID.class), any(UUID.class));
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(authHeader, null, validUpdateProductDTO));

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toDTO(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnFail_WhenProductExist() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(productRepository.existsByUserIdAndName(USER_ID, validUpdateProductDTO.name())).thenReturn(true);

        assertThrows(ProductExistException.class, () -> productService.updateProduct(authHeader, PRODUCT_ID, validUpdateProductDTO));

        verify(productRepository, times(1)).findByUserIdAndId(any(), any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toDTO(any(Product.class));
        verify(mapper, never()).toEntityUpdate(any(), any());
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void updateProduct_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(invalidAuthHeader, PRODUCT_ID, validUpdateProductDTO));
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
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(authHeader, PRODUCT_ID, invalidUpdateProductDTO));

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
        verify(mapper, never()).toEntityUpdate(any(), any());
        verify(mapper, never()).toDTO(any());
    }

    @Test
    void withdrawProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        ResponseProductDTO withdrawProductResponseDTO = validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 90);
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(any(Product.class))).thenReturn(withdrawProductResponseDTO);

        ResponseProductDTO result = productService.withdrawProduct(authHeader, PRODUCT_ID, withdrawOrPutProductDTO);

        assertThat(result).isEqualTo(withdrawProductResponseDTO);

        verify(productRepository, times(1)).findByUserIdAndId(any(), any());
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(authHeader, null, withdrawOrPutProductDTO));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenProductIsNotFound() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.withdrawProduct(authHeader, PRODUCT_ID, withdrawOrPutProductDTO));
    }

    @Test
    void withdrawProduct_ShouldReturnFail_WhenQuantityIsGreaterThanStock() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        assertThrows(NotEnoghProductException.class, () -> productService.withdrawProduct(authHeader, PRODUCT_ID, bigWithdrawOrPutProductDTO));
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void withdrawProduct_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(invalidAuthHeader, PRODUCT_ID, withdrawOrPutProductDTO));
    }

    static Stream<Arguments> withdrawOrPutInvalidProducts() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of((Object) null),
                Arguments.of(-10)
        );
    }

    @ParameterizedTest
    @MethodSource("withdrawOrPutInvalidProducts")
    void withdrawProduct_ShouldReturnIllegalArgumentExceptions(Integer quantity) {
        WithdrawOrPutProductDTO invalidWithdrawProductDTO = new WithdrawOrPutProductDTO(quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.withdrawProduct(authHeader, PRODUCT_ID, invalidWithdrawProductDTO));
    }

    @Test
    void putProduct_ShouldReturnSuccess_WhenEverythingIsOk() {
        ResponseProductDTO putProductResponseDTO = validResponseProductDTO = new ResponseProductDTO(PRODUCT_ID, "Product Name", new BigDecimal("100.99"), 110);
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.of(validEntityProduct));
        when(mapper.toDTO(any(Product.class))).thenReturn(putProductResponseDTO);

        ResponseProductDTO result = productService.putProduct(authHeader, PRODUCT_ID, withdrawOrPutProductDTO);

        assertThat(result).isEqualTo(putProductResponseDTO);

        verify(productRepository, times(1)).findByUserIdAndId(any(), any());
        verify(productRepository, times(1)).save(any());
        verify(mapper, times(1)).toDTO(any(Product.class));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(authHeader, null, withdrawOrPutProductDTO));
    }

    @Test
    void putProduct_ShouldReturnFail_WhenProductIsNotFound() {
        when(tokenUtils.getUUID(authHeader)).thenReturn(USER_ID);
        when(productRepository.findByUserIdAndId(USER_ID, PRODUCT_ID)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.putProduct(authHeader, PRODUCT_ID, withdrawOrPutProductDTO));
    }

    @ParameterizedTest
    @MethodSource("invalidAuthHeaders")
    void putProduct_ShouldReturnIllegalArgumentException_WhenInvalidAuthHeader(String invalidAuthHeader) {
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(invalidAuthHeader, PRODUCT_ID, withdrawOrPutProductDTO));
    }

    @ParameterizedTest
    @MethodSource("withdrawOrPutInvalidProducts")
    void putProduct_ShouldReturnIllegalArgumentExceptions(Integer quantity) {
        WithdrawOrPutProductDTO invalidPutProductDTO = new WithdrawOrPutProductDTO(quantity);
        assertThrows(IllegalArgumentException.class, () -> productService.putProduct(authHeader, PRODUCT_ID, invalidPutProductDTO));
    }

}