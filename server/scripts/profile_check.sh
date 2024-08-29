#!/usr/bin/env bash

HOST="http://host.docker.internal"
RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" ${HOST}/profile)

if [ ${RESPONSE_CODE} -ge 400 ]; then
  CURRENT_PROFILE=real2
else
  CURRENT_PROFILE=$(curl -s ${HOST}/profile)
fi

if [ "${CURRENT_PROFILE}" == "real1" ]; then
  IDLE_PROFILE=real2
else
  IDLE_PROFILE=real1
fi
echo "${IDLE_PROFILE}"
