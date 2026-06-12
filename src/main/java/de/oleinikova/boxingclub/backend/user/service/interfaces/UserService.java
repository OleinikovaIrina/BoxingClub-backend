package de.oleinikova.boxingclub.backend.user.service.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserCreateResponseDto createUser(UserCreateDto userCreateDto);

    UserResponseDto getUserById(UUID userId);

    List<TrainerResponseDto> getUserByRole(Role role);

    UserResponseDto getUserByEmail(String email);

    List<UserResponseDto> getAllUsers();

    void deleteUser(UUID id);

}

