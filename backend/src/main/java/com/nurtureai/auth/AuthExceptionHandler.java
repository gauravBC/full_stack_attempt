package com.nurtureai.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse invalidCredentials() {
        return new ErrorResponse("Invalid username or password");
    }

    @ExceptionHandler(DuplicateAccountException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse duplicateAccount() {
        return new ErrorResponse("An account already exists for this username, phone number, or email");
    }

    record ErrorResponse(String message) {
    }
}
