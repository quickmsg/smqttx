#!/bin/bash

# 创建数据目录
mkdir -p ./data/node1 ./data/node2 ./data/node3

# 启动节点1
echo "Starting node 1..."
nohup java -Dsmqtt.config.path=config/node1.yaml \
  -jar smqttx-bootstrap/target/smqttx-bootstrap-2.0.9.jar \
  > node1.log 2>&1 &
NODE1_PID=$!

# 等待几秒让节点1启动
sleep 5

# 启动节点2
echo "Starting node 2..."
nohup java -Dsmqtt.config.path=config/node2.yaml \
  -jar smqttx-bootstrap/target/smqttx-bootstrap-2.0.9.jar \
  > node2.log 2>&1 &
NODE2_PID=$!

# 等待几秒让节点2启动
sleep 5

# 启动节点3
echo "Starting node 3..."
nohup java -Dsmqtt.config.path=config/node3.yaml \
  -jar smqttx-bootstrap/target/smqttx-bootstrap-2.0.9.jar \
  > node3.log 2>&1 &
NODE3_PID=$!

echo "All nodes started!"
echo "Node 1 PID: $NODE1_PID"
echo "Node 2 PID: $NODE2_PID"
echo "Node 3 PID: $NODE3_PID"

# 保存PID到文件以便后续停止
echo $NODE1_PID > node1.pid
echo $NODE2_PID > node2.pid
echo $NODE3_PID > node3.pid

echo "Cluster is running. Check logs for details."