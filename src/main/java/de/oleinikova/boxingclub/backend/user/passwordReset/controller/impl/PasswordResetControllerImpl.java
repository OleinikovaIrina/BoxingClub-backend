package de.oleinikova.boxingclub.backend.user.passwordReset.controller.impl;

import de.oleinikova.boxingclub.backend.user.passwordReset.controller.interfaces.PasswordResetApi;
import de.oleinikova.boxingclub.backend.user.passwordReset.dto.request.PasswordResetRequest;
import de.oleinikova.boxingclub.backend.user.passwordReset.service.interfaces.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetControllerImpl implements PasswordResetApi {

    private final PasswordResetService passwordResetService;


    @Override
    public ResponseEntity<Void> createPasswordResetToken(String email) {
       passwordResetService.createPasswordResetToken(email);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Boolean> validatePasswordResetToken(String tokenValue) {
       boolean valid= passwordResetService.validatePasswordResetToken(tokenValue);
        return ResponseEntity.ok(valid);
    }

    @Override
    public ResponseEntity<Void> resetPassword(  PasswordResetRequest request) {
        passwordResetService.resetPassword(request.passwordResetToken(),request.password());
        return ResponseEntity.ok().build();
    }
}
