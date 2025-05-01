package com.tongji.codejourneycolab.codejourneycolabbackend.exception;

public class IdentityAlreadyExistsException extends RuntimeException {
    public IdentityAlreadyExistsException(String message) {
        super(message);
    }
}
