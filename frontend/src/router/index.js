import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  // 登录页面
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/ProductList.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/product/:code',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/purchase/:code',
    name: 'PurchasePage',
    component: () => import('@/views/PurchasePage.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/success',
    name: 'PurchaseSuccess',
    component: () => import('@/views/PurchaseSuccess.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue')
  },
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('@/views/ProfileEdit.vue')
  },
  {
    path: '/security',
    name: 'SecuritySetting',
    component: () => import('@/views/SecuritySetting.vue')
  },
  {
    path: '/messages',
    name: 'MessageCenter',
    component: () => import('@/views/MessageCenter.vue')
  },
  {
    path: '/help',
    name: 'HelpCenter',
    component: () => import('@/views/HelpCenter.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/order/:orderNo',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  {
    path: '/assets',
    name: 'AssetsOverview',
    component: () => import('@/views/AssetsOverview.vue'),
    meta: { requiresAuth: true, role: 'USER' }
  },
  // 管理平台路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/services',
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'services',
        name: 'ServiceManage',
        component: () => import('@/views/admin/ServiceManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'products',
        name: 'ProductManage',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'workflows',
        name: 'WorkflowManage',
        component: () => import('@/views/admin/WorkflowManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'orders',
        name: 'OrderManage',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      },
      {
        path: 'logs',
        name: 'LogManage',
        component: () => import('@/views/admin/LogManage.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  
  // 需要登录的页面
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token || !userInfo) {
      next('/login')
      return
    }
    
    const user = JSON.parse(userInfo)
    const requiredRole = to.meta.role
    
    // 检查角色权限
    if (requiredRole && user.role !== requiredRole) {
      alert('权限不足，无法访问该页面')
      if (user.role === 'ADMIN') {
        next('/admin')
      } else {
        next('/products')
      }
      return
    }
  }
  
  // 已登录用户访问登录页，跳转到对应首页
  if (to.path === '/login' && token && userInfo) {
    const user = JSON.parse(userInfo)
    if (user.role === 'ADMIN') {
      next('/admin')
    } else {
      next('/products')
    }
    return
  }
  
  next()
})

export default router