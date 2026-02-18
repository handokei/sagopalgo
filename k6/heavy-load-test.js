import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

/**
 * Heavy Load Test - DB 부하를 극대화하는 테스트
 *
 * 목적: 시스템을 터뜨려서 한계점 찾기
 * - 1000 VUs 동시 접속
 * - 복합 쿼리 (목록 + 상세 + 이미지)
 * - 쓰기 작업 포함 (장바구니, 주문)
 */

const errorRate = new Rate('error_rate');
const responseTime = new Trend('response_time');
const dbQueryTime = new Trend('db_query_time');
const ordersAttempted = new Counter('orders_attempted');
const ordersFailed = new Counter('orders_failed');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  scenarios: {
    // 극한 부하 시나리오
    extreme_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 200 },   // 200 VUs
        { duration: '30s', target: 500 },   // 500 VUs
        { duration: '30s', target: 800 },   // 800 VUs
        { duration: '1m', target: 1000 },   // 1000 VUs - 극한
        { duration: '1m', target: 1000 },   // 유지
        { duration: '30s', target: 0 },     // 종료
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<5000'],  // 일단 높게
    error_rate: ['rate<0.5'],
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

  // 상품 전체 조회
  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=100`);
  const products = productRes.json('content') || [];

  console.log(`로그인: ${token ? '성공' : '실패'}`);
  console.log(`상품 수: ${products.length}`);

  return { token, products };
}

export default function (data) {
  // 복합 시나리오: 한 iteration에 여러 요청

  // 1. 상품 목록 조회 (여러 페이지)
  for (let i = 0; i < 3; i++) {
    const page = Math.floor(Math.random() * 5);
    const start = Date.now();

    const res = http.get(`${BASE_URL}/api/products?page=${page}&size=20`);

    dbQueryTime.add(Date.now() - start);
    responseTime.add(Date.now() - start);
    errorRate.add(res.status !== 200);

    check(res, { 'list ok': (r) => r.status === 200 });
  }

  // 2. 상품 상세 조회 (여러 개)
  if (data.products && data.products.length > 0) {
    for (let i = 0; i < 5; i++) {
      const product = data.products[Math.floor(Math.random() * data.products.length)];
      const start = Date.now();

      const detailRes = http.get(`${BASE_URL}/api/products/${product.id}`);
      const imageRes = http.get(`${BASE_URL}/api/products/${product.id}/images`);

      dbQueryTime.add(Date.now() - start);
      responseTime.add(Date.now() - start);
      errorRate.add(detailRes.status !== 200);
    }
  }

  // 3. 인증 필요한 작업 (토큰 있을 때만)
  if (data.token) {
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`,
    };

    // 장바구니 조회
    const cartRes = http.get(`${BASE_URL}/api/carts/me`, { headers });
    errorRate.add(cartRes.status !== 200);

    // 주문 내역 조회
    const orderRes = http.get(`${BASE_URL}/api/orders?page=0&size=10`, { headers });
    errorRate.add(orderRes.status !== 200);

    // 10% 확률로 주문 시도
    if (Math.random() < 0.1 && data.products.length > 0) {
      const product = data.products[Math.floor(Math.random() * data.products.length)];
      ordersAttempted.add(1);

      const start = Date.now();
      const createOrderRes = http.post(
        `${BASE_URL}/api/orders`,
        JSON.stringify({
          items: [{ productId: product.id, quantity: 1 }],
          name: `Heavy_${__VU}`,
          phoneNumber: '010-1234-5678',
          address: '서울시',
        }),
        { headers }
      );

      responseTime.add(Date.now() - start);

      if (createOrderRes.status !== 200 && createOrderRes.status !== 201) {
        ordersFailed.add(1);
      }
    }
  }

  // 아주 짧은 대기 (부하 극대화)
  sleep(0.05);
}

export function teardown() {
  console.log('\n========================================');
  console.log('Heavy Load 테스트 완료');
  console.log('최대 1000 VUs 동시 접속 테스트');
  console.log('========================================\n');
}
