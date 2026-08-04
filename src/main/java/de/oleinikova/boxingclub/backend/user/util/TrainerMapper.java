package de.oleinikova.boxingclub.backend.user.util;


import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {

    TrainerResponseDto toTrainerResponseDto(AppUser user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "confirmationStatus", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "telegramChatId", ignore = true)
    AppUser toEntity (TrainerCreateRequestDto dto);
}
