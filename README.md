# NurtureAI Phase 1

NurtureAI is a pregnancy nutrition companion built as a web app, mobile app, and Spring Boot API.

## Architecture

- Web: React.js Create React App
- Mobile: React Native with Expo
- Backend: Java 21, Spring Boot
- Data: PostgreSQL primary with read replica configuration placeholders
- Cache: Redis
- Async: Kafka
- AI: backend-only orchestration layer, currently mocked

## Local Development

Start infrastructure:

```bash
npm run db:up
```

Start backend:

```bash
npm run api:dev
```

Start web:

```bash
npm start
```

Start mobile:

```bash
cd mobile
npm install
npm start
```

## First API Endpoints

```text
POST /api/auth/signup
POST /api/auth/login
POST /api/auth/otp/request
GET  /api/daily-plans/today?userId=demo-user
POST /api/daily-plans/generate?userId=demo-user
POST /api/daily-plans/generate-now?userId=demo-user
GET  /api/pantry?userId=demo-user
```

## AI Integration

Local development uses the mock AI client by default. To run the backend with real OpenAI calls, export your key in the terminal and start the OpenAI script:

```bash
export OPENAI_API_KEY=your_api_key
npm run api:dev:openai
```

The dashboard shows an AI status badge after login. It should say `OpenAI live` when the backend is using the real provider.

The frontend never calls the AI provider directly. It calls `POST /api/daily-plans/generate-now`, and the backend handles prompts, safety instructions, API credentials, and JSON parsing.

Ollama provider for free local development:

```bash
ollama pull llama3.2
npm run api:dev:ollama
```

Mock page flow for UI checks:

```text
http://localhost:3000/?MockFlow=Y
```

## Next Build Steps

1. Replace mocked meal plan data with persisted PostgreSQL records.
2. Add auth and user onboarding.
3. Add Redis caching for today's plan.
4. Add Kafka consumers for AI generation, pantry vision, and notifications.
5. Persist AI-generated plans and cache them in Redis.

## Roadmap / TODOs

See [PHASE_1_TODOS.md](PHASE_1_TODOS.md) for the next implementation roadmap.
