<template>
  <div class="messages-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>消息管理</span>
          <el-button type="primary" @click="refreshData">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="messages" style="width: 100%">
        <el-table-column prop="topic" label="主题" />
        <el-table-column prop="clientId" label="客户端ID" />
        <el-table-column prop="qos" label="QoS" />
        <el-table-column prop="timestamp" label="时间戳" />
        <el-table-column prop="payload" label="消息内容" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="viewMessage(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const messages = ref([
  {
    topic: 'sensor/temperature',
    clientId: 'client_001',
    qos: 1,
    timestamp: '2024-01-15 11:45:00',
    payload: '{"temperature": 25.6, "humidity": 60}'
  },
  {
    topic: 'sensor/humidity',
    clientId: 'client_002',
    qos: 0,
    timestamp: '2024-01-15 11:40:00',
    payload: '{"humidity": 65}'
  }
])

const refreshData = () => {
  console.log('刷新消息数据')
}

const viewMessage = (message) => {
  console.log('查看消息详情:', message)
}
</script>

<style scoped>
.messages-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 