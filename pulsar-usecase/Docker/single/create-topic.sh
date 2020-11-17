#!/usr/bin/env bash

#Create necessary topics
echo "Create necessary test topics: 'non-partition-test-topic' and 'test-topic' with 3 partitions with no message retention by default"
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 topics create persistent://public/default/non-partitioned-test-topic
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 topics create-partitioned-topic persistent://public/default/test-topic -p 3
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 topics list public/default/

echo "Create another namespace 'retained' for topics with retention policy"
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 namespaces create public/retained

echo "Set retention policy for this namespace 'retained' to infinite"
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 namespaces set-retention public/retained --size -1 --time -1

echo "Create a non-partition under this namespace (Event replay only works with non-paritioned topic)"
winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 topics create persistent://public/retained/non-partitioned-test-topic

winpty docker exec cli bin/pulsar-admin --admin-url http://broker:8080 topics list public/retained/

