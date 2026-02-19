import http from 'k6/http';

/**
 * 환경별 BASE_URL 가져오기
 */
export function getBaseUrl() {
  return __ENV.BASE_URL || 'http://localhost:8080';
}

/**
 * 인증 헤더 생성
 */
export function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

/**
 * JSON 요청 헤더
 */
export function jsonHeaders() {
  return {
    'Content-Type': 'application/json',
  };
}

/**
 * 로그인 후 토큰 반환
 */
export function login(baseUrl, user) {
  const res = http.post(
    `${baseUrl}/api/users/login`,
    JSON.stringify({
      email: user.email,
      password: user.password,
    }),
    { headers: jsonHeaders() }
  );

  if (res.status === 200) {
    return res.json('accessToken');
  }
  return null;
}

/**
 * 배열에서 랜덤 요소 선택
 */
export function randomItem(array) {
  return array[Math.floor(Math.random() * array.length)];
}

/**
 * 범위 내 랜덤 정수
 */
export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * 범위 내 랜덤 sleep
 */
export function randomSleep(min, max) {
  const duration = Math.random() * (max - min) + min;
  return duration;
}

/**
 * 확률 체크 (0.0 ~ 1.0)
 */
export function chance(probability) {
  return Math.random() < probability;
}
