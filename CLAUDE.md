# Sagopalgo Backend — 프로젝트 컨텍스트

## 기술 스택
- Spring Boot 3.5.3 + Java 17
- MySQL 8.x + Spring Data JPA + QueryDSL 5.0.0
- Redis + Redisson 3.27.0 (캐싱 + 분산 락)
- Spring Kafka (비동기 알림)
- Spring Security (OAuth2 Google/Kakao/Naver + JWT)
- WebSocket (실시간 알림)
- SpringDoc OpenAPI 2.8.15 (Swagger)
- CoolSMS SDK 4.3.0 (SMS 인증)
- Gradle

## 주요 설정
- 백엔드 포트: 8080
- Hibernate DDL: `${DDL_AUTO:update}`
- JWT: Access Token 30분, Refresh Token 14일 (Redis 블랙리스트)
- Kafka: `${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}`
- Redis: `${REDIS_HOST:localhost}:6379`
- Multipart: max-file-size 10MB, max-request-size 50MB
- Actuator: health, info, metrics, prometheus

## GitHub
- 저장소: `handokei/sagopalgo`
- 이슈/PR 대상 브랜치: `dev` (main은 운영 준비 전 절대 병합 금지)

---

## 패키지 구조

```
org.example
├── global                    # 공통/인프라 계층
│   ├── config                # 설정 빈 (Security, JPA, QueryDSL, Redis, Redisson, Kafka, WebSocket, Async)
│   │   └── entity/BaseEntity # createdAt, updatedAt (JPA Auditing)
│   ├── security              # 인증/인가
│   │   ├── jwt               # JWT 토큰 처리
│   │   ├── auth              # Spring Security 통합
│   │   └── oauth2            # OAuth2 (Google, Kakao, Naver)
│   ├── response              # ApiResponse + GlobalExceptionHandler
│   ├── lock                  # 분산 락 (Redisson AOP)
│   ├── file                  # 파일 저장 서비스
│   └── constants             # KafkaTopics enum
│
└── domain                    # 도메인 모듈 (10개 바운디드 컨텍스트)
    ├── user                  # 사용자 관리
    ├── product               # 상품 카탈로그 (캐싱 + 재고 관리)
    ├── order                 # 주문 처리 (Facade 패턴)
    ├── payment               # 결제 연동 (PortOne)
    ├── delivery              # 배송 관리
    ├── cart                  # 장바구니 (회원/비회원 이중 모드)
    ├── notification          # 비동기 알림 (Kafka 기반)
    ├── review                # 상품 리뷰
    ├── like                  # 상품 찜
    ├── viewhistory           # 최근 본 상품
    ├── category              # 카테고리
    └── sms                   # SMS 인증
```

**domain 하위 패키지 구조**
```
domain/{도메인명}
├── controller
│   └── dto
├── service
│   └── (facade)             # 복합 도메인 오케스트레이션
├── domain
│   ├── model
│   └── repository
├── (client)                 # 외부 API 클라이언트 (payment)
├── (consumer)               # Kafka 컨슈머 (notification)
├── (event)                  # 이벤트 리스너 (notification)
├── (support)                # 보조 클래스 (cart)
└── exception
```

---

## Git 컨벤션

### 브랜치 전략

| 브랜치 | 용도 |
|--------|------|
| `main` | 배포 브랜치 |
| `dev` | 통합 개발 브랜치 |
| `feat/기능명` | 기능 작업 브랜치 |

- 브랜치 생성 전 반드시 GitHub Issue 먼저 생성
- **이슈 생성 시 브랜치 prefix에 맞는 라벨을 반드시 설정**:

  | 브랜치 prefix | GitHub 라벨 |
  |---------------|-------------|
  | `feat/` | `feat` |
  | `fix/` | `fix` |
  | `refactor/` | `refactor` |
  | `chore/` | `chore` |
  | `docs/` | `documents` |
  | `test/` | `chore` |
  | `comment/` | `chore` |
  | `rename/` | `refactor` |
  | `remove/` | `chore` |
  | `style/` | `chore` |

