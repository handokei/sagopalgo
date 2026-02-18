# k6 부하 테스트

## 설치

```bash
# Mac
brew install k6
```

## 테스트 종류

### 

| 테스트 | 설명 | 용도 | 시간 |
|--------|------|------|------|
| `realistic-load-test.js` | 시나리오 기반 종합 테스트 | 실제 트래픽 패턴 시뮬레이션 | 3분 |
| `stress-test.js` | 스트레스 테스트 | 시스템 한계점 찾기 | 9분 |
| `soak-test.js` | 내구성 테스트 | 메모리 누수, 커넥션 풀 문제 | 30분 |
| `breakpoint-test.js` | 한계점 테스트 | 정확한 Breaking Point 측정 | 4분 |

### 기본 테스트

| 테스트 | 설명 |
|--------|------|
| `product-list-test.js` | 상품 목록 조회 |
| `product-detail-test.js` | 상품 상세 + 이미지 |
| `order-flow-test.js` | 주문 플로우 |
| `stock-concurrency-test.js` | 재고 동시성 |
| `spike-test.js` | 스파이크 트래픽 |

## 사전 준비

### 테스트 사용자 생성

```bash
# 테스트 사용자 3명 생성 (realistic-load-test 용)
for i in 1 2 3; do
  curl -X POST http://localhost:8080/api/users/register \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"test${i}@test.com\",\"password\":\"Test1234@\",\"confirmPassword\":\"Test1234@\",\"name\":\"테스트${i}\",\"nickname\":\"테스터${i}\",\"userRole\":\"ROLE_USER\"}"
done
```

## 실행 방법

### 기본 실행

```bash
# 종합 부하 테스트 (권장 - 먼저 실행)
k6 run ./k6/realistic-load-test.js

# 스트레스 테스트 (시스템 한계 확인)
k6 run ./k6/stress-test.js

# 내구성 테스트 (메모리 누수 확인)
k6 run ./k6/soak-test.js

# 한계점 테스트 (Breaking Point 측정)
k6 run ./k6/breakpoint-test.js
```

### 환경변수 설정

```bash
# 다른 서버 대상
k6 run -e BASE_URL=http://192.168.1.100:8080 ./k6/realistic-load-test.js
```

### 결과 저장

```bash
k6 run --out json=result.json ./k6/realistic-load-test.js
```

## 테스트 유형별 가이드

### Load Test (부하 테스트)
- **목적**: 예상 트래픽에서 시스템이 정상 동작하는지 확인
- **사용 시점**: 배포 전, 정기 성능 검증
- **실행**: `realistic-load-test.js`

### Stress Test (스트레스 테스트)
- **목적**: 시스템 한계 확인, 병목 지점 발견
- **사용 시점**: 용량 계획, 인프라 확장 전
- **실행**: `stress-test.js`

### Soak Test (내구성 테스트)
- **목적**: 장시간 운영 시 문제 발견 (메모리 누수 등)
- **사용 시점**: 대규모 릴리즈 전
- **실행**: `soak-test.js`

### Breakpoint Test (한계점 테스트)
- **목적**: 정확한 시스템 한계 RPS 측정
- **사용 시점**: 용량 계획, SLA 설정
- **실행**: `breakpoint-test.js`

## 성능 기준 (SLA)

| 메트릭 | 목표 | 설명 |
|--------|------|------|
| p95 응답시간 | < 1초 | 95%의 요청이 1초 이내 응답 |
| p99 응답시간 | < 2초 | 99%의 요청이 2초 이내 응답 |
| 에러율 | < 1% | 정상 운영 시 |
| 주문 성공률 | > 95% | 재고 있을 때 |

## Grafana 연동

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

**RPS:**
```promql
sum(rate(http_server_requests_seconds_count{job="sagopalgo"}[1m]))
```

**JVM Heap Memory:**
```promql
jvm_memory_used_bytes{job="sagopalgo", area="heap"}
```

## 결과 해석

### 정상
- p95 < 1초, 에러율 < 1%
- 응답시간이 시간이 지나도 안정적

### 문제 징후
- p95 > 2초: 성능 최적화 필요
- 에러율 > 5%: 시스템 장애 가능성
- 응답시간 점진적 증가: 메모리 누수 의심
