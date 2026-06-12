package de.oleinikova.boxingclub.backend.session.persistence;

import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TrainingSession> findByIdAndCancelledFalse(UUID id);

    List<TrainingSession> findAllByTitleIgnoreCaseAndCancelledFalse(String title);

    List<TrainingSession> findAllByTitleIgnoreCaseAndCancelledFalseAndStartTimeAfter(String title, LocalDateTime dateTime);

    List<TrainingSession> findAllByTrainer_IdAndCancelledFalse(UUID trainerId);

    List<TrainingSession> findAllByTrainer_IdAndCancelledFalseAndStartTimeAfter(UUID trainerId, LocalDateTime dateTime);

    List<TrainingSession> findAllByTrainer_LastNameIgnoreCaseAndCancelledFalse(String lastName);

    List<TrainingSession> findAllByTrainer_LastNameIgnoreCaseAndCancelledFalseAndStartTimeAfter(String lastName, LocalDateTime dateTime);

    List<TrainingSession> findAllByCancelledFalse();

    List<TrainingSession> findAllByCancelledFalseAndStartTimeAfter(LocalDateTime dateTime);

    boolean existsByTrainer_IdAndStartTime(UUID trainerId, LocalDateTime startTime);
}
