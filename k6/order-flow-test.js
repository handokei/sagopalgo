import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 주문 플로우 부하 테스트
 * 로그인 → 장바구니 → 주문
 *
 * 사전 준비: 테스트 사용자 생성 필요
 */
export const options = {
  stages: [
    { duration: '30s', target: 5 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = 'http://localhost:8080';

// 테스트 사용자 (미리 생성 필요)
const TEST_USER = {
  email: 'test@test.com',
  password: 'test1234',
};

export function setup() {
  // 로그인
  const loginRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify(TEST_USER),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (loginRes.status !== 200) {
    console.log('로그인 실패 - 테스트 사용자를 먼저 생성하세요');
    return { token: null, productId: null };
  }

  const token = loginRes.json('accessToken');

  // 상품 ID 가져오기
  const productRes = http.get(`${BASE_URL}/api/products?page=0&size=1`);
  const products = productRes.json('content');
  const productId = products && products.length > 0 ? products[0].id : 1;

  return { token, productId };
}

export default function (data) {
  if (!data.token) {
    console.log('토큰 없음 - 스킵');
    return;
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  // 1. 장바구니 조회
  const cartRes = http.get(`${BASE_URL}/api/carts/me`, { headers });
  check(cartRes, {
    'cart fetch': (r) => r.status === 200,
  });

  sleep(1);

  // 2. 장바구니에 상품 추가
  const addRes = http.post(
    `${BASE_URL}/api/carts/me/items`,
    JSON.stringify({ productId: data.productId, quantity: 1 }),
    { headers }
  );
  check(addRes, {
    'cart add': (r) => r.status === 200 || r.status === 201 || r.status === 400,
  });

  sleep(1);

  // 3. 주문 생성
  const orderRes = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      items: [{ productId: data.productId, quantity: 1 }],
      name: '테스트',
      phoneNumber: '010-1234-5678',
      address: '서울시 테스트구',
    }),
    { headers }
  );

  check(orderRes, {
    'order created': (r) => r.status === 200 || r.status === 201,
    'order failed (stock)': (r) => r.status === 400,
  });

  sleep(2);
}
