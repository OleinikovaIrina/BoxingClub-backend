package de.oleinikova.boxingclub.backend.membership.controller.interfaces;

import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/admin/memberships")
public interface AdminMembershipApi extends AdminMembershipApiSwaggerDoc {

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    @GetMapping
    List<MembershipResponseDto> getMembershipByLastName(@RequestParam String lastName);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    MembershipResponseDto rejectMembership(@PathVariable("id") UUID membershipId);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    MembershipResponseDto approveMembership(@PathVariable("id") UUID membershipId);
}
