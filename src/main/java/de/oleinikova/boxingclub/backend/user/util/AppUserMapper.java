package de.oleinikova.boxingclub.backend.user.util;


import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "confirmationStatus", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    AppUser toEntity(UserCreateDto dto);

    @Mapping(target = "confirmationResent", constant = "false")
    UserCreateResponseDto toCreateResponseDto(AppUser user);

    UserResponseDto toResponseDto(AppUser user);

}
