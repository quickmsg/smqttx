<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon clients">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.clients }}</div>
              <div class="stat-label">在线客户端</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon topics">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.topics }}</div>
              <div class="stat-label">活跃主题</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon messages">
              <el-icon><Message /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.messages }}</div>
              <div class="stat-label">消息总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rules">
              <el-icon><Setting /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.rules }}</div>
              <div class="stat-label">规则数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="系统性能">
          <div ref="performanceChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card title="消息流量">
          <div ref="messageChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="最近连接">
          <el-table :data="recentConnections" style="width: 100%">
            <el-table-column prop="clientId" label="客户端ID" />
            <el-table-column prop="ip" label="IP地址" />
            <el-table-column prop="connectTime" label="连接时间" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'online' ? 'success' : 'danger'">
                  {{ scope.row.status === 'online' ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const stats = ref({
  clients: 0,
  topics: 0,
  messages: 0,
  rules: 0
})

const recentConnections = ref([])
const performanceChart = ref()
const messageChart = ref()
let performanceChartInstance = null
let messageChartInstance = null

// 模拟数据
const mockData = () => {
  stats.value = {
    clients: Math.floor(Math.random() * 100) + 50,
    topics: Math.floor(Math.random() * 200) + 100,
    messages: Math.floor(Math.random() * 10000) + 5000,
    rules: Math.floor(Math.random() * 20) + 10
  }
  
  recentConnections.value = [
    {
      clientId: 'client_001',
      ip: '192.168.1.100',
      connectTime: '2024-01-15 10:30:00',
      status: 'online'
    },
    {
      clientId: 'client_002',
      ip: '192.168.1.101',
      connectTime: '2024-01-15 10:25:00',
      status: 'online'
    },
    {
      clientId: 'client_003',
      ip: '192.168.1.102',
      connectTime: '2024-01-15 10:20:00',
      status: 'offline'
    }
  ]
}

const initCharts = () => {
  // 性能图表
  performanceChartInstance = echarts.init(performanceChart.value)
  performanceChartInstance.setOption({
    title: { text: 'CPU & 内存使用率' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['CPU', '内存'] },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value', max: 100 },
    series: [
      {
        name: 'CPU',
        type: 'line',
        data: [30, 25, 40, 60, 80, 70, 45]
      },
      {
        name: '内存',
        type: 'line',
        data: [50, 55, 60, 65, 70, 75, 60]
      }
    ]
  })
  
  // 消息流量图表
  messageChartInstance = echarts.init(messageChart.value)
  messageChartInstance.setOption({
    title: { text: '消息吞吐量' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: [120, 200, 150, 80, 70, 110, 130]
      }
    ]
  })
}

onMounted(() => {
  console.log('=== Dashboard Component Mounted ===')
  console.log('Initializing dashboard...')
  
  mockData()
  console.log('Mock data initialized:', stats.value)
  
  initCharts()
  console.log('Charts initialized')
  
  // 定时更新数据
  const timer = setInterval(() => {
    mockData()
    if (performanceChartInstance && messageChartInstance) {
      performanceChartInstance.setOption({
        series: [
          {
            name: 'CPU',
            data: Array.from({length: 7}, () => Math.floor(Math.random() * 100))
          },
          {
            name: '内存',
            data: Array.from({length: 7}, () => Math.floor(Math.random() * 100))
          }
        ]
      })
      messageChartInstance.setOption({
        series: [
          {
            data: Array.from({length: 7}, () => Math.floor(Math.random() * 300))
          }
        ]
      })
    }
  }, 5000)
  
  onUnmounted(() => {
    clearInterval(timer)
    if (performanceChartInstance) {
      performanceChartInstance.dispose()
    }
    if (messageChartInstance) {
      messageChartInstance.dispose()
    }
  })
  
  console.log('Dashboard setup completed')
  console.log('===============================')
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.clients {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.topics {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.messages {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rules {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style> 