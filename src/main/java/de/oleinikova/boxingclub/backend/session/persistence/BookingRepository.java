package de.oleinikova.boxingclub.backend.session.persistence;

import de.oleinikova.boxingclub.backend.session.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findBySession_IdAndUser_Id(UUID sessionId, UUID userId);

    boolean existsBySession_IdAndUser_IdAndCancelledFalse(UUID sessionId, UUID userId);

    long countBySession_IdAndCancelledFalse(UUID sessionId);

    List<Booking> findAllByUser_IdAndCancelledFalse(UUID userId);
}