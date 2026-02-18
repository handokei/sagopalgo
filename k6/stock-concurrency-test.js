import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

/**
 * 재고 동시성 테스트
 *
 * 시나리오: 재고 10개인 상품에 50명이 동시 주문
 * 예상: 정확히 10명만 성공
 *
 * 사전 준비:
 * 1. 테스트 사용자 생성 (test@test.com / test1234)
 * 2. 재고 10개인 상품 생성 후 ID 확인
 */

const successCount = new Counter('successful_orders');
const failCount = new Counter('failed_orders');

export const options = {
  vus: 50,
  iterations: 50,
  thresholds: {
    http_req_duration: ['p(95)<2000'],
  },
};

const BASE_URL = 'http://localhost:8080';
const TEST_PRODUCT_ID = 6; // IN_STOCK 상품

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

  if (loginRes.status !== 200) {
    console.log('로그인 실패');
    return { token: null };
  }

  return { token: loginRes.json('accessToken') };
}

export default function (data) {
  if (!data.token) return;

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  const res = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      items: [{ productId: TEST_PRODUCT_ID, quantity: 1 }],
      name: `테스트_${__VU}`,
      phoneNumber: '010-1234-5678',
      address: '서울시',
    }),
    { headers }
  );

  if (res.status === 200 || res.status === 201) {
    successCount.add(1);
    console.log(`VU ${__VU}: 주문 성공`);
  } else {
    failCount.add(1);
    console.log(`VU ${__VU}: 주문 실패 (${res.status})`);
  }

  check(res, {
    'response received': (r) => r.status !== 0,
  });
}

export function teardown(data) {
  console.log('\n========================================');
  console.log('재고 동시성 테스트 완료');
  console.log('성공한 주문 수와 재고 감소량이 일치하는지 확인하세요');
  console.log('========================================\n');
}
