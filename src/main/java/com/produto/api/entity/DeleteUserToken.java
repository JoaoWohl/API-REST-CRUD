package com.produto.api.entity;

import com.produto.api.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@Entity
@Table(name = "delete_tokens")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class DeleteUserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, name = "id")
    private UUID id;

    @Column(unique = true, nullable = false, name = "token")
    private UUID token = UUID.randomUUID();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    @NonNull
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expires_at = LocalDateTime.now().plusHours(12);

    @Column(name = "used")
    private boolean used;
}
