package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Membership", description = "Operations for regular users")
public interface UserMembershipApiSwaggerDoc {

    @Operation(summary = "Create membership for a specific user")
    MembershipResponseDto createMembership(UUID userId, MembershipCreateRequestDto dto);

    @Operation(summary = "Get all memberships of a user")
    List<MembershipResponseDto> getMembershipsByUserId(UUID userId);

    @Operation(summary = "Cancel a membership (soft delete)")
    MembershipResponseDto cancelMembership(UUID membershipId);
}