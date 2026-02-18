import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

/**
 * Breakpoint 테스트
 *
 * 목적: 시스템의 정확한 한계점(Breaking Point) 찾기
 * - 무한히 부하를 증가시켜 시스템이 실패하는 지점 확인
 * - SLA 기준(p95 < 1초)을 초과하는 지점 찾기
 *
 * 주의: 이 테스트는 시스템을 의도적으로 과부하시킵니다.
 *       프로덕션 환경에서 절대 실행하지 마세요!
 */

const errorRate = new Rate('error_rate');
const slaViolation = new Rate('sla_violation');  // p95 > 1초 위반
const responseTime = new Trend('response_time');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  scenarios: {
    breakpoint: {
      executor: 'ramping-arrival-rate',
      startRate: 10,
      timeUnit: '1s',
      preAllocatedVUs: 500,
      maxVUs: 1000,
      stages: [
        { duration: '30s', target: 20 },   // 20 RPS
        { duration: '30s', target: 50 },   // 50 RPS
        { duration: '30s', target: 100 },  // 100 RPS
        { duration: '30s', target: 150 },  // 150 RPS
        { duration: '30s', target: 200 },  // 200 RPS
        { duration: '30s', target: 300 },  // 300 RPS
        { duration: '30s', target: 400 },  // 400 RPS
        { duration: '30s', target: 500 },  // 500 RPS (한계 테스트)
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<5000'],  // 일단 높게 설정 (한계 찾기 위해)
    error_rate: ['rate<0.5'],            // 50% 미만
  },
};

export default function () {
  const endpoints = [
    '/api/products?page=0&size=10',
    '/api/products/1',
    '/api/products/2',
    '/api/products/3',
  ];

  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
  const start = Date.now();

  const res = http.get(`${BASE_URL}${endpoint}`);

  const duration = Date.now() - start;
  responseTime.add(duration);
  errorRate.add(res.status !== 200);
  slaViolation.add(duration > 1000);  // 1초 초과시 SLA 위반

  check(res, {
    'status ok': (r) => r.status === 200,
    'response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function teardown() {
  console.log('\n========================================');
  console.log('Breakpoint 테스트 완료');
  console.log('');
  console.log('결과 분석:');
  console.log('1. sla_violation 비율이 급증하는 RPS 확인');
  console.log('2. error_rate가 급증하는 RPS 확인');
  console.log('3. 두 지점 중 먼저 발생하는 것이 Breaking Point');
  console.log('');
  console.log('권장 용량:');
  console.log('- Breaking Point의 70-80%를 최대 용량으로 설정');
  console.log('========================================\n');
}
