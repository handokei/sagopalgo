import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 스파이크 테스트
 * 갑작스러운 트래픽 증가 시 서버 동작 확인
 */
export const options = {
  stages: [
    { duration: '10s', target: 5 },    // 워밍업
    { duration: '5s', target: 100 },   // 스파이크!
    { duration: '30s', target: 100 },  // 유지
    { duration: '10s', target: 5 },    // 감소
    { duration: '10s', target: 0 },    // 종료
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  const res = http.get(`${BASE_URL}/api/products?page=0&size=10`);

  check(res, {
    'status 200': (r) => r.status === 200,
    'response time < 2s': (r) => r.timings.duration < 2000,
  });

  sleep(0.1);
}
