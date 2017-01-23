#!/usr/bin/env bash

nohup $JAVA_HOME/bin/java \
      -Xms128m -Xmx128m \
      -Dlogging.config=file:../config/logback.xml \
      -jar simple-telegram-bot.jar \
      --spring.config.location=../config/application.yml \
      </dev/null >/dev/null 2>&1 &