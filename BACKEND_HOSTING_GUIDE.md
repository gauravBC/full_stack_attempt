# NurtureAI Backend Hosting Guide

This guide connects the hosted Vercel UI to a real Spring Boot backend, hosted Postgres, and an AI provider.

## Target Phase 1 Setup

```text
Vercel React UI -> Hosted Spring Boot API -> Hosted Postgres
                                      -> Hosted AI provider
```

Kafka is intentionally left out of hosted Phase 1 until event-driven notifications are active.

## AI Provider Recommendation

For hosted testing, use `AI_PROVIDER=groq` first. Groq is OpenAI-compatible, easy to wire from Spring Boot, and currently offers a free developer tier with rate limits. This is different from xAI Grok.

Other options:

- `AI_PROVIDER=openai`: best quality and reliability, but requires paid OpenAI API billing/credits.
- `AI_PROVIDER=ollama`: great locally, but needs your own hosted VM for production testing.
- `AI_PROVIDER=mock`: safe fallback with no external AI calls.

## Backend Environment Variables

Set these on Railway or Render:

```text
PORT=8080                         # Usually injected by the host. App also supports SERVER_PORT.
JDBC_DATABASE_URL=jdbc:postgresql://HOST:PORT/DB?sslmode=require
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
CORS_ALLOWED_ORIGINS=https://nurtureai07.vercel.app,http://localhost:3000,http://127.0.0.1:3000
AI_PROVIDER=groq
GROQ_API_KEY=...
GROQ_MODEL=llama-3.1-8b-instant
```

If using OpenAI instead:

```text
AI_PROVIDER=openai
OPENAI_API_KEY=...
OPENAI_MODEL=gpt-5.2
```

## Vercel Environment Variable

In Vercel, set:

```text
REACT_APP_API_BASE_URL=https://YOUR_BACKEND_DOMAIN/api
```

Then redeploy the Vercel frontend. Without redeploying, Create React App will not pick up the new env var.

## Railway Deployment

1. Create a Railway project.
2. Add a PostgreSQL database.
3. Add a new service from GitHub for this repo.
4. Set the service root directory to `backend` if Railway asks.
5. Use the Dockerfile in `backend/Dockerfile`.
6. Add the backend environment variables listed above.
7. Deploy.
8. Open:

```text
https://YOUR_RAILWAY_BACKEND/api/health
```

Expected response:

```json
{"status":"ok","database":"ok"}
```

## Render Deployment

Option A: Blueprint

1. Push `render.yaml` to GitHub.
2. In Render, create a Blueprint from this repo.
3. Add a Render Postgres database.
4. Fill the secret env vars in the web service.
5. Deploy.

Option B: Manual web service

1. New Web Service -> connect GitHub repo.
2. Environment: Docker.
3. Root directory: `backend`.
4. Health check path: `/api/health`.
5. Add backend environment variables.
6. Deploy.

## Connect Hosted UI to Backend

After backend health is green:

1. Copy backend URL, for example:

```text
https://nurtureai-backend.onrender.com
```

2. In Vercel project settings, set:

```text
REACT_APP_API_BASE_URL=https://nurtureai-backend.onrender.com/api
```

3. Redeploy Vercel.
4. Test normal URL, not MockFlow:

```text
https://nurtureai07.vercel.app
```

## Smoke Tests

```bash
curl https://YOUR_BACKEND/api/health
curl https://YOUR_BACKEND/api/ai/status
```

Expected AI status for Groq:

```json
{"provider":"groq","model":"llama-3.1-8b-instant","realAiEnabled":true}
```

## Important Security Notes

- Never put AI keys in React/Vercel frontend code.
- Store AI keys only in backend environment variables.
- Rotate any key that was pasted into chat, logs, screenshots, or Git history.
- Add JWT before public beta so usernames cannot be spoofed in API requests.
