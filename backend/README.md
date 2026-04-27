# Enterprise Web Backend (Portal)

Spring Boot backend for a general enterprise web service with authentication, order management APIs, websocket notifications, and AWS deployment support.

## Tech Stack

- Java 17, Spring Boot 3.3.5
- Spring Security + JWT
- Spring Data JPA
- MariaDB/MySQL + Redis
- WebSocket (STOMP)
- Gradle + Docker

## Local Run

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

## Docker Compose

1. Copy `.env.example` to `.env`
2. Fill `DOCKER_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`
3. Run:

```bash
docker compose pull
docker compose up -d
```

## CI/CD

- Workflow: `/.github/workflows/deploy.yml`
- Trigger: push to `main`
- Steps: test -> build image -> push Docker Hub -> deploy EC2

Required GitHub Secrets:

- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`
- `EC2_HOST`
- `EC2_USERNAME`
- `EC2_KEY`