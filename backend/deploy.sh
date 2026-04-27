#!/usr/bin/env bash
# EC2(Amazon Linux 2 ???먯꽌 ?꾨줈?앺듃 猷⑦듃???먭퀬 ?ㅽ뻾: chmod +x deploy.sh && ./deploy.sh
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p logs

if [ -d .git ]; then
  git pull origin main || git pull origin master || true
fi

./gradlew bootJar -x test
JAR="$(ls build/libs/*.jar | head -1)"

pkill -f "${JAR}" 2>/dev/null || pkill -f 'portal-.*\.jar' 2>/dev/null || true
sleep 2

nohup java -jar "${JAR}" \
  --spring.profiles.active=prod \
  >> logs/app.log 2>&1 &
echo "諛고룷 ?꾨즺. 濡쒓렇: $(pwd)/logs/app.log"
echo "?꾨줈??prod ?ъ슜 ??RDS쨌JWT_SECRET ???섍꼍蹂?섍? ?ㅼ젙?섏뼱 ?덉뼱???⑸땲??"
