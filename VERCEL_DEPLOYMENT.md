# Vercel Deployment - NurtureAI Web

Vercel should host the React web app only. The Spring Boot backend must be hosted separately and exposed with a public HTTPS URL.

## Required Vercel Setting

Add this environment variable in Vercel Project Settings > Environment Variables:

```text
REACT_APP_API_BASE_URL=https://YOUR_BACKEND_PUBLIC_URL/api
```

Examples:

```text
REACT_APP_API_BASE_URL=https://nurtureai-api.onrender.com/api
REACT_APP_API_BASE_URL=https://nurtureai-api.koyeb.app/api
```

## Vercel Build Settings

These are also captured in `vercel.json`:

```text
Framework Preset: Create React App
Build Command: npm run build
Output Directory: build
Install Command: npm install
Root Directory: ./
```

## Deploy From GitHub

1. Push this project to GitHub.
2. Go to https://vercel.com/new.
3. Import the GitHub repository.
4. Keep the root directory as `./`.
5. Add `REACT_APP_API_BASE_URL`.
6. Deploy.

## Mock UI Testing

Use this URL to test layout/navigation without consuming AI/backend calls:

```text
https://YOUR_VERCEL_SITE.vercel.app/?MockFlow=Y
```

## Important

Do not add backend secrets to Vercel for this React app. AI keys, database credentials, and provider keys belong only on the backend host.
