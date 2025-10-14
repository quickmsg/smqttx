import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'clients',
        name: 'Clients',
        component: () => import('@/views/Clients.vue')
      },
      {
        path: 'topics',
        name: 'Topics',
        component: () => import('@/views/Topics.vue')
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/Messages.vue')
      },
      {
        path: 'rules',
        name: 'Rules',
        component: () => import('@/views/Rules.vue')
      },
      {
        path: 'monitoring',
        name: 'Monitoring',
        component: () => import('@/views/Monitoring.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/Settings.vue')
      }
    ]
  },
  // 添加通配符路由，处理 /index.html 等路径
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  console.log('=== Route Guard Debug ===')
  console.log('Route guard - to:', to.path, 'from:', from.path)
  console.log('Auth store - isAuthenticated:', authStore.isAuthenticated, 'token:', authStore.token)
  console.log('Route matched:', to.matched.map(route => route.path))
  console.log('Route meta:', to.meta)
  console.log('========================')
  
  // 处理 /index.html 路径
  if (to.path === '/index.html') {
    console.log('Redirecting /index.html to /')
    next('/')
    return
  }
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    console.log('Redirecting to login - requires auth but not authenticated')
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    console.log('Redirecting to dashboard - already authenticated')
    next('/')
  } else {
    console.log('Proceeding to:', to.path)
    next()
  }
})

// 添加路由解析后的钩子
router.afterEach((to, from) => {
  console.log('=== Route After Each ===')
  console.log('Navigation completed to:', to.path)
  console.log('Component loaded:', to.matched[to.matched.length - 1]?.components?.default?.name || 'Unknown')
  console.log('========================')
})

export default router 