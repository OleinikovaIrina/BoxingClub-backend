package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/user/memberships")
public interface UserMembershipApi {


    @PostMapping
    MembershipResponseDto createMembership(@RequestBody @Valid MembershipCreateRequestDto dto, Authentication authentication);


    @GetMapping
    List<MembershipResponseDto> getMyMemberships(Authentication authentication);


    @PostMapping("/{membershipId}/cancel")
    MembershipResponseDto cancelMembership(@PathVariable UUID membershipId, Authentication authentication);
}
