import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('smqttx_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('smqttx_user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  const login = async (credentials) => {
    try {
      const response = await loginApi(credentials)
      token.value = response.token
      user.value = response.user
      
      localStorage.setItem('smqttx_token', token.value)
      localStorage.setItem('smqttx_user', JSON.stringify(user.value))
      
      return { success: true }
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