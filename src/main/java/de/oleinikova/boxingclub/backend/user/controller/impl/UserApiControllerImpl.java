package de.oleinikova.boxingclub.backend.user.controller.impl;

import de.oleinikova.boxingclub.backend.user.controller.interfaces.UserApi;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserApiControllerImpl implements UserApi {

    private final UserService userService;


    @Override
    public UserCreateResponseDto createUser(UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @Override
    public UserResponseDto getUserById(UUID userId) {
        return userService.getUserById(userId);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public void deleteUser(UUID id) {
        userService.deleteUser(id);
    }
}
