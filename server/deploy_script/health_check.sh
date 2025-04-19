#!/bin/bash

if [ "$#" -ne 4 ]; then
    echo "⚠️ 파라미터가 부족합니다. ⚠️"
    echo "=> $0 serverIp containerName healthCheckUrl maxAttempts sleepInterval"
    exit 1
fi

serverIp=$1
containerName=$2
healthCheckUrl=$3
maxAttempts=$4
sleepInterval=$5

attempts=0
healthCheckSuccess=false

while [ $attempts -lt $maxAttempts ]; do
    httpCode=$(ssh -o StrictHostKeyChecking=no root@$serverIp "curl -s -o /dev/null -w \"%{http_code}\" $healthCheckUrl")
    echo "Health check 횟수 $((attempts + 1)). HTTP Status: $httpCode"
    
    if [ "$httpCode" == "200" ]; then
        echo "✅ CD 완료 $serverIp."
        healthCheckSuccess=true
        break
    fi
    
    attempts=$((attempts + 1))
    sleep $sleepInterval
done

if [ "$healthCheckSuccess" == "false" ]; then
    echo "❌ Health check 실패 : $serverIp."
    exit 1
else
    ssh -o StrictHostKeyChecking=no root@$serverIp "
        docker rm -f ${containerName}-backup || true
    "
    echo "✅ 배포 성공 : $serverIp"
fi