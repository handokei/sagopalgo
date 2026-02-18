import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 설정
export const options = {
  // 시나리오 1: 점진적 부하 증가
  stages: [
    { duration: '30s', target: 10 },  // 30초 동안 10명까지 증가
    { duration: '1m', target: 10 },   // 1분 동안 10명 유지
    { duration: '30s', target: 50 },  // 30초 동안 50명까지 증가
    { duration: '1m', target: 50 },   // 1분 동안 50명 유지
    { duration: '30s', target: 0 },   // 30초 동안 0명으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95%의 요청이 500ms 이하
    http_req_failed: ['rate<0.01'],    // 실패율 1% 미만
  },
};

const BASE_URL = 'http://localhost:8080';

// 테스트용 사용자 정보 (미리 생성해둬야 함)
const TEST_USER = {
  email: 'loadtest@test.com',
  password: 'test1234',
};

// 테스트용 상품 ID (실제 존재하는 상품 ID로 변경)
const TEST_PRODUCT_ID = 1;

export function setup() {
  // 로그인하여 토큰 획득
  const loginRes = http.post(
    `${BASE_URL}/api/users/login`,
    JSON.stringify(TEST_USER),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(loginRes, {
    'login successful': (r) => r.status === 200,
  });

  const token = loginRes.json('accessToken');
  return { token };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  // 1. 상품 조회
  const productRes = http.get(`${BASE_URL}/api/products/${TEST_PRODUCT_ID}`, { headers });
  check(productRes, {
    'product fetch successful': (r) => r.status === 200,
  });

  sleep(1);

  // 2. 장바구니에 추가
  const cartRes = http.post(
    `${BASE_URL}/api/carts/me/items`,
    JSON.stringify({
      productId: TEST_PRODUCT_ID,
      quantity: 1,
    }),
    { headers }
  );
  check(cartRes, {
    'cart add successful': (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);

  // 3. 주문 생성
  const orderRes = http.post(
    `${BASE_URL}/api/orders`,
    JSON.stringify({
      items: [{ productId: TEST_PRODUCT_ID, quantity: 1 }],
      name: '테스트',
      phoneNumber: '010-1234-5678',
      address: '서울시 테스트구',
    }),
    { headers }
  );
  check(orderRes, {
    'order successful': (r) => r.status === 200 || r.status === 201,
    'order failed - out of stock': (r) => r.status === 400,
  });

  sleep(2);
}
