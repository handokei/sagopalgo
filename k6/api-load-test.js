import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 설정
export const options = {
  stages: [
    { duration: '20s', target: 10 },   // 20초 동안 10명까지 증가
    { duration: '40s', target: 30 },   // 40초 동안 30명 유지
    { duration: '20s', target: 60 },   // 60명까지 증가
    { duration: '20s', target: 0 },    // 종료
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'], // 95% 요청이 800ms 이하
    http_req_failed: ['rate<0.05'],   // 실패율 5% 이하
  },
};

export default function () {
  const res = http.get('http://localhost:8080/api/products');

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

}
