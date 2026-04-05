#!/usr/bin/env bash
# EC2(Amazon Linux 2 등)에서 프로젝트 루트에 두고 실행: chmod +x deploy.sh && ./deploy.sh
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p logs

if [ -d .git ]; then
  git pull origin main || git pull origin master || true
fi

./gradlew bootJar -x test
JAR="$(ls build/libs/*.jar | head -1)"

pkill -f "${JAR}" 2>/dev/null || pkill -f 'kiosk-.*\.jar' 2>/dev/null || true
sleep 2

nohup java -jar "${JAR}" \
  --spring.profiles.active=prod \
  >> logs/app.log 2>&1 &
echo "배포 완료. 로그: $(pwd)/logs/app.log"
echo "프로필 prod 사용 — RDS·JWT_SECRET 등 환경변수가 설정되어 있어야 합니다."
