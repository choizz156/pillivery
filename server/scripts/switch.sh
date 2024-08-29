#!/usr/bin/env bash
txtred='\033[1;31m'
txtlw='\033[1;33m'
txtpur='\033[1;35m'
txtgrn='\033[1;32m'
txtgrey='\033[1;30m'

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

function switch_proxy() {
  IDLE_PORT=$(find_idle_port)

  echo -e "${txtpur} 전환할 port: $IDLE_PORT"
  echo -e "${txtpur} port 전환"

  SERVICE1=$(docker ps --filter "name=app" --format '{{.Names}} {{.CreatedAt}}' | sort -k2,3 | tail -n 2 | awk '{print $1}' | head -n 1)
  SERVICE2=$(docker ps --filter "name=app" --format '{{.Names}} {{.CreatedAt}}' | sort -k2,3 | tail -n 1 | awk '{print $1}')


docker exec -i nginx /bin/bash <<EOF
   echo "set \\\$service_url http://app:${IDLE_PORT};" |
   tee /etc/nginx/conf.d/service-url.inc
   nginx -s reload
EOF

  echo -e "${txtpur} nginx reload"

  if [ ${IDLE_PORT} == 8081 ]; then
    EXPIRE_PORT=8082
  else
    EXPIRE_PORT=8081
  fi

  IDLE_PID=$(lsof -i :${EXPIRE_PORT} -t)

  if [ -z ${IDLE_PID} ]; then
    echo -e "${txtgrn} >> 해당 포트에서 구동 중인 애플리케이션이 없습니다."
  else
   CONTAINER_NAME=$(docker ps --format '{{.Names}}' | grep ${EXPIRE_PORT})
   docker stop ${CONTAINER_NAME}
   sleep 3
    echo -e "${txtpur} 구동 종료"
  fi
}
