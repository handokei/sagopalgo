import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { SharedArray } from 'k6/data';
import { getBaseUrl, authHeaders, jsonHeaders, login, randomItem, randomInt, chance } from './lib/helpers.js';

/**
 * Heavy Load Test - 극한 부하 테스트
 *
 * 실행 방법:
 *   k6 run heavy-load-test.js
 *   k6 run -e ENV=dev heavy-load-test.js
 *   k6 run -e BASE_URL=http://api.example.com heavy-load-test.js
 */

// 외부 데이터 로드 (VU 간 공유, 메모리 효율적)
const users = new SharedArray('users', function() {
  return JSON.parse(open('./data/users.json'));
});

const orders = new SharedArray('orders', function() {
  return JSON.parse(open('./data/orders.json'));
});

const config = JSON.parse(open('./data/config.json'));

// 환경 설정
const ENV = __ENV.ENV || 'local';
const BASE_URL = __ENV.BASE_URL || config.environments[ENV]?.baseUrl || 'http://localhost:8080';
const testConfig = config.testData;

// 커스텀 메트릭
const errorRate = new Rate('error_rate');
const responseTime = new Trend('response_time');
const dbQueryTime = new Trend('db_query_time');
const ordersAttempted = new Counter('orders_attempted');
const ordersFailed = new Counter('orders_failed');

export const options = {
  scenarios: {
    extreme_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 200 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 800 },
        { duration: '1m', target: config.scenarios.heavy.maxVus },
        { duration: '1m', target: config.scenarios.heavy.maxVus },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_duration: [`p(95)<${config.environments[ENV]?.thresholds?.p95 || 5000}`],
    error_rate: [`rate<${config.environments[ENV]?.thresholds?.errorRate || 0.5}`],
  },
};

export function setup() {
  console.log(`\n========================================`);
  console.log(`Environment: ${ENV}`);
  console.log(`Base URL: ${BASE_URL}`);
  console.log(`Max VUs: ${config.scenarios.heavy.maxVus}`);
  console.log(`========================================\n`);

  // 여러 사용자로 로그인 (부하 분산)
  const tokens = [];
  for (const user of users) {
    const token = login(BASE_URL, user);
    if (token) {
      tokens.push({ token, email: user.email });
    }
  }

  // 상품 목록 조회
  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=100`);
  const products = productRes.json('content') || [];

  console.log(`로그인 성공: ${tokens.length}/${users.length}`);
  console.log(`상품 수: ${products.length}`);

  return { tokens, products };
}

export default function(data) {
  // VU별로 다른 사용자 토큰 사용 (부하 분산)
  const userSession = data.tokens.length > 0
    ? data.tokens[__VU % data.tokens.length]
    : null;

  // 1. 상품 목록 조회 (여러 페이지)
  for (let i = 0; i < 3; i++) {
    const page = randomInt(0, testConfig.maxPage - 1);
    const start = Date.now();

    const res = http.get(`${BASE_URL}/api/products?page=${page}&size=${testConfig.pageSize}`);

    dbQueryTime.add(Date.now() - start);
    responseTime.add(Date.now() - start);
    errorRate.add(res.status !== 200);

    check(res, { 'list ok': (r) => r.status === 200 });
  }

  // 2. 상품 상세 조회
  if (data.products && data.products.length > 0) {
    for (let i = 0; i < 5; i++) {
      const product = randomItem(data.products);
      const start = Date.now();

      const detailRes = http.get(`${BASE_URL}/api/products/${product.id}`);

      dbQueryTime.add(Date.now() - start);
      responseTime.add(Date.now() - start);
      errorRate.add(detailRes.status !== 200);
    }
  }

  // 3. 인증 필요한 작업
  if (userSession) {
    const headers = authHeaders(userSession.token);

    // 장바구니 조회
    const cartRes = http.get(`${BASE_URL}/api/carts/me`, { headers });
    errorRate.add(cartRes.status !== 200);

    // 주문 내역 조회
    const orderRes = http.get(`${BASE_URL}/api/orders?page=0&size=10`, { headers });
    errorRate.add(orderRes.status !== 200);

    // 설정된 확률로 주문 시도
    if (chance(testConfig.orderProbability) && data.products.length > 0) {
      const product = randomItem(data.products);
      const orderData = randomItem(orders);
      ordersAttempted.add(1);

      const start = Date.now();
      const createOrderRes = http.post(
        `${BASE_URL}/api/orders`,
        JSON.stringify({
          items: [{ productId: product.id, quantity: 1 }],
          name: orderData.name,
          phoneNumber: orderData.phoneNumber,
          address: orderData.address,
        }),
        { headers }
      );

      responseTime.add(Date.now() - start);

      if (createOrderRes.status !== 200 && createOrderRes.status !== 201) {
        ordersFailed.add(1);
      }
    }
  }

  sleep(0.05);
}

export function teardown(data) {
  console.log('\n========================================');
  console.log('Heavy Load 테스트 완료');
  console.log(`Environment: ${ENV}`);
  console.log(`테스트 사용자: ${data.tokens.length}명`);
  console.log('========================================\n');
}
