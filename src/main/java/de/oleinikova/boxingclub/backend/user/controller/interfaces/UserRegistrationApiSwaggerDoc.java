package de.oleinikova.boxingclub.backend.user.controller.interfaces;


import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Registration",description = "Operation Registration for regular user")
public interface UserRegistrationApiSwaggerDoc {

@Operation(summary = "Create user")
    UserCreateResponseDto register(UserCreateDto userCreateDto);

@Operation(summary = "Confirmation Registration")
UserResponseDto confirmRegistration(String code);

}
