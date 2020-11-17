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
    ymlFile='docker-compose.event-queue.command-queue.yml'
fi

if [ "$COMMANDQUEUE" = true ]; then
    ymlFile='docker-compose.event-queue.command-queue.yml'
fi

if [ "$EVENTREPLAY" = true ]; then
    ymlFile='docker-compose.event-replay.yml'
fi
echo "Use the yml file: ${ymlFile}"
docker-compose -f ${ymlFile} up -d zookeeper broker schema-registry connect cli
while [[ $(docker inspect -f {{.State.Health.Status}} broker) != *healthy* ]]; do
    echo -ne "\r\033[0KWaiting for broker to be healthy";
    sleep 1
    echo -n "."
    sleep 1
    echo -n "."
    sleep 1
    echo -n "."
done
echo "Broker is now fully started"


