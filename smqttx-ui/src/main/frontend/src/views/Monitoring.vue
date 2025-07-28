<template>
  <div class="monitoring-page">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card title="系统监控">
          <div ref="systemChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card title="网络监控">
          <div ref="networkChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="实时日志">
          <div class="log-container">
            <div v-for="log in logs" :key="log.id" class="log-item">
              <span class="log-time">{{ log.time }}</span>
              <span class="log-level" :class="log.level">{{ log.level }}</span>
              <span class="log-message">{{ log.message }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const systemChart = ref()
const networkChart = ref()
let systemChartInstance = null
let networkChartInstance = null

const logs = ref([
  { id: 1, time: '11:45:00', level: 'INFO', message: '客户端 client_001 连接成功' },
  { id: 2, time: '11:44:30', level: 'WARN', message: '主题 sensor/temperature 消息队列已满' },
  { id: 3, time: '11:44:00', level: 'INFO', message: '规则引擎执行成功' }
])

const initCharts = () => {
  // 系统监控图表
  systemChartInstance = echarts.init(systemChart.value)
  systemChartInstance.setOption({
    title: { text: '系统资源使用率' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['CPU', '内存', '磁盘'] },
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
      },
      {
        name: '磁盘',
        type: 'line',
        data: [20, 22, 25, 28, 30, 32, 25]
      }
    ]
  })
  
  // 网络监控图表
  networkChartInstance = echarts.init(networkChart.value)
  networkChartInstance.setOption({
    title: { text: '网络流量' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['入站', '出站'] },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '入站',
        type: 'line',
        data: [120, 200, 150, 80, 70, 110, 130]
      },
      {
        name: '出站',
        type: 'line',
        data: [100, 180, 140, 70, 60, 100, 120]
      }
    ]
  })
}

onMounted(() => {
  initCharts()
  
  // 模拟实时日志
  const logTimer = setInterval(() => {
    const levels = ['INFO', 'WARN', 'ERROR']
    const messages = [
      '客户端连接成功',
      '消息发送成功',
      '规则执行完成',
      '系统资源使用率过高',
      '网络连接异常'
    ]
    
    const newLog = {
      id: Date.now(),
      time: new Date().toLocaleTimeString(),
      level: levels[Math.floor(Math.random() * levels.length)],
      message: messages[Math.floor(Math.random() * messages.length)]
    }
    
    logs.value.unshift(newLog)
    if (logs.value.length > 50) {
      logs.value.pop()
    }
  }, 3000)
  
  onUnmounted(() => {
    clearInterval(logTimer)
    if (systemChartInstance) {
      systemChartInstance.dispose()
    }
    if (networkChartInstance) {
      networkChartInstance.dispose()
    }
  })
})
</script>

<style scoped>
.monitoring-page {
  padding: 20px;
}

.log-container {
  height: 300px;
  overflow-y: auto;
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}

.log-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  font-family: monospace;
  font-size: 12px;
}

.log-time {
  color: #666;
  margin-right: 10px;
  min-width: 80px;
}

.log-level {
  padding: 2px 6px;
  border-radius: 3px;
  margin-right: 10px;
  min-width: 50px;
  text-align: center;
  font-weight: bold;
}

.log-level.INFO {
  background: #e1f3d8;
  color: #67c23a;
}

.log-level.WARN {
  background: #fdf6ec;
  color: #e6a23c;
}

.log-level.ERROR {
  background: #fef0f0;
  color: #f56c6c;
}

.log-message {
  flex: 1;
  color: #333;
}
</style> 