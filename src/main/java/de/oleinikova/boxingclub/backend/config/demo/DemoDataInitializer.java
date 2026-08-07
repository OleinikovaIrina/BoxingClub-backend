package de.oleinikova.boxingclub.backend.config.demo;

import de.oleinikova.boxingclub.backend.membership.entity.Membership;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipType;
import de.oleinikova.boxingclub.backend.membership.persistence.MembershipRepository;
import de.oleinikova.boxingclub.backend.session.entity.SessionType;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import de.oleinikova.boxingclub.backend.session.persistence.TrainingSessionRepository;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.demo-data.enabled",
        havingValue = "true"
)
public class DemoDataInitializer implements CommandLineRunner {

    private static final String USER_EMAIL = "user@test.com";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String TRAINER_ONE_EMAIL = "trainer1@test.com";
    private static final String TRAINER_TWO_EMAIL = "trainer2@test.com";
    private static final int DEMO_SCHEDULE_WEEKS = 13;

    private final AppUserRepository userRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        AppUser demoUser = createUserIfMissing(
                "Demo",
                "User",
                USER_EMAIL,
                "Password@1",
                Role.ROLE_USER
        );


        createUserIfMissing(
                "Demo",
                "Admin",
                ADMIN_EMAIL,
                "Password@2",
                Role.ROLE_ADMIN
        );

        AppUser trainerOne = createUserIfMissing(
                "Daniel",
                "Fischer",
                TRAINER_ONE_EMAIL,
                "Password@3",
                Role.ROLE_TRAINER
        );

        AppUser trainerTwo = createUserIfMissing(
                "Anna",
                "Weber",
                TRAINER_TWO_EMAIL,
                "Password@4",
                Role.ROLE_TRAINER
        );

        createMembershipIfMissing(demoUser);

        for (int weekOffset = 0;
             weekOffset < DEMO_SCHEDULE_WEEKS;
             weekOffset++) {

            createTrainingIfMissing(
                    trainerOne,
                    "Beginner Boxing",
                    SessionType.GROUP,
                    10,
                    nextOccurrence(
                            DayOfWeek.TUESDAY,
                            LocalTime.of(18, 0)
                    ).plusWeeks(weekOffset),
                    60
            );

            createTrainingIfMissing(
                    trainerOne,
                    "Individual Coaching",
                    SessionType.INDIVIDUAL,
                    1,
                    nextOccurrence(
                            DayOfWeek.WEDNESDAY,
                            LocalTime.of(17, 0)
                    ).plusWeeks(weekOffset),
                    60
            );

            createTrainingIfMissing(
                    trainerTwo,
                    "Boxing Fitness",
                    SessionType.GROUP,
                    12,
                    nextOccurrence(
                            DayOfWeek.THURSDAY,
                            LocalTime.of(19, 0)
                    ).plusWeeks(weekOffset),
                    60
            );

            createTrainingIfMissing(
                    trainerTwo,
                    "Technique Training",
                    SessionType.GROUP,
                    8,
                    nextOccurrence(
                            DayOfWeek.SATURDAY,
                            LocalTime.of(10, 0)
                    ).plusWeeks(weekOffset),
                    75
            );
        }

        log.info("Demo data initialization completed");
    }

    private AppUser createUserIfMissing(
            String firstName,
            String lastName,
            String email,
            String rawPassword,
            Role role
    ) {

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    AppUser user = new AppUser();

                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPassword(
                            passwordEncoder.encode(rawPassword)
                    );
                    user.setRole(role);
                    user.setConfirmationStatus(
                            ConfirmationStatus.CONFIRMED
                    );
                    user.setEnabled(true);

                    AppUser savedUser = userRepository.save(user);

                    log.info(
                            "Demo user created: {} with role {}",
                            email,
                            role
                    );

                    return savedUser;
                });
    }

    private void createMembershipIfMissing(AppUser user) {

        boolean hasActiveMembership = membershipRepository
                .findByUser_Id(user.getId())
                .stream()
                .anyMatch(Membership::isCurrentlyActive);

        if (hasActiveMembership) {
            log.info(
                    "Active demo membership already exists for: {}",
                    user.getEmail()
            );
            return;
        }

        LocalDate today = LocalDate.now();

        Membership membership = new Membership();

        membership.setUser(user);
        membership.setStreet("Musterstrasse 1");
        membership.setPostalCode("60311");
        membership.setCity("Frankfurt am Main");
        membership.setIban(null);

        membership.setType(MembershipType.ADULT);
        membership.setDuration(MembershipDuration.YEARLY);

        membership.setHasDiscount(false);
        membership.setConsentToSepa(false);
        membership.setConsentToDataPolicy(true);

        membership.activate(
                today,
                today.plusYears(1)
        );

        membershipRepository.save(membership);

        log.info(
                "Active demo membership created for: {}",
                user.getEmail()
        );
    }

    private void createTrainingIfMissing(
            AppUser trainer,
            String title,
            SessionType type,
            int maxParticipants,
            LocalDateTime startTime,
            int durationMinutes
    ) {

        boolean alreadyExists =
                trainingSessionRepository
                        .existsByTrainer_IdAndStartTimeAndCancelledFalse(
                                trainer.getId(),
                                startTime
                        );

        if (alreadyExists) {
            return;
        }

        TrainingSession session = new TrainingSession();

        session.setTrainer(trainer);
        session.setTitle(title);
        session.setType(type);
        session.setMaxParticipants(maxParticipants);
        session.setStartTime(startTime);
        session.setDurationMinutes(durationMinutes);
        session.setCancelled(false);

        trainingSessionRepository.save(session);

        log.info(
                "Demo training created: {} at {}",
                title,
                startTime
        );
    }

    private LocalDateTime nextOccurrence(
            DayOfWeek dayOfWeek,
            LocalTime time
    ) {

        LocalDate date = LocalDate
                .now()
                .with(TemporalAdjusters.nextOrSame(dayOfWeek));

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        if (!dateTime.isAfter(LocalDateTime.now())) {
            return dateTime.plusWeeks(1);
        }

        return dateTime;
    }
}

