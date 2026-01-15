package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Membership", description = "Operations for authenticated users")
public interface UserMembershipApiSwaggerDoc {

    default MembershipResponseDto createMembership(MembershipCreateRequestDto dto) {
        throw new UnsupportedOperationException();
    }

    default List<MembershipResponseDto> getMyMemberships() {
        throw new UnsupportedOperationException();
    }

    default MembershipResponseDto cancelMembership(UUID membershipId) {
        throw new UnsupportedOperationException();
    }
}