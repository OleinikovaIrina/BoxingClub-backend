package de.oleinikova.boxingclub.backend.membership.persistence;

import de.oleinikova.boxingclub.backend.membership.entity.Membership;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface MembershipRepository extends JpaRepository<Membership, UUID> {


    List<Membership> findAllByUser_LastNameIgnoreCase(String appUserLastName);

    List<Membership> findAllByUser_EmailIgnoreCase(String email);

    List<Membership> findByUser_Id(UUID userId);

    @Query("""
    SELECT m
    FROM Membership m
    WHERE m.status = de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus.APPROVED
      AND m.startDate IS NOT NULL
      AND m.startDate <= CURRENT_DATE
      AND (m.endDate IS NULL OR m.endDate >= CURRENT_DATE)
""")
    List<Membership> findCurrentlyActiveMemberships();

    boolean existsByUser_IdAndDurationAndStatus(
            UUID userId,
            MembershipDuration duration,
            MembershipStatus status
    );

    List<Membership> findByStatus(MembershipStatus membershipStatus);
}
