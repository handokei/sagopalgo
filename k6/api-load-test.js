import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * API 기본 부하 테스트
 *
 * 상품 목록 조회 API의 성능을 테스트합니다.
 */

export const options = {
  scenarios: {
    // 시나리오 1: 일반 부하
    normal_load: {
      executor: 'constant-vus',
      vus: 10,
      duration: '1m',
    },
    // 시나리오 2: 스파이크 테스트
    spike: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 100 },  // 급격히 100명까지
        { duration: '30s', target: 100 },  // 유지
        { duration: '10s', target: 0 },    // 급격히 감소
      ],
      startTime: '1m',  // 1분 후 시작
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<200'],  // 95%가 200ms 이하
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  // 상품 목록 조회 (비인증 API)
  const listRes = http.get(`${BASE_URL}/api/products?page=0&size=10`);
  check(listRes, {
    'product list status 200': (r) => r.status === 200,
    'product list has content': (r) => r.json('content') !== undefined,
  });

  sleep(0.5);

  // 상품 상세 조회
  const products = listRes.json('content');
  if (products && products.length > 0) {
    const productId = products[0].id;
    const detailRes = http.get(`${BASE_URL}/api/products/${productId}`);
    check(detailRes, {
      'product detail status 200': (r) => r.status === 200,
    });
  }

  sleep(0.5);
}
