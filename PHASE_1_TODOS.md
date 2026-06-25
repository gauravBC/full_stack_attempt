# NurtureAI Phase 1 TODOs

These are the next major TODOs for the NurtureAI app after the current Phase 1 prototype.

## 1. Integrate JWT Security

- Replace the temporary local login flow with Spring Security.
- Hash passwords with bcrypt or Argon2 instead of temporary SHA-256 logic.
- Issue JWT access tokens after login.
- Add refresh-token flow or secure session renewal.
- Protect backend APIs so user data cannot be accessed without a valid token.
- Add user ownership checks on profile, meal plan, pantry, grocery, partner, and account APIs.
- Add logout/token invalidation strategy.
- Add rate limits for login and AI endpoints.

## 2. Add Features/Screens Suggested by Vikram

- Capture Vikram's complete screen/feature list.
- Prioritize the screens into Phase 1.1 backlog.
- Implement one screen at a time with backend persistence.
- Keep layout mobile-responsive and consistent with the current dashboard/account/profile navigation.
- Add empty, loading, success, and error states for every new screen.

## 3. Pregnancy Dietary Intelligence

- Build a pregnancy nutrition knowledge base covering:
  - Pregnancy week/trimester needs.
  - Vegetarian, egg, and non-vegetarian preferences.
  - Allergies and avoided foods.
  - Indian/regional cuisine patterns.
  - Pantry-aware meal planning.
  - Hydration and supplement timing.
  - Safety constraints such as raw/unpasteurized foods.
- Start with RAG/knowledge-base grounding plus strong prompts and guardrails.
- Add clinician-safety disclaimers and escalation guidance.
- Treat model fine-tuning/training as a later step after collecting high-quality examples and evaluation data.
- Create AI evaluation cases for common pregnancy nutrition scenarios.

## 4. Host Model and Backend

- Host Spring Boot backend on a public server such as Render, Koyeb, Railway, Fly.io, AWS, GCP, or Azure.
- Host Postgres on Neon, Supabase, Render Postgres, AWS RDS, or another managed provider.
- For public AI, prefer a hosted model provider such as Groq, Gemini, OpenAI, or a managed inference endpoint.
- Avoid Ollama for public hosting unless running a dedicated VM/server with enough RAM/CPU/GPU.
- Keep all AI keys, DB credentials, JWT secrets, and provider secrets only on the backend host.

## 5. Connect Hosted Backend to Vercel UI

- Set the Vercel environment variable:

```text
REACT_APP_API_BASE_URL=https://your-backend-url/api
```

- Configure backend CORS to allow:

```text
https://nurtureai07.vercel.app
```

- Redeploy Vercel after changing environment variables.
- Test these public endpoints from the browser/network tab:
  - `POST /api/auth/login`
  - `GET /api/ai/status`
  - `GET /api/diet-profile`
  - `POST /api/daily-plans/generate-now`
- Keep `?MockFlow=Y` available for UI-only testing without backend calls.

## 6. Future-Ready Scale

- Add production-grade JWT auth and authorization checks.
- Add API rate limiting and abuse protection.
- Use Redis for caching common reads and AI/session state where appropriate.
- Use Kafka or a managed queue for async AI generation, notifications, reminders, and pantry processing.
- Add Postgres connection pooling.
- Add Postgres read replicas when read traffic grows.
- Add indexes for high-traffic queries.
- Make AI generation async for slow provider calls.
- Add AI request budget controls per user/account.
- Add centralized logs, metrics, traces, and alerts.
- Add health checks and deployment readiness checks.
- Add backup and restore policy for Postgres.
- Add CDN/static caching for frontend assets.
- Horizontally scale backend instances behind a load balancer.

## Notes

- Local Ollama is working for local AI testing.
- Vercel currently hosts the frontend only.
- Public login requires a public backend and public Postgres database.
- The pasted OpenAI key should be revoked/rotated before any production hosting.
