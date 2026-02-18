# k6 부하 테스트

## 설치

```bash
# Mac
brew install k6
```

## 테스트 스크립트

| 스크립트 | 설명 | 명령어 |
|---------|------|--------|
| product-list-test.js | 상품 목록 조회 | `k6 run ./k6/product-list-test.js` |
| product-detail-test.js | 상품 상세 + 이미지 | `k6 run ./k6/product-detail-test.js` |
| order-flow-test.js | 주문 플로우 | `k6 run ./k6/order-flow-test.js` |
| stock-concurrency-test.js | 재고 동시성 | `k6 run ./k6/stock-concurrency-test.js` |
| spike-test.js | 스파이크 테스트 | `k6 run ./k6/spike-test.js` |

## 사전 준비

### 테스트 사용자 생성
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test1234","name":"테스트","nickname":"테스터"}'
```

## 실행 예시

```bash
# 기본 실행
k6 run ./k6/product-list-test.js

# 결과 JSON 출력
k6 run --out json=result.json ./k6/product-list-test.js

# VU 수 변경
k6 run --vus 100 --duration 30s ./k6/spike-test.js
```

## Grafana 연동

Prometheus + Grafana가 실행 중이면 실시간 모니터링 가능:
```bash
docker-compose up -d
```

### 추천 Grafana 쿼리

**p95 응답시간:**
```promql
histogram_quantile(0.95, sum by (le) (rate(http_server_requests_seconds_bucket{job="sagopalgo"}[1m])))
```

**Error Rate:**
```promql
sum(rate(http_server_requests_seconds_count{job="sagopalgo", status=~"5.."}[1m]))
/ sum(rate(http_server_requests_seconds_count{job="sagopalgo"}[1m]))
or vector(0)
```

**RPS (Requests Per Second):**
```promql
sum(rate(http_server_requests_seconds_count{job="sagopalgo"}[1m]))
```
