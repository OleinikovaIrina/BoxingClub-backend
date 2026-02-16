package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/admin/memberships")
public interface AdminMembershipApi extends AdminMembershipApiSwaggerDoc {
    @Override
    @GetMapping
    List<MembershipResponseDto> getMembershipByLastName(@RequestParam String lastName);

    @Override
    @PostMapping("/{id}/reject")
    MembershipResponseDto rejectMembership(@PathVariable("id") UUID membershipId);

    @Override
    @PostMapping("/{id}/approve")
    MembershipResponseDto approveMembership(@PathVariable("id") UUID membershipId);

    @Override
    @GetMapping("/active")
    List<MembershipResponseDto> getActiveMemberships();

    @Override
    @GetMapping("/pending")
    List<MembershipResponseDto> getPendingMemberships();
}
