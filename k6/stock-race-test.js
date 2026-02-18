import http from 'k6/http';
import { check } from 'k6';

/**
 * 재고 경쟁 테스트
 *
 * 시나리오: 재고 10개인 상품에 100명이 동시에 주문
 * 목표: 정확히 10명만 성공해야 함 (동시성 처리 검증)
 *
 * 사전 준비:
 * 1. 테스트 사용자 여러 명 생성 (loadtest1@test.com ~ loadtest100@test.com)
 * 2. 테스트 상품 생성 (재고 10개)
 */

export const options = {
  vus: 100,           // 100명 동시 사용자
  iterations: 100,    // 총 100번 실행 (1인당 1번)
  thresholds: {
    http_req_duration: ['p(95)<1000'],
  },
};

const BASE_URL = 'http://localhost:8080';
const TEST_PRODUCT_ID = 1;  // 재고 10개인 상품 ID

// 미리 생성된 테스트 토큰들 (실제 테스트 시 생성 필요)
// 또는 setup에서 여러 사용자 로그인
const TEST_TOKENS = [];

export function setup() {
  const tokens = [];

  // 테스트 사용자 100명 로그인 (미리 생성되어 있어야 함)
  for (let i = 1; i <= 100; i++) {
    const loginRes = http.post(
      `${BASE_URL}/api/users/login`,
      JSON.stringify({
        email: `loadtest${i}@test.com`,
        password: 'test1234',
      }),
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (loginRes.status === 200) {
      tokens.push(loginRes.json('accessToken'));
    }
  }

  console.log(`로그인 성공: ${tokens.length}명`);
  return { tokens };
}

export default function (data) {
  const vuId = __VU - 1;  // 0부터 시작
  const token = data.tokens[vuId % data.tokens.length];

  if (!token) {
    console.log('토큰 없음');
    return;
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };

  // 동시에 주문 시도
  const orderRes = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      items: [{ productId: TEST_PRODUCT_ID, quantity: 1 }],
      name: `테스트${vuId}`,
      phoneNumber: '010-1234-5678',
      address: '서울시 테스트구',
    }),
    { headers }
  );

  const success = orderRes.status === 200 || orderRes.status === 201;
  const outOfStock = orderRes.status === 400;

  check(orderRes, {
    'order response received': (r) => r.status !== 0,
  });

  if (success) {
    console.log(`VU ${vuId}: 주문 성공!`);
  } else if (outOfStock) {
    console.log(`VU ${vuId}: 재고 부족`);
  } else {
    console.log(`VU ${vuId}: 실패 - ${orderRes.status}`);
  }
}

export function teardown(data) {
  console.log('테스트 완료. 주문 성공 수와 재고 감소량이 일치하는지 확인하세요.');
}
