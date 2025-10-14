import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 安全地获取token
  const tokenValue = localStorage.getItem('smqttx_token')
  console.log('Initial tokenValue from localStorage:', tokenValue)
  // 确保token是有效的，不是"undefined"或其他无效值
  const validToken = tokenValue && tokenValue !== 'undefined' && tokenValue.trim() !== '' ? tokenValue : ''
  const token = ref(validToken)
  
  // 安全地解析用户数据
  let userData = null
  try {
    const userStr = localStorage.getItem('smqttx_user')
    console.log('Initial userStr from localStorage:', userStr)
    if (userStr) {
      userData = JSON.parse(userStr)
    }
  } catch (error) {
    console.error('Error parsing user data from localStorage:', error)
    localStorage.removeItem('smqttx_user') // 清除损坏的数据
  }
  
  const user = ref(userData)

  const isAuthenticated = computed(() => {
    // 确保token是有效的非空字符串，不是"undefined"或其他无效值
    const isValidToken = token.value && token.value !== 'undefined' && token.value.trim() !== ''
    console.log('isAuthenticated computed - token.value:', token.value, 'isValidToken:', isValidToken)
    return isValidToken
  })

  const login = async (credentials) => {
    try {
      const response = await loginApi(credentials)
      
      if (response.success && response.data) {
        // 后端返回格式: { success: true, data: { access_token: "...", expires_in: ... } }
        token.value = response.data.access_token
        user.value = {
          username: credentials.username,
          expiresIn: response.data.expires_in
        }
        
        localStorage.setItem('smqttx_token', token.value)
        localStorage.setItem('smqttx_user', JSON.stringify(user.value))
        
        return { success: true }
      } else {
        return { success: false, error: '登录失败' }
      }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('smqttx_token')
    localStorage.removeItem('smqttx_user')
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout
  }
}) 