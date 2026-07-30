package com.produto.api.entity;

import com.produto.api.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@Entity
@Table(name = "delete_tokens")
public class DeleteUserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, name = "id")
    private UUID id;

    @Column(unique = true, nullable = false, name = "token")
    private UUID token = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expires_at = LocalDateTime.now().plusHours(12);

    @Column(name = "used")
    private boolean used;
}
