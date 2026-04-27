#!/bin/bash
# git commit 실행 전 리뷰 완료 여부 확인
# 리뷰 미완료 시 commit 차단

COMMAND=$(echo "$CLAUDE_TOOL_INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('command',''))" 2>/dev/null)

if echo "$COMMAND" | grep -q "git commit"; then
  REVIEW_MARKER="/Users/kimdohan/IdeaProjects/sagopalgo/.claude/.review-passed"
  if [ ! -f "$REVIEW_MARKER" ]; then
    echo "❌ 리뷰를 먼저 실행하세요 — git commit 차단됨"
    echo "코드 리뷰 완료 후 커밋이 가능합니다."
    exit 1
  fi
  rm -f "$REVIEW_MARKER"
fi

exit 0
