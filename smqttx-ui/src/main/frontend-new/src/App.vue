<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  const currentPath = router.currentRoute.value.path
  const token = localStorage.getItem('smqttx_token')
  
  console.log('App mounted - current path:', currentPath, 'token exists:', !!token)
  
  // 处理 /index.html 路径重定向
  if (currentPath === '/index.html') {
    console.log('Redirecting /index.html to /')
    router.replace('/')
    return
  }
  
  // 检查是否需要重定向到登录页面
  if (!token && currentPath !== '/login') {
    console.log('No token found, redirecting to login')
    router.push('/login')
  } else if (token && currentPath === '/login') {
    console.log('Token found, redirecting to dashboard')
    router.push('/')
  }
})
</script>

<style>
#app {
  height: 100vh;
  width: 100vw;
}
</style> 