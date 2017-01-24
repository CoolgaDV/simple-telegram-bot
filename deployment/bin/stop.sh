#!/usr/bin/env bash

for pid in $(pgrep -f simple-telegram-bot.jar); do
    kill -15 ${pid}
done