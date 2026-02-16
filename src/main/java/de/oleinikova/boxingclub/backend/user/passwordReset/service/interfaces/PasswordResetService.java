package de.oleinikova.boxingclub.backend.user.passwordReset.service.interfaces;

import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;

public interface PasswordResetService {

    void createPasswordResetToken(String email);

    boolean validatePasswordResetToken(String tokenValue);

    void resetPassword(String tokenValue, String newPassword);

}
