package com.nurtureai.auth;

import com.nurtureai.auth.dto.LoginRequest;
import com.nurtureai.auth.dto.SignUpRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return new AuthResponse(
            UUID.randomUUID().toString(),
            request.username(),
            "TODO: Hash password with Argon2/bcrypt and persist user.",
            null
        );
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedUser user = authService.login(request);

        return new AuthResponse(
            UUID.randomUUID().toString(),
            user.username(),
            "Login successful. TODO: issue secure session/JWT.",
            user
        );
    }

    @PostMapping("/otp/request")
    AuthResponse requestOtp() {
        return new AuthResponse(
            UUID.randomUUID().toString(),
            "phone-login",
            "TODO: Integrate SMS OTP provider, expiry, retry limits, and abuse protection.",
            null
        );
    }

    record AuthResponse(String requestId, String subject, String todo, AuthenticatedUser user) {
    }
}
