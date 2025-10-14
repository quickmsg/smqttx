<template>
  <div class="topics-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>主题管理</span>
          <el-button type="primary" @click="refreshData">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="topics" style="width: 100%">
        <el-table-column prop="topic" label="主题" />
        <el-table-column prop="subscribers" label="订阅者数量" />
        <el-table-column prop="messages" label="消息数量" />
        <el-table-column prop="lastMessageTime" label="最后消息时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
            <el-button size="small" type="danger" @click="deleteTopic(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const topics = ref([
  {
    topic: 'sensor/temperature',
    subscribers: 5,
    messages: 1200,
    lastMessageTime: '2024-01-15 11:45:00'
  },
  {
    topic: 'sensor/humidity',
    subscribers: 3,
    messages: 800,
    lastMessageTime: '2024-01-15 11:40:00'
  }
])

const refreshData = () => {
  console.log('刷新主题数据')
}

const viewDetails = (topic) => {
  console.log('查看主题详情:', topic)
}

const deleteTopic = (topic) => {
  console.log('删除主题:', topic)
}
</script>

<style scoped>
.topics-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 