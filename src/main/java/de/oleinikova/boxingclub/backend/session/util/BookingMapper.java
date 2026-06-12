package de.oleinikova.boxingclub.backend.session.util;

import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;
import de.oleinikova.boxingclub.backend.session.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bookedAt", ignore = true)
    @Mapping(target = "cancelled", ignore = true)
    Booking toEntity(BookingCreateRequestDto dto);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "title", source = "session.title")
    @Mapping(target = "dateTime", source = "session.startTime")
    @Mapping(target = "type", source = "session.type")
    @Mapping(target = "trainerLastName", source = "session.trainer.lastName")
    BookingResponseDto toResponseDto(Booking booking);


}
