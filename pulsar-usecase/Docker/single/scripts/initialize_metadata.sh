#!/bin/bash
bin/pulsar initialize-cluster-metadata \
  --cluster pulsar-cluster-1 \
  --zookeeper zookeeper:2181 \
  --configuration-store zookeeper:2181 \
  --web-service-url http://broker:8080 \
  --broker-service-url pulsar://broker:6650
