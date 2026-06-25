package com.nurtureai.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PatchMapping("/password")
    AccountMessageResponse updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        accountService.updatePassword(request);
        return new AccountMessageResponse("Password updated for local testing. TODO: replace with Spring Security session flow.");
    }

    @GetMapping("/partners")
    List<PartnerContactResponse> partners(@RequestParam @NotBlank String username) {
        return accountService.getPartners(username);
    }

    @PutMapping("/partners")
    PartnerContactResponse savePartner(@Valid @RequestBody PartnerContactRequest request) {
        return accountService.savePartner(request);
    }

    public record PasswordUpdateRequest(
        @NotBlank String username,
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, max = 128) String newPassword
    ) {
    }

    public record PartnerContactRequest(
        @NotBlank String username,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String relationship,
        boolean notificationsEnabled
    ) {
    }

    public record PartnerContactResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String relationship,
        boolean notificationsEnabled
    ) {
    }

    public record AccountMessageResponse(String message) {
    }
}
