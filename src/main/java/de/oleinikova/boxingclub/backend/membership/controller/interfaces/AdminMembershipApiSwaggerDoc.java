package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin Membership", description = "Operations for admin")
public interface AdminMembershipApiSwaggerDoc {

    @Operation(summary = "Get all memberships of a user")
    List<MembershipResponseDto> getMembershipByLastName(String lastName);

    @Operation(summary = "Reject a membership")
    MembershipResponseDto rejectMembership(UUID membershipId);

    @Operation(summary = "Approve a membership")
    MembershipResponseDto approveMembership(UUID membershipId);

    @Operation(summary = "Find  currently active memberships")
    List<MembershipResponseDto> getActiveMemberships();

    @Operation(summary = "Find  pending memberships")
    List<MembershipResponseDto> getPendingMemberships();

}
