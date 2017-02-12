#!/usr/bin/env bash

executable=$1/redis/src/redis-server

for pid in $(pgrep -f ${executable}); do
    kill -15 ${pid}
done

eval ${executable} $1/config/redis.conf