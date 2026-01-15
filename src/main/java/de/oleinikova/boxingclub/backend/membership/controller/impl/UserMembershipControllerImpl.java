package de.oleinikova.boxingclub.backend.membership.controller.impl;

import de.oleinikova.boxingclub.backend.membership.controller.interfaces.UserMembershipApi;
import de.oleinikova.boxingclub.backend.membership.controller.interfaces.UserMembershipApiSwaggerDoc;
import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.service.interfaces.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
public class UserMembershipControllerImpl  implements UserMembershipApi, UserMembershipApiSwaggerDoc {

    private final MembershipService membershipService;

    @Override
    public MembershipResponseDto createMembership(MembershipCreateRequestDto dto, Authentication authentication) {

        String email = authentication.getName();
        return membershipService.createMembershipByEmail(email, dto);
    }

    @Override
    public List<MembershipResponseDto> getMyMemberships(Authentication authentication) {
        String email = authentication.getName();
        return membershipService.getMembershipsByEmail(email);
    }

    @Override
    public MembershipResponseDto cancelMembership(UUID membershipId,Authentication authentication  ) {
        String email = authentication.getName();
        return membershipService.cancelMembershipByEmail(membershipId,email);
    }
}
