import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// 설정
const BASE_URL = 'https://sagopalgo.p-e.kr';
const PRODUCT_ID = 5;  // 테스트할 상품 ID

// 테스트 계정 (미리 생성된 계정)
const TEST_EMAIL = 'test1@test.com';
const TEST_PASSWORD = 'Test1234!';

// 메트릭
const successCounter = new Counter('successful_orders');
const failCounter = new Counter('failed_orders');
const orderDuration = new Trend('order_duration');

let jwtToken = '';

export const options = {
    scenarios: {
        concurrent_orders: {
            executor: 'shared-iterations',
            vus: 100,           // 100명 동시 접속
            iterations: 100,    // 총 100번 주문
            maxDuration: '60s',
        },
    },
};

// 테스트 시작 전 로그인
export function setup() {
    const loginRes = http.post(`${BASE_URL}/api/users/login`, JSON.stringify({
        email: TEST_EMAIL,
        password: TEST_PASSWORD
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(loginRes, {
        'login success': (r) => r.status === 200,
    });

    console.log(`로그인 응답: ${loginRes.body}`);
    const body = JSON.parse(loginRes.body);
    console.log(`🔐 로그인 성공, 토큰: ${body.accessToken ? body.accessToken.substring(0, 50) + '...' : 'NULL'}`);

    return { token: body.accessToken };
}

export default function (data) {
    const url = `${BASE_URL}/api/orders`;

    const payload = JSON.stringify({
        items: [
            {
                productId: PRODUCT_ID,
                quantity: 1
            }
        ],
        name: '테스트유저',
        phoneNumber: '010-1234-5678',
        address: '서울시 강남구 테스트동'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`,
        },
    };

    const start = Date.now();
    const res = http.post(url, payload, params);
    const duration = Date.now() - start;

    orderDuration.add(duration);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    if (success) {
        successCounter.add(1);
    } else {
        failCounter.add(1);
        console.log(`❌ 실패: ${res.status}`);
        console.log(`Headers: ${JSON.stringify(res.headers)}`);
        console.log(`Body: ${res.body}`);
    }
}

export function handleSummary(data) {
    const successCount = data.metrics.successful_orders ? data.metrics.successful_orders.values.count : 0;
    const failCount = data.metrics.failed_orders ? data.metrics.failed_orders.values.count : 0;
    const avgDuration = data.metrics.order_duration ? data.metrics.order_duration.values.avg.toFixed(2) : 0;

    return {
        stdout: `
========================================
        분산락 테스트 결과
========================================
✅ 성공한 주문: ${successCount}
❌ 실패한 주문: ${failCount}
⏱️  평균 응답시간: ${avgDuration}ms
========================================

👉 검증 방법:
   1. DB에서 상품 ID ${PRODUCT_ID}의 재고 확인
   2. 초기재고 - ${successCount} = 남은 재고
   3. 분산락 정상 작동시 정확히 일치해야 함
========================================
`,
    };
}
