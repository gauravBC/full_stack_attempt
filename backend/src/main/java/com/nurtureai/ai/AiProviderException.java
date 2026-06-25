package com.nurtureai.ai;

import org.springframework.http.HttpStatus;

public class AiProviderException extends RuntimeException {

    private final HttpStatus status;

    public AiProviderException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
