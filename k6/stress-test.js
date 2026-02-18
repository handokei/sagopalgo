import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

/**
 * 스트레스 테스트
 *
 * 목적: 시스템의 한계점 찾기
 * - 점진적으로 부하를 증가시켜 시스템이 어디서 무너지는지 확인
 * - 최대 처리량(Max Throughput) 측정
 * - 병목 지점 식별
 */

const errorRate = new Rate('error_rate');
const responseTime = new Trend('response_time');
const throughput = new Counter('throughput');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    // 단계별 부하 증가
    { duration: '1m', target: 50 },    // 1분: 50 VUs
    { duration: '1m', target: 100 },   // 2분: 100 VUs
    { duration: '1m', target: 150 },   // 3분: 150 VUs
    { duration: '1m', target: 200 },   // 4분: 200 VUs
    { duration: '1m', target: 250 },   // 5분: 250 VUs (스트레스)
    { duration: '1m', target: 300 },   // 6분: 300 VUs (한계 테스트)
    { duration: '2m', target: 300 },   // 7-8분: 300 VUs 유지
    { duration: '1m', target: 0 },     // 9분: 종료
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'],  // 스트레스 상황에서도 p95 < 3초
    error_rate: ['rate<0.2'],            // 에러율 20% 미만 (스트레스 허용)
  },
};

const TEST_USER = {
  email: 'test3@test.com',
  password: 'Test1234@',
};

export function setup() {
  // 로그인
  const loginRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify(TEST_USER),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const token = loginRes.status === 200 ? loginRes.json('accessToken') : null;

  // 상품 목록
  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=20`);
  const products = productRes.json('content') || [];

  return { token, products };
}

export default function (data) {
  const scenarios = [
    { weight: 50, fn: () => productList() },
    { weight: 30, fn: () => productDetail(data) },
    { weight: 15, fn: () => cartOperation(data) },
    { weight: 5, fn: () => orderOperation(data) },
  ];

  // 가중치 기반 시나리오 선택
  const rand = Math.random() * 100;
  let cumulative = 0;

  for (const scenario of scenarios) {
    cumulative += scenario.weight;
    if (rand < cumulative) {
      scenario.fn();
      break;
    }
  }
}

function productList() {
  const start = Date.now();
  const page = Math.floor(Math.random() * 5);

  const res = http.get(`${BASE_URL}/api/products?page=${page}&size=10`);

  responseTime.add(Date.now() - start);
  throughput.add(1);
  errorRate.add(res.status !== 200);

  check(res, { 'product list ok': (r) => r.status === 200 });

  sleep(0.1 + Math.random() * 0.3);
}

function productDetail(data) {
  const product = data.products[Math.floor(Math.random() * data.products.length)];
  if (!product) return productList();

  const start = Date.now();
  const res = http.get(`${BASE_URL}/api/products/${product.id}`);

  responseTime.add(Date.now() - start);
  throughput.add(1);
  errorRate.add(res.status !== 200);

  check(res, { 'product detail ok': (r) => r.status === 200 });

  sleep(0.1 + Math.random() * 0.3);
}

function cartOperation(data) {
  if (!data.token) return productList();

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  const product = data.products[Math.floor(Math.random() * data.products.length)];
  if (!product) return;

  const start = Date.now();
  const res = http.post(
    `${BASE_URL}/api/carts/me/items`,
    JSON.stringify({ productId: product.id, quantity: 1 }),
    { headers }
  );

  responseTime.add(Date.now() - start);
  throughput.add(1);
  errorRate.add(res.status !== 200 && res.status !== 201 && res.status !== 400);

  sleep(0.1 + Math.random() * 0.2);
}

function orderOperation(data) {
  if (!data.token) return productList();

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  const product = data.products[Math.floor(Math.random() * data.products.length)];
  if (!product) return;

  const start = Date.now();
  const res = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      items: [{ productId: product.id, quantity: 1 }],
      name: `스트레스_${__VU}`,
      phoneNumber: '010-1234-5678',
      address: '서울시',
    }),
    { headers }
  );

  responseTime.add(Date.now() - start);
  throughput.add(1);
  errorRate.add(res.status !== 200 && res.status !== 201 && res.status !== 400);

  sleep(0.1 + Math.random() * 0.2);
}

export function teardown() {
  console.log('\n========================================');
  console.log('스트레스 테스트 완료');
  console.log('- 최대 VU: 300');
  console.log('- 응답시간과 에러율 그래프를 확인하세요');
  console.log('- 시스템 리소스(CPU, Memory, DB Connection) 확인 필요');
  console.log('========================================\n');
}
