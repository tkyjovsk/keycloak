#!/bin/bash

docker ps -a -q -f name=performance | xargs docker kill || true
docker ps -a -q -f name=performance | xargs docker rm || true
docker network ls -q -f name=performance | xargs docker network rm || true
docker volume ls -q -f name=performance | xargs docker volume rm || true
