# sagopalgo 프로젝트 핵심 규칙

## 테스트 & 타입
- 커버리지 100% 필수 (lines / functions / branches / statements)
- 커밋 전 반드시 `./gradlew test` + `./gradlew build` 통과 확인

## 코드 리뷰 (필수 — PreToolUse hook으로 커밋 차단됨)
- 코드 수정 + 테스트 통과 후, 커밋 전에 자동으로 리뷰 실행
- `.claude/agents/reviewer.md` 파일을 읽고 그 지침을 따르는 general-purpose Agent를 실행
- 사용자가 요청하지 않아도 반드시 리뷰 먼저 진행
- **리뷰 통과(P1 없음) 시 반드시 마커 파일 생성:**
  ```bash
  touch /Users/kimdohan/IdeaProjects/sagopalgo/.claude/.review-passed
  ```
- 마커가 없으면 `git commit`이 PreToolUse hook에 의해 차단됨
- 리뷰 실행 방법:
  ```
  Agent({
    description: "코드 리뷰",
    prompt: "먼저 /Users/kimdohan/IdeaProjects/sagopalgo/.claude/agents/reviewer.md 를 읽고 그 지침을 정확히 따라 리뷰하세요. git diff로 변경사항을 확인하고 [1]~[12] 체크리스트를 모두 적용하세요."
  })
  ```

## 브랜치 & 커밋
- 모든 작업 브랜치는 `dev` 기준으로 생성
- 브랜치명: `feat/#이슈번호-기능명`, `fix/#이슈번호-버그명`
- 커밋 메시지: `<type>(#이슈번호): <제목>`
- Co-Authored-By 절대 금지

## PR
- base 브랜치는 반드시 `dev`
- 본문에 `Closes #이슈번호` 포함
- --label, --assignee "handokei" 필수 포함

## dev 브랜치 동기화
- 항상 `git fetch origin && git merge origin/dev` 사용
- 로컬 dev 직접 merge 금지
