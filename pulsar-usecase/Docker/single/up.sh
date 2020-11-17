#!/usr/bin/env bash


echo "Starting up infra"
source ./up-infra.sh "$@"
source ./create-topic.sh
echo "Starting up services"
source ./up-application.sh "$@" "all"

