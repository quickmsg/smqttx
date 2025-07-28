<template>
  <div class="rules-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>规则引擎</span>
          <el-button type="primary" @click="addRule">添加规则</el-button>
        </div>
      </template>
      
      <el-table :data="rules" style="width: 100%">
        <el-table-column prop="name" label="规则名称" />
        <el-table-column prop="topic" label="监听主题" />
        <el-table-column prop="action" label="执行动作" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
              {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editRule(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const rules = ref([
  {
    name: '温度监控规则',
    topic: 'sensor/temperature',
    action: 'HTTP POST',
    status: 'enabled',
    createTime: '2024-01-15 10:00:00'
  },
  {
    name: '湿度监控规则',
    topic: 'sensor/humidity',
    action: 'Database Insert',
    status: 'enabled',
    createTime: '2024-01-15 09:30:00'
  }
])

const addRule = () => {
  console.log('添加规则')
}

const editRule = (rule) => {
  console.log('编辑规则:', rule)
}

const deleteRule = (rule) => {
  console.log('删除规则:', rule)
}
</script>

<style scoped>
.rules-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 