#!/usr/bin/env bash

source ./parseUseCase.sh

PUBSUB=false
EVENTQUEUE=false
COMMANDQUEUE=false
EVENTREPLAY=false
READER=false


parseCmd "$@"
retval=$?
ymlFile=''
if [ $retval != 0 ]; then
    exit $retval
fi

if [ "$PUBSUB" = true ]; then
    ymlFile='docker-compose.pub-sub.yml'
fi

if [ "$EVENTQUEUE" = true ]; then
    ymlFile='docker-compose.event-queue.yml'
fi

if [ "$COMMANDQUEUE" = true ]; then
    ymlFile='docker-compose.command-queue.yml'
fi

if [ "$EVENTREPLAY" = true ]; then
    ymlFile='docker-compose.event-replay.yml'
fi
echo "Use the yml file: ${ymlFile}"
docker-compose -f ${ymlFile} build