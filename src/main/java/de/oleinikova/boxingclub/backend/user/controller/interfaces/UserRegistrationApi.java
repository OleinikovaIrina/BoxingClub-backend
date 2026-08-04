package de.oleinikova.boxingclub.backend.user.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
public interface UserRegistrationApi extends UserRegistrationApiSwaggerDoc {
    @Override
    @PermitAll
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserCreateResponseDto register(@Valid @RequestBody UserCreateDto userCreateDto);

    @Override
    @PermitAll
    @GetMapping("/confirm")
    ResponseEntity<Void> confirmRegistration(@RequestParam String code);
}
