package de.oleinikova.boxingclub.backend.session.util;

import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingSessionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "startTime", source = "dateTime")
    @Mapping(target = "cancelled", ignore = true)
    TrainingSession toEntity(TrainingSessionCreateRequestDto dto);

    @Mapping(target = "trainerLastName", source = "session.trainer.lastName")
    @Mapping(target = "trainerId", source = "session.trainer.id")
    @Mapping(target = "dateTime", source = "session.startTime")
    TrainingSessionResponseDto toDto(TrainingSession session, Integer availableSlots);
}
