#!/usr/bin/env bash

#Create necessary topics
echo "Create test topic for pub-sub use case"
winpty docker exec cli kafka-topics --create --topic pub-sub-test --bootstrap-server broker:9092

echo "Create test topic for command-queue and event-queue use case"
winpty docker exec cli kafka-topics --create --topic event-command-queue-test --partitions 3 --bootstrap-server broker:9092


echo "Create test topic for event-replay use case"
winpty docker exec cli kafka-topics --create --topic event-replay-test --partitions 2 --bootstrap-server broker:9092

