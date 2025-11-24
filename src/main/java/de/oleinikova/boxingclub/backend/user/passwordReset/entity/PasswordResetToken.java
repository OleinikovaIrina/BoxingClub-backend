package de.oleinikova.boxingclub.backend.user.passwordReset.entity;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private PasswordResetStatus status;

    public static PasswordResetToken createForUser(AppUser user) {
        return PasswordResetToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(PasswordResetStatus.PENDING)
                .build();

    }
}



