package de.oleinikova.boxingclub.backend.membership.service.impl;

import de.oleinikova.boxingclub.backend.membership.entity.Membership;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus;
import de.oleinikova.boxingclub.backend.membership.exception.MembershipDurationException;
import de.oleinikova.boxingclub.backend.membership.exception.MembershipNotFoundException;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.membership.service.interfaces.MembershipService;
import de.oleinikova.boxingclub.backend.membership.dto.request.MembershipCreateRequestDto;
import de.oleinikova.boxingclub.backend.membership.dto.response.MembershipResponseDto;
import de.oleinikova.boxingclub.backend.membership.persistence.MembershipRepository;
import de.oleinikova.boxingclub.backend.membership.util.MembershipMapper;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository repository;
    private final AppUserRepository appUserRepository;
    private final MembershipMapper membershipMapper;

    @Transactional
    @Override
    public MembershipResponseDto createMembership(UUID userId, MembershipCreateRequestDto dto) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate;
        switch (dto.duration()) {
            case TRIAL -> endDate = startDate.plusDays(7);
            case MONTHLY -> endDate = startDate.plusMonths(1);
            case YEARLY -> endDate = startDate.plusYears(1);
            default -> throw new MembershipDurationException(dto.duration().name());
        }

        Membership entity = membershipMapper.toEntity(dto);

        if (dto.iban() != null && !dto.iban().isBlank()){
            entity.setIban(dto.iban().replaceAll("\\s+", "").toUpperCase());
        }

        entity.setUser(user);
        entity.setStatus(MembershipStatus.PENDING);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setActive(false);


        Membership saved = repository.save(entity);
        return membershipMapper.toDto(saved);
    }

    @Transactional
    public MembershipResponseDto approveMembership(UUID membershipId) {
        Membership m = repository.findById(membershipId)
                .orElseThrow(MembershipNotFoundException::new);

        if (m.getDuration() == MembershipDuration.TRIAL) {
            boolean everHadApprovedTrial = repository.existsByUser_IdAndDurationAndStatus(
                    m.getUser().getId(),
                    MembershipDuration.TRIAL,
                    MembershipStatus.APPROVED
            );
            if (everHadApprovedTrial) {
                throw new IllegalStateException("User already used a TRIAL once.");
            }
        }

        m.setStatus(MembershipStatus.APPROVED);
        m.setActive(true);

        m = repository.save(m);
        return membershipMapper.toDto(m);
    }

    @Transactional
    public MembershipResponseDto rejectMembership(UUID membershipId) {
        Membership m = repository.findById(membershipId)
                .orElseThrow(MembershipNotFoundException::new);

        m.setStatus(MembershipStatus.REJECTED);
        m.setActive(false);

        m = repository.save(m);
        return membershipMapper.toDto(m);
    }

    @Override
    public List<MembershipResponseDto> getMembershipsByUserId(UUID userId) {

        return repository.findByUser_Id(userId)
                .stream()
                .map(membershipMapper::toDto)
                .toList();

    }

    @Override
    public List<MembershipResponseDto> getMembershipByLastName(String lastName) {

        return repository.findAllByUser_LastNameIgnoreCase(lastName)
                .stream()
                .map(membershipMapper::toDto)
                .toList();

    }

    @Transactional
    @Override
    public MembershipResponseDto cancelMembership(UUID membershipId) {
        Membership m = repository.findById(membershipId)
                .orElseThrow(MembershipNotFoundException::new);

        LocalDate today = LocalDate.now();

        if (m.getStatus() == MembershipStatus.CANCELLED) {
            boolean changed = false;
            if (m.isActive()) {
                m.setActive(false);
                changed = true;
            }
            if (m.getEndDate() == null || m.getEndDate().isAfter(today)) {
                m.setEndDate(today);
                changed = true;
            }
            if (changed) m = repository.save(m);
            return membershipMapper.toDto(m);
        }

        m.setStatus(MembershipStatus.CANCELLED);
        m.setActive(false);
        if (m.getEndDate() == null || m.getEndDate().isAfter(today)) {
            m.setEndDate(today);
        }

        m = repository.save(m);
        return membershipMapper.toDto(m);
    }

}
