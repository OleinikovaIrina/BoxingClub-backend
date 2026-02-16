package de.oleinikova.boxingclub.backend.user.passwordReset.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.passwordReset.dto.request.PasswordResetRequest;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/password")
public interface PasswordResetApi extends PasswordResetApiSwaggerDoc{

    @Override
    @PostMapping("/request")
    ResponseEntity<Void> createPasswordResetToken(@RequestParam("email") String email);

    @Override
    @GetMapping("/validate")
    ResponseEntity<Boolean> validatePasswordResetToken(@RequestParam("token") String tokenValue);

    @Override
    @PostMapping("/reset")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request);
}
