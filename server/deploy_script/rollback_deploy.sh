#!/bin/bash

if [ $# -ne 2 ]; then
    echo "⚠️파라미터가 부족합니다.⚠️"
    echo "=> $0 serverIp containerName"
    exit 1
fi


serverIp=$1
containerName=$2

echo "새롭게 배포하려던 컨테이너 정지 및 삭제 on $serverIp..."
ssh -o StrictHostKeyChecking=no root@$serverIp "
    docker stop $containerName || true
    docker rm -f $containerName || true
"

# 백업 컨테이너가 있으면 재시작
backupExists=$(ssh -o StrictHostKeyChecking=no root@$serverIp "docker ps -a --filter name=${containerName}-backup -q")

if [ -n "$backupExists" ]; then
    echo "백업 컨테이너 재시작 on $serverIp..."
    작sh -o StrictHostKeyChecking=no root@$serverIp "
        docker rename ${containerName}-backup $containerName
        docker start $containerName
    "
    echo "✅ 롤백 성공 on $serverIp"
else
    echo "❌ 롤백 가능한 컨테이너 존재하지 않음. on $serverIp."
    exit 1
fi