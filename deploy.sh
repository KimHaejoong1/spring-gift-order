#!/bin/bash

# 배포 스크립트
echo "=== Deploy Start ==="

# 1. 프로젝트 빌드
echo "1. Build Start"
./gradlew clean build -x test

# 2. 빌드된 JAR 파일 찾기
BUILD_PATH=$(find build/libs -name "*.jar" | head -1)
if [ -z "$BUILD_PATH" ]; then
    echo "no build jar file"
    exit 1
fi

JAR_NAME=$(basename $BUILD_PATH)
echo "JAR_NAME: $JAR_NAME"

# 3. 현재 실행 중인 프로세스 확인 및 종료
CURRENT_PID=$(grep -f $JAR_NAME)
if [ -z "$CURRENT_PID" ]; then
    echo "no process"
    sleep 1
else
    echo "processs exit.. (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# 4. 배포 디렉토리 설정 (로컬 테스트용)
DEPLOY_PATH="./deploy/"
if [ ! -d "$DEPLOY_PATH" ]; then
    echo "DEPLOY_PATH: $DEPLOY_PATH"
    mkdir -p $DEPLOY_PATH
fi

# 5. JAR 파일 복사
echo "JAR copy..."
cp $BUILD_PATH $DEPLOY_PATH

# 6. 애플리케이션 실행
cd $DEPLOY_PATH
echo "Application Start..."
nohup java -jar $JAR_NAME > app.log 2>&1 &

echo "Deploy Complete!"
echo "[log] tail -f $DEPLOY_PATH/app.log"
echo "Application Connect: http://localhost:8080"

echo "=== Deploy Complete ==="