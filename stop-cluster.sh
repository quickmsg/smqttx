#!/bin/bash

# 停止节点1
if [ -f node1.pid ]; then
    NODE1_PID=$(cat node1.pid)
    echo "Stopping node 1 (PID: $NODE1_PID)..."
    kill $NODE1_PID
    rm node1.pid
fi

# 停止节点2
if [ -f node2.pid ]; then
    NODE2_PID=$(cat node2.pid)
    echo "Stopping node 2 (PID: $NODE2_PID)..."
    kill $NODE2_PID
    rm node2.pid
fi

# 停止节点3
if [ -f node3.pid ]; then
    NODE3_PID=$(cat node3.pid)
    echo "Stopping node 3 (PID: $NODE3_PID)..."
    kill $NODE3_PID
    rm node3.pid
fi

echo "All nodes stopped."