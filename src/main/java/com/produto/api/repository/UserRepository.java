package com.produto.api.repository;

import com.produto.api.entity.user.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(@NotEmpty String login);
    boolean existsByLogin(@NotEmpty String login);
}
