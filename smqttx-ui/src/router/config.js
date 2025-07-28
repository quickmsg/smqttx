import TabsView from '@/layouts/tabs/TabsView'
import BlankView from '@/layouts/BlankView'
// import PageView from '@/layouts/PageView'

// 路由配置
const options = {
  routes: [
    {
      path: '/login',
      name: '登录页',
      component: () => import('@/pages/login')
    },
    {
      path: '*',
      name: '404',
      component: () => import('@/pages/exception/404'),
    },
    {
      path: '/403',
      name: '403',
      component: () => import('@/pages/exception/403'),
    },
    {
      path: '/',
      name: '首页',
      component: TabsView,
      redirect: '/login',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          meta: {
            icon: 'dashboard'
          },
          component: BlankView,
          children: [
            {
              path: 'console',
              name: '控制台',
              component: () => import('@/pages/dashboard/console'),
            },
            {
              path: 'connections',
              name: '连接信息',
              component: () => import('@/pages/dashboard/connections'),
            },
            {
              path: 'publish',
              name: '推送信息',
              component: () => import('@/pages/dashboard/publish'),
            },
            {
              path: 'acl',
              name: '访问控制',
              component: () => import('@/pages/dashboard/acl'),
            },
            {
              path: 'rule',
              name: '规则管控',
              component: () => import('@/pages/dashboard/acl'),
            }
            ,
            {
              path: 'datasource',
              name: '数据源管控',
              component: () => import('@/pages/dashboard/acl'),
            }

          ]
        }
      ]
    },
  ]
}

export default options
