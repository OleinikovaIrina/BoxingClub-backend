package de.oleinikova.boxingclub.backend.session.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus;
import de.oleinikova.boxingclub.backend.membership.persistence.MembershipRepository;
import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;
import de.oleinikova.boxingclub.backend.session.entity.Booking;
import de.oleinikova.boxingclub.backend.session.entity.SessionType;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import de.oleinikova.boxingclub.backend.session.exception.BookingNotFoundException;
import de.oleinikova.boxingclub.backend.session.exception.SessionNotFoundException;
import de.oleinikova.boxingclub.backend.session.persistence.BookingRepository;
import de.oleinikova.boxingclub.backend.session.persistence.TrainingSessionRepository;
import de.oleinikova.boxingclub.backend.session.service.interfaces.BookingService;
import de.oleinikova.boxingclub.backend.session.util.BookingMapper;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final MembershipRepository membershipRepository;
    private final AppUserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingCreateRequestDto dto, String email) {

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);

        TrainingSession trainingSession = trainingSessionRepository
                .findByIdAndCancelledFalse(dto.sessionId())
                .orElseThrow(SessionNotFoundException::new);


        if (trainingSession.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RestApiException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot book past sessions"
            );
        }

        LocalDate sessionDate = trainingSession.getSessionDate();

        boolean hasActiveMembership = membershipRepository
                .findByUser_Id(user.getId())
                .stream()
                .anyMatch(m ->
                        m.getStatus() == MembershipStatus.APPROVED &&
                                m.getStartDate() != null &&
                                m.getEndDate() != null &&
                                !sessionDate.isBefore(m.getStartDate()) &&
                                !sessionDate.isAfter(m.getEndDate())
                );

        if (!hasActiveMembership) {
            throw new RestApiException(
                    HttpStatus.FORBIDDEN,
                    "Membership is not valid for the session date"
            );
        }

        Optional<Booking> existingBooking =
                bookingRepository.findBySession_IdAndUser_Id(
                        trainingSession.getId(),
                        user.getId()
                );

        if (existingBooking.isPresent()) {

            Booking booking = existingBooking.get();

            if (!booking.isCancelled()) {
                throw new RestApiException(
                        HttpStatus.BAD_REQUEST,
                        "Already booked"
                );
            }

            long count = bookingRepository
                    .countBySession_IdAndCancelledFalse(trainingSession.getId());

            if (trainingSession.getType() == SessionType.GROUP
                    && count >= trainingSession.getMaxParticipants()) {

                throw new RestApiException(
                        HttpStatus.BAD_REQUEST,
                        "Session is full"
                );
            }

            if (trainingSession.getType() == SessionType.INDIVIDUAL
                    && count >= 1) {

                throw new RestApiException(
                        HttpStatus.BAD_REQUEST,
                        "Individual session already booked"
                );
            }

            booking.setCancelled(false);
            booking.setBookedAt(LocalDateTime.now());

            Booking saved = bookingRepository.save(booking);

            return mapper.toResponseDto(saved);
        }

        long count = bookingRepository
                .countBySession_IdAndCancelledFalse(trainingSession.getId());

        if (trainingSession.getType() == SessionType.GROUP
                && count >= trainingSession.getMaxParticipants()) {

            throw new RestApiException(
                    HttpStatus.BAD_REQUEST,
                    "Session is full"
            );
        }

        if (trainingSession.getType() == SessionType.INDIVIDUAL
                && count >= 1) {

            throw new RestApiException(
                    HttpStatus.BAD_REQUEST,
                    "Individual session already booked"
            );
        }

        Booking booking = new Booking();

        booking.setSession(trainingSession);
        booking.setUser(user);
        booking.setBookedAt(LocalDateTime.now());

        try {

            Booking saved = bookingRepository.save(booking);

            return mapper.toResponseDto(saved);

        } catch (DataIntegrityViolationException e) {

            throw new RestApiException(
                    HttpStatus.CONFLICT,
                    "You already booked this training"
            );
        }
    }

    @Transactional
    @Override
    public void cancelBooking(UUID bookingId, String email) {

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new RestApiException(HttpStatus.FORBIDDEN, "You can cancel only your own booking");
        }

        if (booking.isCancelled()) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Booking already cancelled");
        }

        booking.setCancelled(true);

    }

    @Override
    public List<BookingResponseDto> getAllMyBookings(String email) {

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);

        return bookingRepository.findAllByUser_IdAndCancelledFalse(user.getId())
                .stream()
                .map(mapper::toResponseDto)
                .toList();

    }
}