- 브랜치 명명:

  | 브랜치 prefix | 용도 |
  |---------------|------|
  | `feat/기능명` | 새로운 기능 추가 |
  | `fix/버그명` | 버그 수정 |
  | `refactor/작업명` | 리팩토링 (기능 변경 X) |
  | `test/테스트명` | 테스트 코드 추가/수정 |
  | `chore/작업명` | 빌드 설정 변경 |
  | `docs/문서명` | 문서 수정 |
  | `comment/작업명` | 주석 추가 및 변경 |
  | `rename/작업명` | 파일 혹은 폴더명 수정 |
  | `remove/작업명` | 파일 삭제 |
  | `style/작업명` | 코드 포맷팅, 스타일 변경 (논리 변경 X) |

- 작업 흐름: feature 브랜치 → PR → `dev`
- **PR 전 필수 순서**: `./gradlew test` → `./gradlew build` → PR 생성
- 테스트 커버리지 100% 유지

### PR 생성 전 Test Plan 사전 공유 (필수)

**PR을 생성하기 전에 반드시 사용자에게 Test Plan 항목을 먼저 공유할 것.**
사용자가 로컬에서 직접 확인한 후 PR을 생성한다.

Test Plan 공유 형식:
```
PR 전 직접 확인해주세요:
- [ ] 항목 1
- [ ] 항목 2
```

### 커밋 타입

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 (기능 변경 X) |
| `test` | 테스트 코드 추가/수정 |
| `chore` | 빌드 설정 변경 |
| `docs` | 문서 수정 (README 등) |
| `comment` | 주석 추가 및 변경 |
| `rename` | 파일 혹은 폴더명 수정 |
| `remove` | 파일 삭제 |
| `style` | 코드 포맷팅, 세미콜론 등 스타일 변경 (논리 변경 X) |
| `merge` | 파일 병합 |

### 커밋 메시지 형식

```
<type>(#<issue>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

**예시**
```
feat(#1): 상품 목록 조회 API 구현
fix(#2): 재고 차감 시 동시성 이슈 수정
refactor(#4): ProductService 캐싱 로직 분리
test(#5): 주문 생성 통합 테스트 작성
```

### 커밋 세분화 규칙
- `Co-Authored-By: Claude` 절대 포함 금지
- **레이어 단위로 커밋 세분화**: Repository → Service → Controller → Test
- 한 커밋 = 한 레이어 또는 한 역할

---

## 자바 코드 스타일

### 패키지 이름
- 연속된 소문자 단어로 구성 (e.g. `com.example.deepspace`)

### 클래스 이름
- `UpperCamelCase` — 명사 혹은 명사구

### 메소드 이름
- `lowerCamelCase` — 동사 혹은 동사구

### 상수 이름
- `CONSTANT_CASE` — 모두 대문자, 단어 구분은 `_`

### 필드 / 파라미터 이름
- `lowerCamelCase` — 명사 혹은 명사구
- public 메서드에서 한 글자 파라미터 금지

### 테스트 메소드 이름
- `기능_테스트_회원_정보를_수정한다`
- `예외_테스트_이메일이_중복되었다`

---

## 핵심 아키텍처 패턴

### 의존 방향
`controller → service(facade) → domain(model/repository)` 단방향

### 동시성 제어
- Redisson 분산 락 (`@DistributedLock`) + JPA 비관적 락
- 재고 차감 등 크리티컬 섹션에 적용

### 캐싱 전략
- Redis: 상품 목록(5분 TTL), 상품 상세(10분 TTL)
- 상품 변경 시 자동 eviction
- 캐시 실패 시 graceful degradation

### 이벤트 드리븐
- 주문 생성 → Spring Event → Kafka → NotificationConsumer (3회 재시도 + DLT)

### API 응답 표준
```java
ApiResponse<T> { success, message, data, timestamp }
```

### 에러 처리
- 도메인별 ErrorCode enum + BusinessException 패턴
- GlobalExceptionHandler (@RestControllerAdvice)

---

## 완료된 도메인
- User (OAuth2 Google/Kakao/Naver + JWT)
- Product (CRUD + 캐싱 + 재고 관리 + QueryDSL 동적 필터링)
- Order (주문 생성/결제/배송/취소 + Facade 패턴)
- Payment (PortOne 연동)
- Delivery (주문 시 자동 생성)
- Cart (회원/비회원 이중 모드)
- Notification (Kafka 비동기 + 재시도 + DLT)
- Review (상품 리뷰 + 평점)
- Like (상품 찜)
- ViewHistory (최근 본 상품)
- Category (카테고리 CRUD)
- SMS (CoolSMS 인증)
