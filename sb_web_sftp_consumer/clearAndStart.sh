#!/usr/bin/env bash

# to delete all volumes:
# docker rm $(docker ps -aq) && docker volume rm $(docker volume ls -q) && docker image rm sb-sftp-app:latest

# initial run: `mvn clean install && docker compose up`

docker container rm sb-sftp-app && \
  docker image rm sb-sftp-app:latest && \
  mvn clean install && \
  docker compose up
