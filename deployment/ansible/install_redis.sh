#!/usr/bin/env bash

cd $1

redis_directory=redis

if [ -d ${redis_directory} ]; then
    exit 0;
fi

archive_name=redis-stable.tar.gz

wget http://download.redis.io/redis-stable.tar.gz

tar -xzf ${archive_name}
rm ${archive_name}

mv redis-stable ${redis_directory}

cd ${redis_directory}
make