package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import de.oleinikova.boxingclub.backend.user.util.AppUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private AppUserRepository repo;
    @Mock
    private AppUserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void shouldCreateUserSuccessfully() {

        String email = " TEST@mail.com";
        String normalizedEmail = "test@mail.com";
        String password = "Box_club2025!";
        String encodedPassword = "encoded-pass";

        UserCreateDto userCreateDto = new UserCreateDto("Dick", "Black", email, password);

        AppUser mappedUser = new AppUser();
        mappedUser.setEmail(normalizedEmail);

        AppUser savedUser = new AppUser();
        savedUser.setEmail(normalizedEmail);
        savedUser.setId(UUID.randomUUID());

        var responseDto = new UserCreateResponseDto(savedUser.getId(),
                normalizedEmail,
                Role.ROLE_USER,
                false);

        when(repo.existsByEmailIgnoreCase(normalizedEmail))
                .thenReturn(false);
        when(mapper.toEntity(userCreateDto)).thenReturn(mappedUser);
        when(passwordEncoder.encode(password))
                .thenReturn(encodedPassword);
        when(repo.save(mappedUser))
                .thenReturn(savedUser);
        when(mapper.toCreateResponseDto(savedUser))
                .thenReturn(responseDto);

        var result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(normalizedEmail,result.email());

        verify(repo).existsByEmailIgnoreCase(normalizedEmail);
        verify(passwordEncoder).encode(password);
        verify(mapper).toEntity(userCreateDto);
    }
    }
