package de.oleinikova.boxingclub.backend.user.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/admin/users")
public interface UserApi extends UserApiSwaggerDoc{

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserCreateResponseDto createUser(@Valid @RequestBody UserCreateDto userCreateDto);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    UserResponseDto getUserById(@PathVariable UUID userId);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email")
    UserResponseDto getUserByEmail(@RequestParam String email);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    List<UserResponseDto> getAllUsers();

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable UUID id);
}
