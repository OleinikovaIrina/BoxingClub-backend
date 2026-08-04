package de.oleinikova.boxingclub.backend.telegram.link.entity;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "telegram_link_token",
        indexes = {
                @Index(
                        name = "idx_telegram_link_token_user_status",
                        columnList = "user_id, status"
                )
        }
)
public class TelegramLinkToken {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TelegramLinkTokenStatus status = TelegramLinkTokenStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;
}
