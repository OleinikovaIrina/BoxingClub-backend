package de.oleinikova.boxingclub.backend.user.util;


import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {

    TrainerResponseDto toTrainerResponseDto(AppUser user);
}
