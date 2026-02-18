import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { SharedArray } from 'k6/data';

/**
 * 실무 수준 종합 부하 테스트
 *
 * 시나리오:
 * - 70% 브라우징 (상품 목록 + 상세 조회)
 * - 20% 장바구니 추가
 * - 10% 주문 완료
 *
 * 부하 패턴:
 * - Ramp-up: 점진적 증가
 * - Steady State: 안정 상태 유지
 * - Ramp-down: 점진적 감소
 */

// ============ 커스텀 메트릭 ============
const orderSuccessRate = new Rate('order_success_rate');
const cartAddRate = new Rate('cart_add_success_rate');
const pageLoadTime = new Trend('page_load_time');
const orderProcessTime = new Trend('order_process_time');
const apiErrors = new Counter('api_errors');
const businessTransactions = new Counter('business_transactions');

// ============ 설정 ============
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 테스트 사용자 풀 (실무에서는 더 많은 사용자)
const TEST_USERS = [
  { email: 'test1@test.com', password: 'Test1234@' },
  { email: 'test2@test.com', password: 'Test1234@' },
  { email: 'test3@test.com', password: 'Test1234@' },
];

// ============ 부하 시나리오 ============
export const options = {
  scenarios: {
    // 시나리오 1: 브라우징 사용자 (70%)
    browsers: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 35 },  // Ramp-up
        { duration: '2m', target: 35 },   // Steady
        { duration: '30s', target: 0 },   // Ramp-down
      ],
      exec: 'browsingScenario',
    },
    // 시나리오 2: 장바구니 사용자 (20%)
    shoppers: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 10 },
        { duration: '2m', target: 10 },
        { duration: '30s', target: 0 },
      ],
      exec: 'shoppingScenario',
    },
    // 시나리오 3: 구매자 (10%)
    buyers: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 5 },
        { duration: '2m', target: 5 },
        { duration: '30s', target: 0 },
      ],
      exec: 'purchaseScenario',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],  // p95 < 1초, p99 < 2초
    http_req_failed: ['rate<0.05'],                   // 에러율 5% 미만
    order_success_rate: ['rate>0.8'],                 // 주문 성공률 80% 이상
    cart_add_success_rate: ['rate>0.9'],              // 장바구니 추가 성공률 90% 이상
    page_load_time: ['p(95)<500'],                    // 페이지 로드 p95 < 500ms
  },
};

// ============ Setup: 로그인 토큰 획득 ============
export function setup() {
  const tokens = {};

  for (const user of TEST_USERS) {
    const loginRes = http.post(
      `${BASE_URL}/api/users/login`,
      JSON.stringify(user),
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (loginRes.status === 200) {
      tokens[user.email] = loginRes.json('accessToken');
    }
  }

  // 상품 목록 가져오기
  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=20`);
  const products = productRes.json('content') || [];

  return {
    tokens,
    products: products.filter(p => p.stockStatus !== 'OUT_OF_STOCK'),
  };
}

// ============ 헬퍼 함수 ============
function getRandomUser(data) {
  const emails = Object.keys(data.tokens);
  const email = emails[Math.floor(Math.random() * emails.length)];
  return { email, token: data.tokens[email] };
}

function getRandomProduct(data) {
  if (!data.products || data.products.length === 0) return null;
  return data.products[Math.floor(Math.random() * data.products.length)];
}

function getAuthHeaders(token) {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

// Think time: 실제 사용자처럼 페이지를 읽는 시간
function thinkTime(min = 1, max = 3) {
  sleep(min + Math.random() * (max - min));
}

// ============ 시나리오 1: 브라우징 ============
export function browsingScenario(data) {
  group('상품 목록 조회', () => {
    const page = Math.floor(Math.random() * 3);
    const start = Date.now();

    const res = http.get(`${BASE_URL}/api/products?page=${page}&size=10`);

    pageLoadTime.add(Date.now() - start);

    const success = check(res, {
      'status 200': (r) => r.status === 200,
      'has content': (r) => r.json('content') !== null,
    });

    if (!success) apiErrors.add(1);
    businessTransactions.add(1);
  });

  thinkTime(2, 5);  // 목록 살펴보는 시간

  group('상품 상세 조회', () => {
    const product = getRandomProduct(data);
    if (!product) return;

    const start = Date.now();
    const res = http.get(`${BASE_URL}/api/products/${product.id}`);

    pageLoadTime.add(Date.now() - start);

    const success = check(res, {
      'detail status 200': (r) => r.status === 200,
    });

    if (!success) apiErrors.add(1);
    businessTransactions.add(1);

    // 이미지도 조회
    http.get(`${BASE_URL}/api/products/${product.id}/images`);
  });

  thinkTime(3, 8);  // 상품 상세 보는 시간
}

// ============ 시나리오 2: 장바구니 ============
export function shoppingScenario(data) {
  const { token } = getRandomUser(data);
  if (!token) return;

  const headers = getAuthHeaders(token);

  // 먼저 브라우징
  browsingScenario(data);

  group('장바구니 추가', () => {
    const product = getRandomProduct(data);
    if (!product) return;

    const quantity = Math.floor(Math.random() * 3) + 1;  // 1~3개

    const res = http.post(
      `${BASE_URL}/api/carts/me/items`,
      JSON.stringify({ productId: product.id, quantity }),
      { headers }
    );

    const success = res.status === 200 || res.status === 201;
    cartAddRate.add(success);

    if (!success) apiErrors.add(1);
    businessTransactions.add(1);
  });

  thinkTime(1, 3);

  group('장바구니 조회', () => {
    const res = http.get(`${BASE_URL}/api/carts/me`, { headers });

    check(res, {
      'cart status 200': (r) => r.status === 200,
    });

    businessTransactions.add(1);
  });

  thinkTime(2, 5);
}

// ============ 시나리오 3: 구매 ============
export function purchaseScenario(data) {
  const { token } = getRandomUser(data);
  if (!token) return;

  const headers = getAuthHeaders(token);

  // 먼저 쇼핑
  shoppingScenario(data);

  group('주문 생성', () => {
    const product = getRandomProduct(data);
    if (!product) return;

    const start = Date.now();

    const res = http.post(
      `${BASE_URL}/api/orders`,
      JSON.stringify({
        items: [{ productId: product.id, quantity: 1 }],
        name: `테스트_${__VU}_${__ITER}`,
        phoneNumber: '010-1234-5678',
        address: '서울시 테스트구 테스트동',
      }),
      { headers }
    );

    orderProcessTime.add(Date.now() - start);

    const success = res.status === 200 || res.status === 201;
    orderSuccessRate.add(success);

    check(res, {
      'order created': (r) => r.status === 200 || r.status === 201,
    });

    if (!success) {
      apiErrors.add(1);
      console.log(`주문 실패: ${res.status} - ${res.body}`);
    }

    businessTransactions.add(1);
  });

  thinkTime(1, 2);

  group('주문 내역 조회', () => {
    const res = http.get(`${BASE_URL}/api/orders?page=0&size=5`, { headers });

    check(res, {
      'orders status 200': (r) => r.status === 200,
    });

    businessTransactions.add(1);
  });
}

// ============ Teardown ============
export function teardown(data) {
  console.log('\n========================================');
  console.log('종합 부하 테스트 완료');
  console.log(`테스트 상품 수: ${data.products?.length || 0}`);
  console.log(`테스트 사용자 수: ${Object.keys(data.tokens).length}`);
  console.log('========================================\n');
}
