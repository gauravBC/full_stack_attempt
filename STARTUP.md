# NurtureAI Startup Guide

Use this guide to start the local infrastructure, Spring Boot backend, React web app, and React Native mobile app.

## Project Folder

```bash
cd /Users/gaurav/Desktop/NurtureAI/NurtureAI_V1
```

## 1. Start Infrastructure

Run this first from the project root:

```bash
npm run db:up
```

This starts:

- PostgreSQL
- Redis
- Kafka
- Zookeeper

## 2. Start Backend

Open a second terminal and run:

```bash
npm run api:dev
```

The backend runs at:

```text
http://localhost:8080
```

Useful backend endpoints:

```text
GET  http://localhost:8080/api/daily-plans/today?userId=demo-user
POST http://localhost:8080/api/daily-plans/generate-now?userId=demo-user
GET  http://localhost:8080/api/pantry?userId=demo-user
```

## 3. Start Web Frontend

Open a third terminal and run:

```bash
npm start
```

The web app runs at:

```text
http://localhost:3000
```

For now, login and signup are UI-only. Click `Continue` to enter the app.

## 4. Start Mobile App

Open another terminal and run:

```bash
cd mobile
npm install
npm start
```

Then use:

- Expo Go on your phone
- iOS Simulator
- Android Emulator
- Expo web preview

## AI Modes

Mock AI is the default and does not require an API key:

```bash
AI_PROVIDER=mock npm run api:dev
```

To use OpenAI from the backend:

```bash
export OPENAI_API_KEY=your_api_key
npm run api:dev:openai
```

After login, the dashboard AI badge should say `OpenAI live`. If `AI_PROVIDER=openai` is set without `OPENAI_API_KEY`, the backend now fails loudly instead of falling back to mock mode.

The frontend and mobile app should never call OpenAI directly. They call the Spring Boot backend, and the backend handles the AI provider.

## Ollama local AI

Ollama is a free local AI option for development. Install Ollama, then run:

```bash
ollama pull llama3.2
npm run api:dev:ollama
```

Use this URL to test the page without calling any AI provider:

```text
http://localhost:3000/?MockFlow=Y
```

## Recommended Startup Order

1. `npm run db:up`
2. `npm run api:dev`
3. `npm start`
4. `cd mobile && npm start`

## Troubleshooting

If the web app says the plan cannot be generated:

- Confirm the backend is running on `http://localhost:8080`.
- Confirm the web app is running on `http://localhost:3000`.
- Confirm `REACT_APP_API_BASE_URL` points to `http://localhost:8080/api`.

If Docker services are not running:

```bash
docker compose ps
```

If mobile dependencies are missing:

```bash
cd mobile
npm install
```
