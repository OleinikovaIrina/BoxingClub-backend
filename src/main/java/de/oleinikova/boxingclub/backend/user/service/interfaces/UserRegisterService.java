package de.oleinikova.boxingclub.backend.user.service.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;

public interface UserRegisterService {

    UserCreateResponseDto register(UserCreateDto userCreateDto);

    UserResponseDto confirmRegistration(String code);



}
