import request from '@/utils/request'

// 系统监控API - 与原始UI兼容
export const getSystemMetrics = () => {
  return request({
    url: '/smqtt/monitor/jvm',
    method: 'get'
  })
}

export const getCpuMetrics = () => {
  return request({
    url: '/smqtt/monitor/cpu',
    method: 'get'
  })
}

export const getCounterMetrics = () => {
  return request({
    url: '/smqtt/monitor/counter',
    method: 'get'
  })
}

export const getEventMetrics = () => {
  return request({
    url: '/smqtt/monitor/event',
    method: 'get'
  })
}

// 网络监控API - 新增的NetworkHttpActor支持
export const getNetworkMetrics = () => {
  return request({
    url: '/smqtt/monitor/network',
    method: 'get'
  })
}

// 可选的高级功能API - 如果将来需要可以启用
/*
// 实时日志API
export const getRealTimeLogs = (params) => {
  return request({
    url: '/smqtt/monitor/logs',
    method: 'get',
    params
  })
}

// 历史数据API
export const getHistoricalData = (params) => {
  return request({
    url: '/smqtt/monitor/history',
    method: 'get',
    params
  })
}

// 告警配置API
export const getAlerts = () => {
  return request({
    url: '/smqtt/monitor/alerts',
    method: 'get'
  })
}

export const createAlert = (data) => {
  return request({
    url: '/smqtt/monitor/alerts',
    method: 'post',
    data
  })
}

export const updateAlert = (id, data) => {
  return request({
    url: `/smqtt/monitor/alerts/${id}`,
    method: 'put',
    data
  })
}

export const deleteAlert = (id) => {
  return request({
    url: `/smqtt/monitor/alerts/${id}`,
    method: 'delete'
  })
}
*/ 