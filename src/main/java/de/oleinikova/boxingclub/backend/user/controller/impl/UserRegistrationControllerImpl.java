package de.oleinikova.boxingclub.backend.user.controller.impl;

import de.oleinikova.boxingclub.backend.user.controller.interfaces.UserRegistrationApi;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRegistrationControllerImpl implements UserRegistrationApi {

    private final UserRegisterService userRegisterService;

    @Override
    public UserCreateResponseDto register(UserCreateDto userCreateDto) {
        return userRegisterService.register(userCreateDto);
    }

    @Override
    public UserResponseDto confirmRegistration(String code) {
        return userRegisterService.confirmRegistration(code);
    }
}
