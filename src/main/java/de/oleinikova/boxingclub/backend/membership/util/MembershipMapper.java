package de.oleinikova.boxingclub.backend.membership.util;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.entity.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

// Business fields (user, status, dates) are set in service layer intentionally
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MembershipMapper {
    @Mapping(target = "membershipId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    MembershipResponseDto toDto(Membership membership);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "active" , ignore = true)
    Membership toEntity(MembershipCreateRequestDto dto);
}