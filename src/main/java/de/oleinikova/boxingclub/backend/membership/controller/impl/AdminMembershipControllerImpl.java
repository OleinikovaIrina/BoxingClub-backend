package de.oleinikova.boxingclub.backend.membership.controller.impl;

import de.oleinikova.boxingclub.backend.membership.controller.interfaces.AdminMembershipApi;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.service.interfaces.MembershipService;
import lombok.RequiredArgsConstructor;
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
public class AdminMembershipControllerImpl implements AdminMembershipApi {

    private final MembershipService membershipService;

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
}
