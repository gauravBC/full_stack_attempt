package com.nurtureai.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AiExceptionHandler {

    @ExceptionHandler(AiProviderException.class)
    ResponseEntity<AiErrorResponse> aiProviderError(AiProviderException exception) {
        return ResponseEntity
            .status(exception.status())
            .body(new AiErrorResponse(exception.getMessage()));
    }

    record AiErrorResponse(String message) {
    }
}
