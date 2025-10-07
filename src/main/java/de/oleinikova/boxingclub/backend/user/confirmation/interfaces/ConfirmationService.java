package de.oleinikova.boxingclub.backend.user.confirmation.interfaces;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;

public interface ConfirmationService {

    String generateConfirmationCode(AppUser appUser);

    String regenerateCode(AppUser appUser);

    AppUser confirmAndConsume(String code);
}
