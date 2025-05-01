package com.tongji.codejourneycolab.codejourneycolabbackend.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
