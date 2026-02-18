# k6 부하 테스트

## 설치

```bash
# Mac
brew install k6

# Windows
choco install k6

# Docker
docker pull grafana/k6
```

## 테스트 스크립트

### 1. API 기본 부하 테스트 (api-load-test.js)
상품 목록/상세 조회 API 성능 테스트

```bash
k6 run k6/api-load-test.js
```

### 2. 주문 부하 테스트 (order-load-test.js)
로그인 → 장바구니 → 주문 플로우 테스트

**사전 준비:**
```sql
-- 테스트 사용자 생성 (비밀번호: test1234의 BCrypt 해시)
INSERT INTO users (email, password, name, nickname, user_role, is_deleted)
VALUES ('loadtest@test.com', '$2a$10$...', '테스트', '테스터', 'USER', false);
```

```bash
k6 run k6/order-load-test.js
```

### 3. 재고 경쟁 테스트 (stock-race-test.js)
동시 주문 시 재고 정합성 테스트

**사전 준비:**
1. 테스트 사용자 100명 생성
2. 재고 10개인 상품 생성

```bash
k6 run k6/stock-race-test.js
```

## 결과 해석

```
http_req_duration..............: avg=45ms  min=10ms  max=500ms  p(95)=120ms
http_req_failed................: 0.00%
```

- **avg**: 평균 응답 시간
- **p(95)**: 95% 요청의 응답 시간
- **http_req_failed**: 실패율

## Grafana 연동

```bash
# InfluxDB로 결과 저장
k6 run --out influxdb=http://localhost:8086/k6 k6/api-load-test.js
```
