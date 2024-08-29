#!/usr/bin/env bash

txtred='\033[1;31m'
txtlw='\033[1;33m'
txtpur='\033[1;35m'
txtgrn='\033[1;32m'
txtgrey='\033[1;30m'

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo -e "${txtpur} health check start"
echo -e "${txtpur} 배포된 포트: $IDLE_PORT"


sleep 10

for RETRY_COUNT in {1..10}
do
  HOST="http://localhost"
  RESPONSE=$(curl -s ${HOST}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then
    echo -e "${txtpur} health check 성공"

    switch_proxy
    break
  else
   echo -e "${txtpur} health check의 응답을 알 수 없거나 실행 상태가 아닙니다."
   echo -e "${txtpur} health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo -e "${txtpur} health check 실패"
    echo -e "${txtpur} 엔진엑스에 연결하지 않고 배포를 종료"
    exit 1
  fi
    echo -e "${txtpur} health check 연결 실패  재시도"
    sleep 10
  done

