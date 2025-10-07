package de.oleinikova.boxingclub.backend.membership.controller.impl;

import de.oleinikova.boxingclub.backend.membership.controller.interfaces.UserMembershipApi;
import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.service.interfaces.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for user membership operations.
 * <p>
 * Handles creation, retrieval, and cancellation of memberships for a specific user.
 * Delegates business logic to {@link MembershipService}.
 */

@RestController
@RequiredArgsConstructor
public class UserMembershipControllerImpl implements UserMembershipApi {

private  final MembershipService membershipService;

    @Override
    public MembershipResponseDto createMembership(UUID userId, MembershipCreateRequestDto dto) {
        return membershipService.createMembership(userId, dto);
    }

    @Override
    public List<MembershipResponseDto> getMembershipsByUserId(UUID userId) {
        return membershipService.getMembershipsByUserId(userId);
    }

    @Override
    public MembershipResponseDto cancelMembership(UUID membershipId) {
        return membershipService.cancelMembership(membershipId);
    }
}
