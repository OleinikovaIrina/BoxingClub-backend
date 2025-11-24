package de.oleinikova.boxingclub.backend.user.passwordReset.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    ResponseEntity<Void> resetPassword(@RequestParam("token") String tokenValue,@RequestParam("newPassword") String newPassword);
}
