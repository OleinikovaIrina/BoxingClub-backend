package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserService;
import de.oleinikova.boxingclub.backend.user.util.AppUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final AppUserRepository repo;
    private final AppUserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    private String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }


    @Override
    @Transactional
    public UserCreateResponseDto createUser(UserCreateDto userCreateDto) {
        String email = normalizeEmail(userCreateDto.email());

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }
        AppUser user = mapper.toEntity(userCreateDto);
        user.setFirstName(userCreateDto.firstName().trim());
        user.setLastName(userCreateDto.lastName().trim());
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);
        user.setConfirmationStatus(ConfirmationStatus.UNCONFIRMED);
        user.setPassword(passwordEncoder.encode(userCreateDto.password()));

        AppUser saved = repo.save(user);
        return mapper.toCreateResponseDto(saved);
    }

    @Override
    public UserResponseDto getUserById(UUID userId) {
        AppUser user = repo.findById(userId).orElseThrow(UserNotFoundException::new);
        return mapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        email = normalizeEmail(email);
        AppUser user = repo.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
        return mapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return repo.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException();
        }
        repo.deleteById(id);
    }
}
