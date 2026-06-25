# NurtureAI Security Plan

This app handles sensitive pregnancy and family-health-adjacent data. Treat all personally identifiable information as private by default.

## Current Status

- Auth screens are present.
- Backend auth endpoints are placeholders.
- Password and OTP logic are TODOs and must not be used for production yet.

## Production Requirements

- Use HTTPS everywhere.
- Store passwords only as slow salted hashes using Argon2id or bcrypt.
- Never log passwords, OTPs, access tokens, AI prompts containing personal data, or full medical notes.
- Use short-lived access tokens plus refresh-token rotation, or secure server-side sessions.
- Add rate limits for login, signup, OTP, AI generation, and password reset.
- Authorize every request by authenticated user ownership. Never trust `userId` from the client.
- Encrypt sensitive data at rest using managed database encryption and field-level encryption for the most sensitive fields.
- Keep secrets in a secrets manager, not `.env` files in production.
- Use audit logs for login, failed login, profile export, deletion, and sensitive profile changes.
- Add consent, account deletion, data export, and retention policies.
- Restrict AI prompts to the minimum data needed and redact unnecessary PII.
- Run dependency scanning, SAST, container scanning, and OWASP ZAP before releases.

## OTP TODO

- Pick SMS provider.
- Generate random numeric OTP server-side.
- Hash OTP before storage.
- Expire OTP after a short window.
- Limit retry attempts per phone number and IP.
- Invalidate OTP after successful login.
