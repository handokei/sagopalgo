import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

/**
 * Soak 테스트 (내구성 테스트)
 *
 * 목적: 장시간 운영 시 발생하는 문제 발견
 * - 메모리 누수
 * - DB 커넥션 풀 고갈
 * - 로그 파일 증가
 * - 캐시 문제
 *
 * 실행 시간: 30분 ~ 1시간 (실무에서는 4-8시간)
 */

const errorRate = new Rate('error_rate');
const responseTime = new Trend('response_time');
const memoryLeakIndicator = new Counter('requests_total');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '2m', target: 30 },   // Ramp-up
    { duration: '26m', target: 30 },  // 안정적인 부하로 장시간 유지
    { duration: '2m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    error_rate: ['rate<0.01'],  // 장시간 운영시 에러율 1% 미만
  },
};

const TEST_USER = {
  email: 'test3@test.com',
  password: 'Test1234@',
};

export function setup() {
  const loginRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify(TEST_USER),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const token = loginRes.status === 200 ? loginRes.json('accessToken') : null;

  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=20`);
  const products = productRes.json('content') || [];

  console.log('========================================');
  console.log('Soak 테스트 시작');
  console.log('- 모니터링 권장 항목:');
  console.log('  1. JVM Heap Memory (Grafana)');
  console.log('  2. DB Connection Pool');
  console.log('  3. Thread Count');
  console.log('  4. GC Pause Time');
  console.log('========================================\n');

  return { token, products };
}

export default function (data) {
  const actions = [
    () => browseProducts(),
    () => viewProduct(data),
    () => viewCart(data),
    () => viewOrders(data),
  ];

  // 랜덤 액션 선택
  const action = actions[Math.floor(Math.random() * actions.length)];
  action();

  memoryLeakIndicator.add(1);

  // 실제 사용자처럼 천천히
  sleep(1 + Math.random() * 2);
}

function browseProducts() {
  const page = Math.floor(Math.random() * 5);
  const start = Date.now();

  const res = http.get(`${BASE_URL}/api/products?page=${page}&size=10`);

  responseTime.add(Date.now() - start);
  errorRate.add(res.status !== 200);

  check(res, { 'browse ok': (r) => r.status === 200 });
}

function viewProduct(data) {
  const product = data.products[Math.floor(Math.random() * data.products.length)];
  if (!product) return browseProducts();

  const start = Date.now();
  const res = http.get(`${BASE_URL}/api/products/${product.id}`);

  responseTime.add(Date.now() - start);
  errorRate.add(res.status !== 200);

  check(res, { 'product ok': (r) => r.status === 200 });
}

function viewCart(data) {
  if (!data.token) return browseProducts();

  const headers = {
    'Authorization': `Bearer ${data.token}`,
  };

  const start = Date.now();
  const res = http.get(`${BASE_URL}/api/carts/me`, { headers });

  responseTime.add(Date.now() - start);
  errorRate.add(res.status !== 200);

  check(res, { 'cart ok': (r) => r.status === 200 });
}

function viewOrders(data) {
  if (!data.token) return browseProducts();

  const headers = {
    'Authorization': `Bearer ${data.token}`,
  };

  const start = Date.now();
  const res = http.get(`${BASE_URL}/api/orders?page=0&size=10`, { headers });

  responseTime.add(Date.now() - start);
  errorRate.add(res.status !== 200);

  check(res, { 'orders ok': (r) => r.status === 200 });
}

export function teardown() {
  console.log('\n========================================');
  console.log('Soak 테스트 완료');
  console.log('');
  console.log('확인 사항:');
  console.log('1. 응답 시간이 시간이 지남에 따라 증가했는지?');
  console.log('2. 메모리 사용량이 계속 증가했는지?');
  console.log('3. 에러율이 시간이 지남에 따라 증가했는지?');
  console.log('4. DB 커넥션이 정상적으로 반환되었는지?');
  console.log('========================================\n');
}
