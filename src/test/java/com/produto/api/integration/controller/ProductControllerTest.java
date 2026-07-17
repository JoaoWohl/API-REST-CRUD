package com.produto.api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.produto.api.dto.request.product.AddProductDTO;
import com.produto.api.dto.request.product.UpdateProductDTO;
import com.produto.api.dto.request.product.WithdrawOrPutProductDTO;
import com.produto.api.entity.Product;
import com.produto.api.integration.BaseIntegrationTest;
import com.produto.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

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

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
    }

    //postProduct
    @Test
    void postProduct_ShouldReturnOk() throws Exception {
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("4000.99"), 10);

        mockMvc.perform(post("/products")
            .with(user("usuario").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().getName()).isEqualTo("Notebook");
    }

    @Test
    void postProduct_ShouldReturnForbiddenError() throws Exception {
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("4000.99"), 10);

        mockMvc.perform(post("/products")
                        .with(user("usuario").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(0);
    }

    @Test
    void postProduct_ShouldReturnBadRequestError() throws Exception {
        AddProductDTO request = new AddProductDTO ("Notebook", new BigDecimal("-4000.99"), null);

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
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        productRepository.save(product);

        mockMvc.perform(get("/products")
                .with(user("usuario").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Notebook"))
                .andExpect(jsonPath("$[0].price").value(4000.99))
                .andExpect(jsonPath("$[0].quantity").value(10));
    }

    @Test
    void getAllProducts_ShouldReturnNotFoundError() throws Exception {

        mockMvc.perform(get("/products")
                .with(user("usuário").roles("USER")))
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
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/products/{id}", saved.getId())
                .with(user("usuario").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(4000.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getProductById_ShouldReturnNotFoundError() throws Exception {
        mockMvc.perform(get("/products/1")
                .with(user("usuario").roles("USER")))
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
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/products/{id}", saved.getId())
                .with(user("usuario").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProductById_ShouldReturnNotFoundError() throws Exception {
        mockMvc.perform(delete("/products/1")
                .with(user("usuario").roles("ADMIN")))
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
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);

        mockMvc.perform(patch("/products/{id}", saved.getId())
            .with(user("usuario").roles("ADMIN"))
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
        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);
        mockMvc.perform(patch("/products/1")
                        .with(user("usuario").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_ShouldReturnForbiddenError() throws Exception {
        UpdateProductDTO request = new UpdateProductDTO("Notebook Gamer", new BigDecimal("4500.89"), 5);
        mockMvc.perform(patch("/products/1")
                        .with(user("usuario").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    //putProduct
    @Test
    void putProduct_ShouldReturnOk() throws Exception {
        Product product = new Product();
        product.setQuantity(10);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(57);

        mockMvc.perform(patch("/products/{id}/put", saved.getId())
                .with(user("usuario").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }

    @Test
    void putProduct_ShouldReturnNotFoundError() throws Exception {
        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(67);

        mockMvc.perform(patch("/products/1/put")
                .with(user("usuario").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    //withdrawProduct
    @Test
    void withdrawProduct_ShouldReturnOk() throws Exception {
        Product product = new Product();
        product.setQuantity(77);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(10);

        mockMvc.perform(patch("/products/{id}/withdraw", saved.getId())
                .with(user("usuario").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }

    @Test
    void withdrawProduct_ShouldReturnNotFoundError() throws Exception {
        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(10);

        mockMvc.perform(patch("/products/1/withdraw")
                        .with(user("usuario").roles("USER"))
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
        Product product = new Product();
        product.setQuantity(67);
        product.setName("Notebook");
        product.setPrice(new BigDecimal("4000.99"));
        Product saved = productRepository.save(product);

        WithdrawOrPutProductDTO request = new WithdrawOrPutProductDTO(68);

        mockMvc.perform(patch("/products/{id}/withdraw", saved.getId())
                        .with(user("usuario").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        List<Product> products = productRepository.findAll();
        assertThat(products.getFirst().getQuantity()).isEqualTo(67);
    }
}