package com.produto.api.repository;

import com.produto.api.entity.DeleteUserToken;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeleteUserTokenRepository extends JpaRepository<DeleteUserToken, UUID> {
    @Query("SELECT t FROM DeleteUserToken t WHERE t.token = :token")
    Optional<DeleteUserToken> findByToken(@NotEmpty @Param("token") UUID token);
}
