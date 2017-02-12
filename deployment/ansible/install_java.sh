#!/usr/bin/env bash

cd $1

java_directory=java

if [ -d ${java_directory} ]; then
    exit 0;
fi

archive_name=jdk-8u112-linux-x64.tar.gz

wget --no-check-certificate --no-cookies --header \
     "Cookie: oraclelicense=accept-securebackup-cookie" \
     http://download.oracle.com/otn-pub/java/jdk/8u112-b15/${archive_name}

tar -xzf ${archive_name}
rm ${archive_name}

mv jdk1.8.0_112 ${java_directory}