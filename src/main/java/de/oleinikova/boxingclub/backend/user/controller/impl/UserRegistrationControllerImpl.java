package de.oleinikova.boxingclub.backend.user.controller.impl;

import de.oleinikova.boxingclub.backend.user.controller.interfaces.UserRegistrationApi;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserRegistrationControllerImpl implements UserRegistrationApi {

    private final UserRegisterService userRegisterService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public UserCreateResponseDto register(UserCreateDto userCreateDto) {
        return userRegisterService.register(userCreateDto);
    }

    @Override
    public ResponseEntity<Void> confirmRegistration(String code) {

        userRegisterService.confirmRegistration(code);

        URI redirectLocation = URI.create(frontendUrl + "/#/login?confirmed=true");

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(redirectLocation)
                .build();

    }
}
