#!/bin/bash

set -e

PROCESS_MARKER=LOGB_NODE_MARKER

echo "Killing existent instances of logb-node (if any)..."
if [ "$(uname)" == "Darwin" ]; then
    ps aux | grep ${PROCESS_MARKER} | grep -v grep | awk '{print $2}' | xargs kill
else
    ps aux | grep ${PROCESS_MARKER} | grep -v grep | awk '{print $2}' | xargs -r kill
fi
sleep 5 # just to ensure that port cleared be start again

echo "Removing old port file..."
rm -f logb-node.port

echo "Starting logb-node..."
nohup java -Dprocess_marker=${PROCESS_MARKER} -jar logb-node.jar > logb-node.out 2>&1 &

echo "Waiting logb-node port publishing..."
sleep 5
node_port=`cat logb-node.port`

echo "logb-node started at port $node_port"