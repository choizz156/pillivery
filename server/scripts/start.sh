#!/usr/bin/env bash
txtred='\033[1;31m'
txtlw='\033[1;33m'
txtpur='\033[1;35m'
txtgrn='\033[1;32m'
txtgrey='\033[1;30m'

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/Users/choi/pillivery

echo -e "${txtpur} >> 디렉토리 이동"
cd $REPOSITORY/server

IDLE_PROFILE=$(find_idle_profile)

echo -e "find_idle_profile e=$IDLE_PROFILE 로 실행"

if [ "${IDLE_PROFILE}" == "real1" ]; then
   docker compose -f test.compose.yml up --no-deps app1
else
   docker compose -f test.compose.yml up --no-deps app2
fi

