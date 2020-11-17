#!/usr/bin/env bash

source ./parseUseCase.sh

PUBSUB=false
EVENTQUEUE=false
COMMANDQUEUE=false
EVENTREPLAY=false
PRODUCER=false
CONSUMER1=false
CONSUMER2=false
READER=false
ALL=false



parseCmd "$@"
retval=$?
ymlFile=''
services=''
if [ $retval != 0 ]; then
    exit $retval
fi


if [ "$PUBSUB" = true ]; then
    ymlFile='docker-compose.pub-sub.yml'
    services='producer consumer-1 consumer-2'
fi

if [ "$EVENTQUEUE" = true ]; then
    ymlFile='docker-compose.event-queue.command-queue.yml'
    services='producer consumer-1 consumer-2'
fi

if [ "$COMMANDQUEUE" = true ]; then
    ymlFile='docker-compose.event-queue.command-queue.yml'
    services='producer consumer-1 consumer-2'
fi

if [ "$EVENTREPLAY" = true ]; then
    ymlFile='docker-compose.event-replay.yml'
    services='producer consumer-1 consumer-2'
fi

echo "Use the yml file: ${ymlFile}"

if [ "$PRODUCER" = true ]; then
    docker-compose -f ${ymlFile} up producer
fi

if [ "$CONSUMER1" = true ]; then
    docker-compose -f ${ymlFile} up consumer-1
fi
if [ "$CONSUMER2" = true ]; then
    docker-compose -f ${ymlFile} up consumer-2
fi
if [ "$ALL" = true ]; then
    docker-compose -f ${ymlFile} up ${services}
fi







