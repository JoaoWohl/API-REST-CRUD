package com.produto.api.repository;

import com.produto.api.entity.Product;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByUserIdAndName(@NotEmpty UUID UserId, @NotEmpty String name);
    List<Product> findAllByUserId(@NotEmpty UUID UserId);
    Optional<Product> findByUserIdAndId(@NotEmpty UUID UserId, @NotEmpty UUID id);
}
