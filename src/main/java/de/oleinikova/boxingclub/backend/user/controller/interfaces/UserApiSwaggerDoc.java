package de.oleinikova.boxingclub.backend.user.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name= "User Service",description = "Admin operations with users")
public interface UserApiSwaggerDoc {

    @Operation(summary = "Create user")
    UserCreateResponseDto createUser(UserCreateDto userCreateDto);

    @Operation(summary = "Get user by ID")
    UserResponseDto getUserById(UUID userId);

    @Operation(summary = "Get user by email")
    UserResponseDto getUserByEmail(String email);

    @Operation(summary = "Get all users ")
    List<UserResponseDto> getAllUsers();

    @Operation(summary = "Delete user by ID")
    void deleteUser(UUID id);
}
