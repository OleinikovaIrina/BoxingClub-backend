package de.oleinikova.boxingclub.backend.membership.exception;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import org.springframework.http.HttpStatus;

public class MembershipNotFoundException extends RestApiException {
    public MembershipNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Membership not found");
    }
}
