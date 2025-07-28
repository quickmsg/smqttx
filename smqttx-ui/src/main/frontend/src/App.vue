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
  // 检查是否需要重定向到登录页面
  const token = localStorage.getItem('smqttx_token')
  if (!token && router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
})
</script>

<style>
#app {
  height: 100vh;
  width: 100vw;
}
</style> 