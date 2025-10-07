package de.oleinikova.boxingclub.backend.exception;

/** 404: доменный ресурс не найден (user/membership/etc.). */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found: " + id);
    }
}
