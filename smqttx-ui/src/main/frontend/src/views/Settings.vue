<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      
      <el-form :model="settings" label-width="120px">
        <el-form-item label="MQTT端口">
          <el-input v-model="settings.mqttPort" type="number" />
        </el-form-item>
        
        <el-form-item label="WebSocket端口">
          <el-input v-model="settings.wsPort" type="number" />
        </el-form-item>
        
        <el-form-item label="HTTP端口">
          <el-input v-model="settings.httpPort" type="number" />
        </el-form-item>
        
        <el-form-item label="最大连接数">
          <el-input v-model="settings.maxConnections" type="number" />
        </el-form-item>
        
        <el-form-item label="消息队列大小">
          <el-input v-model="settings.messageQueueSize" type="number" />
        </el-form-item>
        
        <el-form-item label="启用认证">
          <el-switch v-model="settings.enableAuth" />
        </el-form-item>
        
        <el-form-item label="启用SSL">
          <el-switch v-model="settings.enableSSL" />
        </el-form-item>
        
        <el-form-item label="日志级别">
          <el-select v-model="settings.logLevel">
            <el-option label="DEBUG" value="DEBUG" />
            <el-option label="INFO" value="INFO" />
            <el-option label="WARN" value="WARN" />
            <el-option label="ERROR" value="ERROR" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const settings = ref({
  mqttPort: 1883,
  wsPort: 8999,
  httpPort: 60000,
  maxConnections: 10000,
  messageQueueSize: 1000,
  enableAuth: true,
  enableSSL: false,
  logLevel: 'INFO'
})

const saveSettings = () => {
  ElMessage.success('设置保存成功')
  console.log('保存设置:', settings.value)
}

const resetSettings = () => {
  settings.value = {
    mqttPort: 1883,
    wsPort: 8999,
    httpPort: 60000,
    maxConnections: 10000,
    messageQueueSize: 1000,
    enableAuth: true,
    enableSSL: false,
    logLevel: 'INFO'
  }
  ElMessage.info('设置已重置')
}
</script>

<style scoped>
.settings-page {
  padding: 20px;
}
</style> 