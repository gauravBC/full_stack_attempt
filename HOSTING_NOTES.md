# Hosting Notes

## Free Website Hosting Options

Good free options for hosting the React website:

- Vercel
- Netlify
- Cloudflare Pages
- Render Static Sites
- GitHub Pages

## Recommendation

For NurtureAI, use:

```text
React website: Vercel or Netlify
```

If we want the most generous static hosting limits, use:

```text
Cloudflare Pages
```

## Future Full-Stack Hosting

For a fuller demo or production-style setup:

```text
Frontend: Vercel / Netlify / Cloudflare Pages
Backend: Render / Railway / Fly.io / AWS
Database: Neon / Supabase / Render Postgres / Railway Postgres
```

## Early Demo Recommendation

```text
React website: Vercel
Spring Boot backend: Render
Postgres: Neon or Supabase
Redis/Kafka: skip for early demo, add managed services later
```

## Important

The website can be hosted free, but the Spring Boot backend, Postgres, Redis, and Kafka usually need separate backend or managed-service hosting.
