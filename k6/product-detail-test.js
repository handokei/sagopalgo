import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 상품 상세 + 이미지 조회 부하 테스트
 */
export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 50 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  // 상품 목록에서 ID 가져오기
  const res = http.get(`${BASE_URL}/api/products?page=0&size=10`);
  const products = res.json('content');

  if (products && products.length > 0) {
    return { productIds: products.map(p => p.id) };
  }
  return { productIds: [1] };
}

export default function (data) {
  const productId = data.productIds[Math.floor(Math.random() * data.productIds.length)];

  // 상품 상세 조회
  const detailRes = http.get(`${BASE_URL}/api/products/${productId}`);
  check(detailRes, {
    'detail status 200': (r) => r.status === 200,
  });

  sleep(0.3);

  // 이미지 목록 조회
  const imageRes = http.get(`${BASE_URL}/api/products/${productId}/images`);
  check(imageRes, {
    'images status 200': (r) => r.status === 200 || r.status === 404,
  });

  sleep(0.5);
}
