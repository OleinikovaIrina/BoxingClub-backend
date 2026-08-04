package de.oleinikova.boxingclub.backend;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BoxingClubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxingClubBackendApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(
            name = "app.demo-data.enabled",
            havingValue = "true"
    )
    CommandLineRunner initUsers(AppUserRepository userRepository, PasswordEncoder encoder) {
        return args -> {

            if (userRepository.findByEmailIgnoreCase("user@test.com").isEmpty()) {
                AppUser user = new AppUser();
                user.setFirstName("Demo");
                user.setLastName("User");
                user.setEmail("user@test.com");
                user.setPassword(encoder.encode("Password@1"));
                user.setRole(Role.ROLE_USER);
                user.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
                user.setEnabled(true);

                userRepository.save(user);
            }

            if (userRepository.findByEmailIgnoreCase("admin@test.com").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setFirstName("Demo");
                admin.setLastName("Admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(encoder.encode("Password@2"));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
                admin.setEnabled(true);

                userRepository.save(admin);
            }
        };
    }
}