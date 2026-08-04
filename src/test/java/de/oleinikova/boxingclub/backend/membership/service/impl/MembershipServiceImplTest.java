package de.oleinikova.boxingclub.backend.membership.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.entity.Membership;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipType;
import de.oleinikova.boxingclub.backend.membership.exception.MembershipNotFoundException;
import de.oleinikova.boxingclub.backend.membership.persistence.MembershipRepository;
import de.oleinikova.boxingclub.backend.membership.util.MembershipMapper;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {
    @Mock
    private MembershipRepository repository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private MembershipMapper membershipMapper;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @Test
    void should_create_membership() {

        UUID userId = UUID.randomUUID();
        AppUser existing = new AppUser();
        existing.setId(userId);
        existing.setConfirmationStatus(ConfirmationStatus.CONFIRMED);

        MembershipCreateRequestDto membershipCreateRequestDto = new MembershipCreateRequestDto(MembershipType.ADULT, MembershipDuration.MONTHLY, "Musterstraße 22", "80331", "Aschaffenburg", "  de89370400440532013000", false, true, true);
        Membership membership = new Membership();

        when(appUserRepository.findById(userId)).thenReturn(Optional.of(existing));
        when((membershipMapper).toEntity(membershipCreateRequestDto)).thenReturn(membership);
        when(repository.save(any(Membership.class)))
                .thenReturn(membership);
        membershipService.createMembership(userId, membershipCreateRequestDto);

        assertEquals(userId, membership.getUser().getId());
        assertEquals(MembershipStatus.PENDING, membership.getStatus());
        assertEquals("DE89370400440532013000", membership.getIban());
    }

    @Test
    void should_throw_exception_when_user_not_found_on_create() {
        UUID userId = UUID.randomUUID();
        MembershipCreateRequestDto membershipCreateRequestDto = new MembershipCreateRequestDto(MembershipType.ADULT, MembershipDuration.MONTHLY, "Musterstraße 22", "80331", "Aschaffenburg", "  de89370400440532013000", false, true, true);

        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> membershipService.createMembership(userId, membershipCreateRequestDto));

    }

    @Test
    void should_reject_membership() {
        UUID membershipId = UUID.randomUUID();
        Membership membership = new Membership();

        membership.setId(membershipId);
        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));
        when(repository.save(any(Membership.class))).thenReturn(membership);

        membershipService.rejectMembership(membershipId);

        assertEquals(MembershipStatus.REJECTED, membership.getStatus());
    }

    @Test
    void should_throw_exception_when_membership_not_found_on_reject() {
        UUID membershipId = UUID.randomUUID();

        when(repository.findById(membershipId)).thenReturn(Optional.empty());

        assertThrows(MembershipNotFoundException.class, () -> membershipService.rejectMembership(membershipId));
    }

    @Test
    void should_cancel_membership() {
        UUID membershipId = UUID.randomUUID();
        LocalDate today = LocalDate.now();

        Membership membership = new Membership();
        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));
        when(repository.save(any(Membership.class))).thenReturn(membership);

        membershipService.cancelMembership(membershipId);

        assertEquals(today, membership.getEndDate());
        assertEquals(MembershipStatus.CANCELLED, membership.getStatus());
    }

    @Test
    void should_throw_exception_when_membership_not_found_on_cancel() {
        UUID membershipId = UUID.randomUUID();

        when(repository.findById(membershipId)).thenReturn(Optional.empty());

        assertThrows(MembershipNotFoundException.class, () -> membershipService.cancelMembership(membershipId));
    }

    @Test
    void should_throw_exception_when_trial_already_used() {
        UUID membershipId = UUID.randomUUID();
        Membership membership = new Membership();
        UUID userId = UUID.randomUUID();

        AppUser existing = new AppUser();
        existing.setId(userId);
        membership.setUser(existing);

        membership.setDuration(MembershipDuration.TRIAL);
        membership.setStatus(MembershipStatus.APPROVED);

        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));
        when(repository.existsByUser_IdAndDurationAndStatusIn(userId, MembershipDuration.TRIAL,  List.of(MembershipStatus.APPROVED, MembershipStatus.CANCELLED) )).thenReturn(true);

        assertThrows(RestApiException.class, () -> membershipService.approveMembership(membershipId));
    }

    @Test
    void should_approve_membership() {
        UUID membershipId = UUID.randomUUID();
        Membership membership = new Membership();
        UUID userId = UUID.randomUUID();

        AppUser existing = new AppUser();
        existing.setId(userId);
        membership.setUser(existing);

        membership.setDuration(MembershipDuration.MONTHLY);
        when(repository.findById(membershipId)).thenReturn(Optional.of(membership));
        when(repository.save(any(Membership.class))).thenReturn(membership);

        membershipService.approveMembership(membershipId);

        assertEquals(MembershipStatus.APPROVED, membership.getStatus());
        assertNotNull(membership.getStartDate());
        assertNotNull(membership.getEndDate());
    }

    @Test
    void should_throw_exception_when_membership_not_found_on_approve() {
        UUID membershipId = UUID.randomUUID();

        when(repository.findById(membershipId)).thenReturn(Optional.empty());

        assertThrows(MembershipNotFoundException.class, () -> membershipService.approveMembership(membershipId));
    }

    @Test
    void should_return_memberships_for_user() {
        UUID userId = UUID.randomUUID();

        Membership membership = new Membership();
        var dto = createDto();

        when(repository.findByUser_Id(userId)).thenReturn(List.of(membership));
        when(membershipMapper.toDto(membership)).thenReturn(dto);

        var result = membershipService.getMembershipsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    private MembershipResponseDto createDto() {
        return new MembershipResponseDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Ira",
                "Ivanova",
                MembershipType.ADULT,
                MembershipDuration.MONTHLY,
                MembershipStatus.APPROVED,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                true
        );
    }
}