# 실시간 주문 처리 백엔드 (Kiosk)

Spring Boot 기반 키오스크 주문 API, JWT 인증, JPA, WebSocket(STOMP) 실시간 알림, Docker, AWS 배포 스크립트를 포함합니다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| 언어 / 런타임 | Java 17 |
| 프레임워크 | Spring Boot 3.3.5 |
| 보안 | Spring Security, JWT (jjwt 0.11.5) |
| 데이터 | Spring Data JPA, Hibernate |
| DB | MariaDB(로컬 권장) / MySQL 8(Docker·RDS) |
| 실시간 | WebSocket + STOMP (`/topic/orders`) |
| 빌드 | Gradle |

## 아키텍처

- **Controller**: HTTP 요청/응답, DTO만 사용
- **Service**: 비즈니스 로직, DTO ↔ Entity 변환
- **Repository**: JPA 인터페이스
- **Domain**: Entity, Enum
- **Config**: Security, JWT, WebSocket
- **Exception**: 공통 예외 (`GlobalExceptionHandler`)

```
com.project.kiosk
├── controller
├── service
├── repository
├── domain
├── dto (request / response)
├── config
└── exception
```

## API 목록

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/auth/register` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) | 불필요 |
| POST | `/api/auth/logout` | 로그아웃 (JWT 블랙리스트 등록) | Bearer JWT |
| POST | `/api/orders` | 주문 생성 | Bearer JWT |
| GET | `/api/orders` | 주문 전체 조회 | ADMIN |
| GET | `/api/orders/{id}` | 주문 단건 | 본인 또는 ADMIN |
| PATCH | `/api/orders/{id}/status` | 주문 상태 변경 | ADMIN |

에러 응답은 공통 형식: `{ "status", "message", "timestamp" }`.

## WebSocket (실시간 주문 알림)

- **STOMP 엔드포인트**: `/ws` (SockJS)
- **브로커 prefix**: `/topic`, `/app`
- **구독 채널**: `/topic/orders` — 새 주문 생성 시 서버가 `OrderResponse` JSON을 broadcast
- **테스트 페이지**: 앱 실행 후 브라우저에서 `http://localhost:8080/ws-test.html` 접속 → `/topic/orders` 구독 후 주문 API로 주문 생성 시 화면에 수신

## 로컬 실행

1. **DB**: `kiosk_db` 생성 (MariaDB 또는 MySQL 8).  
2. **`application.yml`**: `DB_PASSWORD`, 필요 시 `SPRING_DATASOURCE_URL` / `DB_DRIVER` 조정.  
3. **JWT**: `JWT_SECRET`은 32바이트 이상 권장.  
4. 실행:

```bash
./gradlew bootRun
# Windows
gradlew.bat bootRun
```

5. 헬스 확인: `http://localhost:8080` (또는 `ws-test.html`로 WebSocket 확인)

## JWT (Postman 예시)

1. `POST /api/auth/register` — Body(JSON): `username`, `password`, `role` (`USER` 또는 `ADMIN` 등)  
2. `POST /api/auth/login` — 응답의 `token` 복사  
3. 이후 요청 Header: `Authorization: Bearer <token>`

로그아웃은 `POST /api/auth/logout` 호출 시 현재 토큰을 Redis 블랙리스트에 저장하여 재사용을 막습니다.

## Docker Compose (앱 + MySQL 8 + Redis)

1. `.env.example`을 복사해 `.env` 생성 후 `DB_PASSWORD`, `JWT_SECRET` 설정  
2. 프로젝트 루트(`Dockerfile` 있는 위치)에서:

```bash
docker compose up --build
```

- 앱: `http://localhost:8080`  
- MySQL: 호스트 `localhost:3307` → 컨테이너 `db:3306` (로컬 3306과 겹치지 않게 매핑)
- Redis: 호스트 `localhost:6379` → 컨테이너 `redis:6379`

## AWS 배포 (EC2 + RDS 개요)

### 인프라

1. **EC2** (예: Amazon Linux 2, t2.micro): 보안 그룹 인바운드 **22**(SSH), **8080**(HTTP API)  
2. **RDS** (MySQL 8): 보안 그룹에서 **EC2 보안 그룹**만 DB 포트(3306) 허용  
3. EC2에 **Java 17** 설치 (예: `sudo yum install -y java-17-amazon-corretto-headless`)

### EC2 환경변수 (prod)

`application-prod.yml`은 다음을 사용합니다.

| 변수 | 설명 |
|------|------|
| `RDS_HOSTNAME` | RDS 엔드포인트 호스트 |
| `RDS_PORT` | 기본 3306 |
| `RDS_DB_NAME` | 기본 `kiosk_db` |
| `RDS_USERNAME` | DB 사용자 |
| `RDS_PASSWORD` | DB 비밀번호 |
| `JWT_SECRET` | JWT 서명용 비밀 (충분한 길이) |
| `JWT_EXPIRATION_MS` | 선택, 기본 86400000 |

예: `~/.bashrc` 또는 systemd `EnvironmentFile`에 export.

### 배포 스크립트

```bash
chmod +x deploy.sh
./deploy.sh
```

- `git pull` 후 `./gradlew bootJar -x test`, 기존 jar 프로세스 종료, `nohup java -jar ... --spring.profiles.active=prod`  
- 로그: `logs/app.log`

**메모리 부족**으로 EC2에서 빌드가 실패하면 로컬에서 `bootJar` 후 `build/libs/*.jar`만 `scp`로 올려 실행해도 됩니다.

## GitHub Actions CI/CD

`main` 브랜치 push 시 아래 순서로 자동 실행됩니다.

1. Gradle 테스트/빌드
2. Docker 이미지 빌드 및 Docker Hub push
3. EC2 SSH 접속 후 `docker compose pull && docker compose up -d`

워크플로우 파일: `.github/workflows/deploy.yml`

### GitHub Secrets

아래 시크릿을 저장소 Settings > Secrets and variables > Actions 에 등록해야 합니다.

- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`
- `EC2_HOST`
- `EC2_USERNAME`
- `EC2_KEY` (SSH 개인키 전체)

### 동작 확인

- `http://<EC2-퍼블릭-IP>:8080/api/auth/login` 등 API 응답 확인  
- RDS 연결 실패 시: RDS 보안 그룹, 서브넷, 자격 증명, `RDS_HOSTNAME` 재확인

## 폴더 구조 (요약)

```
kiosk/
├── Dockerfile
├── docker-compose.yml
├── deploy.sh
├── .env.example
├── build.gradle
├── src/main/java/com/project/kiosk/...
└── src/main/resources/
    ├── application.yml
    ├── application-prod.yml
    └── static/ws-test.html
```

## 라이선스

학습·포트폴리오 용도.
