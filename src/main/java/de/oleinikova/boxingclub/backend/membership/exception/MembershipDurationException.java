package de.oleinikova.boxingclub.backend.membership.exception;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import org.springframework.http.HttpStatus;

public class MembershipDurationException extends RestApiException {
    public MembershipDurationException(String duration) {
        super(HttpStatus.BAD_REQUEST,"Unsupported membership duration: " + duration);
    }
}
