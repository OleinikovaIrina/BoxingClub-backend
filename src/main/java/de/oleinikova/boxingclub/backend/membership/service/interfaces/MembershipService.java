package de.oleinikova.boxingclub.backend.membership.service.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.entity.Membership;

import java.util.List;
import java.util.UUID;

public interface MembershipService {
    MembershipResponseDto createMembership(UUID userId, MembershipCreateRequestDto membershipCreateRequestDto);

    List<MembershipResponseDto> getMembershipsByUserId(UUID userId);

    List<MembershipResponseDto> getMembershipByLastName(String lastName);

    MembershipResponseDto cancelMembership(UUID membershipId);

    MembershipResponseDto rejectMembership(UUID membershipId);

    MembershipResponseDto approveMembership(UUID membershipId);

    List<MembershipResponseDto> getActiveMemberships();

    List<MembershipResponseDto> getPendingMemberships();


    // ===== adapter methods (for controllers) =====

    MembershipResponseDto createMembershipByEmail(String email, MembershipCreateRequestDto dto);

    List<MembershipResponseDto> getMembershipsByEmail(String email);

    MembershipResponseDto cancelMembershipByEmail(UUID membershipId, String email
    );
}
