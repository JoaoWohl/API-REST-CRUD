package com.produto.api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.integration.BaseIntegrationTest;
import com.produto.api.repository.ProductRepository;
import com.produto.api.repository.UserRepository;
import com.produto.api.service.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends BaseIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(UserRole role) {
        User user = new User();
        user.setName("Test Name");
        user.setLogin("testlogin@example.com");
        user.setPassword("TestPassword");
        user.setRole(role);

        return userRepository.save(user);
    }
    private String authenticateUser(User user) {
        return jwtTokenService.generateToken(user);
    }

    //postProduct
    @Test
    void postProduct_ShouldReturnOk() throws Exception {
        String adminToken = authenticateUser(createUser(UserRole.ADMIN));
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("4000.99"), 10);

        mockMvc.perform(post("/products")
            .header("Authorization", "Bearer "+ adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().getName()).isEqualTo("Notebook");
    }

    @Test
    void postProduct_ShouldReturnForbiddenError_WhenRoleUser() throws Exception {
        String userToken = authenticateUser(createUser(UserRole.USER));
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("4000.99"), 10);

        mockMvc.perform(post("/products")
            .header("Authorization", "Bearer " + userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(0);
    }

    @Test
    void postProduct_ShouldReturnForbiddenError_WhenNotHaveAuthToken() throws Exception {
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("4000.99"), 10);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
    }

    static Stream<Arguments> invalidAddProducts() {
        return Stream.of(
                Arguments.of("Product Name", new BigDecimal("100.99"), -100),
                Arguments.of("Product Name", new BigDecimal("100.99"), 0),
                Arguments.of("Product Name", new BigDecimal("-100.99"), 100),
                Arguments.of("Product Name", new BigDecimal("0"), 100),
                Arguments.of("Product Name", null, 100),
                Arguments.of("", new BigDecimal("100.99"), 100),
                Arguments.of(" ", new BigDecimal("100.99"), 100),
                Arguments.of(null, new BigDecimal("100.99"), 100)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAddProducts")
    void postProduct_ShouldReturnBadRequestError(String name, BigDecimal price, int quantity) throws Exception {
        AddProductDTO request = new AddProductDTO (name, price, quantity);

        mockMvc.perform(post("/products")
            .with(user("usuario").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(0);
    }

    //getAllProduct
    @Test
    void getAllProducts_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.USER);
        String userToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        productRepository.save(product);

        mockMvc.perform(get("/products")
            .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Notebook"))
        .andExpect(jsonPath("$[0].price").value(4000.99))
        .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    void getAllProducts_ShouldReturnNotFoundError() throws Exception {
        String userToken = authenticateUser(createUser(UserRole.USER));
        mockMvc.perform(get("/products")
            .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isNotFound());
    }

    @Test
    void getAllProducts_ShouldReturnForbiddenError() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().isForbidden());
    }

    //getById
    @Test
    void getProductById_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.USER);
        String userToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/products/{id}", saved.getId())
            .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId().toString()))
        .andExpect(jsonPath("$.name").value("Notebook"))
        .andExpect(jsonPath("$.price").value(4000.99))
        .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getProductById_ShouldReturnNotFoundError() throws Exception {
        String userToken = authenticateUser(createUser(UserRole.USER));
        mockMvc.perform(get("/products/{id}", UUID.randomUUID())
            .header("Authorization", "Bearer " + userToken))
        .andExpect(status().isNotFound());
    }

    @Test
    void getProductById_ShouldReturnForbiddenError() throws Exception {
        mockMvc.perform(get("/products/1"))
            .andExpect(status().isForbidden());
    }

    //deleteById
    @Test
    void deleteProductById_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.ADMIN);
        String adminToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/products/{id}", saved.getId())
            .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk());
    }

    @Test
    void deleteProductById_ShouldReturnNotFoundError() throws Exception {
        String adminToken = authenticateUser(createUser(UserRole.ADMIN));
        mockMvc.perform(delete("/products/{id}", UUID.randomUUID())
            .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductById_ShouldReturnForbiddenError() throws Exception {
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/products/{id}", saved.getId())
            .with(user("usuario").roles("USER")))
        .andExpect(status().isForbidden());
    }

    //updateById
    @Test
    void updateProduct_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.ADMIN);
        String adminToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);

        mockMvc.perform(patch("/products/{id}", saved.getId())
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getName()).isEqualTo("Notebook Gamer");
        assertThat(products.getFirst().getPrice()).isEqualTo(new BigDecimal("4500.89"));
        assertThat(products.getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void updateProduct_ShouldReturnNotFoundError() throws Exception {
        String adminToken = authenticateUser(createUser(UserRole.ADMIN));
        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);
        mockMvc.perform(patch("/products/{id}", UUID.randomUUID())
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_ShouldReturnForbiddenError() throws Exception {
        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);
        mockMvc.perform(patch("/products/{id}", UUID.randomUUID())
                        .with(user("usuario").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    //putProduct
    @Test
    void putProduct_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.ADMIN);
        String adminToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(57);

        mockMvc.perform(patch("/products/{id}/put", saved.getId())
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }

    @Test
    void putProduct_ShouldReturnNotFoundError() throws Exception {
        String adminToken = authenticateUser(createUser(UserRole.ADMIN));
        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(67);

        mockMvc.perform(patch("/products/{id}/put", UUID.randomUUID())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    //withdrawProduct
    @Test
    void withdrawProduct_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.ADMIN);
        String adminToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(77);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(10);

        mockMvc.perform(patch("/products/{id}/withdraw", saved.getId())
                .header("Authorization", "Bearer " +  adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }

    @Test
    void withdrawProduct_ShouldReturnNotFoundError() throws Exception {
        String adminToken = authenticateUser(createUser(UserRole.ADMIN));
        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(10);

        mockMvc.perform(patch("/products/{id}/withdraw", UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void withdrawProduct_ShouldReturnForbiddenError() throws Exception {
        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(10);

        mockMvc.perform(patch(("/products/1/withdraw"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void withdrawProduct_ShouldNotEnoughProductError() throws Exception {
        User user = createUser(UserRole.ADMIN);
        String adminToken = authenticateUser(user);
        Product product = new Product();
        product.setQuantity(67);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        product.setUser(user);
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(68);

        mockMvc.perform(patch("/products/{id}/withdraw", saved.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }
}