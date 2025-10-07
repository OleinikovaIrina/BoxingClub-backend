package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/user/memberships")
public interface UserMembershipApi extends UserMembershipApiSwaggerDoc{

    @Override
    @PermitAll
    @PostMapping("/{userId}")
    MembershipResponseDto createMembership(@PathVariable UUID userId, @RequestBody @Valid MembershipCreateRequestDto dto);

    @Override
    @PermitAll
    @GetMapping("/{userId}")
    List<MembershipResponseDto> getMembershipsByUserId(@PathVariable UUID userId);

    @Override
    @PermitAll
    @PostMapping("/{membershipId}/cancel")
    MembershipResponseDto cancelMembership(@PathVariable UUID membershipId);
}
