#!/bin/bash

if [ "$#" -ne 4 ]; then
    echo "⚠️파라미터가 부족합니다.⚠️"
    echo "=> $0 serverIp containerName registryUrl imageTag"
    exit 1
fi

serverIp=$1
containerName=$2
registryUrl=$3
imageTag=$4

echo "배포 과정 시작: $serverIp..."

ssh -o StrictHostKeyChecking=no root@$serverIp "
    if docker ps -q --filter name=$containerName; then
        docker rm -f ${containerName}-backup || true
            echo '기존 백업 컨테이너 삭제'
        docker rename $containerName ${containerName}-backup
            echo '백업 컨테이너 설정'
        docker stop ${containerName}-backup || true
            echo  "기존 컨테이너 종료"
    fi
"

echo "새로운 컨테이너 배포"
ssh -o StrictHostKeyChecking=no root@$serverIp "
    docker pull ${registryUrl}:${imageTag}

    echo "컨테이너 배포 시작"
    docker run -d \
         --name $containerName \
         --restart unless-stopped \
         --network server \
         -p 8080:8080 \
         ${registryUrl}:${imageTag}
"

echo "✅ 배포 완료: $serverIp"