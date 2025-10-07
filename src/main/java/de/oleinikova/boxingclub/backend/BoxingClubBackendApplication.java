package de.oleinikova.boxingclub.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BoxingClubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxingClubBackendApplication.class, args);
    }

}
