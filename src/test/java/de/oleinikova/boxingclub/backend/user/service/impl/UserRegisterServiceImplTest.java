package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.user.confirmation.interfaces.ConfirmationService;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserRegisterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceImplTest {

    @Mock
    private AppUserRepository userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationService confirmationService;

    @InjectMocks
    private UserRegisterServiceImpl userRegisterService;

    @Test
    void register_existingUnconfirmed_resendsCode_andDoesNotSaveNewUser() {
        AppUser existing = new AppUser();
        existing.setId(UUID.randomUUID());
        existing.setEmail("user@example.com");
        existing.setRole(Role.ROLE_USER);
        existing.setConfirmationStatus(ConfirmationStatus.UNCONFIRMED);

        when(userRepo.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(existing));

        var resp = userRegisterService.register(new UserCreateDto("Dick", "Black", "user@example.com", "Pass123!"));

        verify(confirmationService).regenerateCode(existing);
        verify(userRepo, never()).save(any());
        verify(passwordEncoder, never()).encode(any());

        assertEquals("user@example.com", resp.email());
        assertEquals(Role.ROLE_USER, resp.role());
        assertTrue(resp.confirmationResent());

        verifyNoMoreInteractions(confirmationService, userRepo, passwordEncoder);
    }

    @Test
    void register_newUser_saves_encodesPassword_generatesCode() {
        when(userRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Secret#1")).thenReturn("ENC(Secret#1)");
        when(userRepo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCreateResponseDto responseDto = userRegisterService.register(
                new UserCreateDto(" Jane ", "  Roe ", "  TeSt@Example.com ", "Secret#1"));

        verify(userRepo).save(argThat(u ->
                u.getFirstName().equals("Jane") &&
                        u.getLastName().equals("Roe") &&
                        u.getEmail().equals("test@example.com") &&
                        u.getPassword().equals("ENC(Secret#1)") &&
                        u.getRole() == Role.ROLE_USER &&
                        u.getConfirmationStatus() == ConfirmationStatus.UNCONFIRMED &&
                        u.isEnabled()
        ));
        verify(passwordEncoder).encode("Secret#1");
        verify(confirmationService).generateConfirmationCode(any(AppUser.class));
        verifyNoMoreInteractions(confirmationService);

        assertEquals("test@example.com", responseDto.email());
        assertEquals(Role.ROLE_USER, responseDto.role());
        assertFalse(responseDto.confirmationResent());
    }


    @Test
    void confirmRegistration() {


    }
}