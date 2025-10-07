package de.oleinikova.boxingclub.backend.membership.entity;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "membership")
public class Membership {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 5)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(length = 34)
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipDuration duration;

    @Column(nullable = true)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    private boolean active;

    @Column(nullable = false)
    private boolean hasDiscount;

    @Column(nullable = false)
    private boolean consentToSepa;

    @Column(nullable = false)
    private boolean consentToDataPolicy;


}


