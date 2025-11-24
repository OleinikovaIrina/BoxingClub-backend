package de.oleinikova.boxingclub.backend.membership.controller.impl;

import de.oleinikova.boxingclub.backend.membership.controller.interfaces.AdminMembershipApi;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.service.interfaces.MembershipService;
import de.oleinikova.boxingclub.backend.membership.util.MembershipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for admin membership operations.
 * <p>
 * Provides functionality to search memberships by last name
 * and to approve or reject membership requests.
 * Delegates business logic to {@link MembershipService}.
 */

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminMembershipControllerImpl implements AdminMembershipApi {

    private final MembershipService membershipService;
    private  final MembershipMapper membershipMapper;

    @Override
    public List<MembershipResponseDto> getMembershipByLastName(String lastName) {
        return membershipService.getMembershipByLastName(lastName);
    }

    @Override
    public MembershipResponseDto rejectMembership(UUID membershipId) {
        return membershipService.rejectMembership(membershipId);
    }

    @Override
    public MembershipResponseDto approveMembership(UUID membershipId) {
        return membershipService.approveMembership(membershipId);
    }

    @Override
    public List<MembershipResponseDto> getActiveMemberships() {
        return membershipService.getActiveMemberships();
    }
}

