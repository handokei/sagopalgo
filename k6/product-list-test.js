import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 상품 목록 조회 부하 테스트
 */
export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '40s', target: 30 },
    { duration: '20s', target: 60 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  // 상품 목록 조회
  const listRes = http.get(`${BASE_URL}/api/products?page=0&size=12`);
  check(listRes, {
    'list status 200': (r) => r.status === 200,
  });

  sleep(0.5);

  // 정렬 변경해서 조회
  const sortedRes = http.get(`${BASE_URL}/api/products?page=0&size=12&sort=latest`);
  check(sortedRes, {
    'sorted list status 200': (r) => r.status === 200,
  });

  sleep(0.5);

  // 카테고리 필터
  const categoryRes = http.get(`${BASE_URL}/api/products?page=0&size=12&productCategory=T_SHIRT`);
  check(categoryRes, {
    'category filter status 200': (r) => r.status === 200,
  });

  sleep(0.5);
}
