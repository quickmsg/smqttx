<template>
  <div class="clients-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户端管理</span>
          <el-button type="primary" @click="refreshData">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="clients" style="width: 100%">
        <el-table-column prop="clientId" label="客户端ID" />
        <el-table-column prop="ip" label="IP地址" />
        <el-table-column prop="port" label="端口" />
        <el-table-column prop="connectTime" label="连接时间" />
        <el-table-column prop="lastMessageTime" label="最后消息时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'online' ? 'success' : 'danger'">
              {{ scope.row.status === 'online' ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
            <el-button size="small" type="danger" @click="disconnectClient(scope.row)">断开</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const clients = ref([
  {
    clientId: 'client_001',
    ip: '192.168.1.100',
    port: 1883,
    connectTime: '2024-01-15 10:30:00',
    lastMessageTime: '2024-01-15 11:45:00',
    status: 'online'
  },
  {
    clientId: 'client_002',
    ip: '192.168.1.101',
    port: 1883,
    connectTime: '2024-01-15 10:25:00',
    lastMessageTime: '2024-01-15 11:40:00',
    status: 'online'
  }
])

const refreshData = () => {
  // 刷新数据逻辑
  console.log('刷新客户端数据')
}

const viewDetails = (client) => {
  console.log('查看客户端详情:', client)
}

const disconnectClient = (client) => {
  console.log('断开客户端连接:', client)
}
</script>

<style scoped>
.clients-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 