<template>
  <div class="admin-layout">
    <div class="admin-header">
      <h1>银行存款产品服务编排管理平台</h1>
      <div class="header-actions">
        <span class="welcome-text">欢迎，{{ userInfo.realName }}</span>
        <button @click="logout" class="btn-logout">退出登录</button>
      </div>
    </div>
    
    <div class="admin-container">
      <div class="admin-sidebar">
        <nav class="admin-nav">
          <router-link to="/admin/services" class="nav-item">
            <span class="nav-icon">🔧</span>
            <span class="nav-text">原子服务管理</span>
          </router-link>
          <router-link to="/admin/products" class="nav-item">
            <span class="nav-icon">📦</span>
            <span class="nav-text">产品属性配置</span>
          </router-link>
          <router-link to="/admin/workflows" class="nav-item">
            <span class="nav-icon">🔄</span>
            <span class="nav-text">流程编排管理</span>
          </router-link>
          <router-link to="/admin/orders" class="nav-item">
            <span class="nav-icon">📋</span>
            <span class="nav-text">订单管理</span>
          </router-link>
          <router-link to="/admin/users" class="nav-item">
            <span class="nav-icon">👥</span>
            <span class="nav-text">用户管理</span>
          </router-link>
          <router-link to="/admin/logs" class="nav-item">
            <span class="nav-icon">📝</span>
            <span class="nav-text">日志管理</span>
          </router-link>
        </nav>
      </div>
      
      <div class="admin-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userInfo = ref({
  realName: ''
})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})

const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.admin-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.btn-link {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-link:hover {
  background: rgba(255,255,255,0.2);
}

.welcome-text {
  color: white;
  margin-right: 20px;
  font-size: 14px;
}

.btn-logout {
  background: rgba(255,255,255,0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: rgba(255,255,255,0.3);
}

.admin-container {
  display: flex;
  max-width: 1400px; /* 减小最大宽度 */
  margin: 20px auto;
  gap: 15px; /* 减小间距 */
  padding: 0 15px;
}

.admin-sidebar {
  width: 180px; /* 减小侧边栏宽度从240px到180px */
  background: white;
  border-radius: 8px;
  padding: 15px 0; /* 调整内边距 */
  height: fit-content;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  flex-shrink: 0;
}

.admin-nav {
  display: flex;
  flex-direction: column;
  gap: 2px; /* 减小导航项间距 */
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px; /* 减小图标和文字间距 */
  padding: 10px 15px; /* 调整内边距 */
  color: #666;
  text-decoration: none;
  transition: all 0.3s;
  font-size: 13px; /* 减小字体大小 */
}

.nav-item:hover {
  background: #f5f5f5;
  color: #667eea;
}

.nav-item.router-link-active {
  background: linear-gradient(90deg, rgba(102,126,234,0.1) 0%, transparent 100%);
  color: #667eea;
  border-right: 3px solid #667eea;
}

.nav-icon {
  font-size: 18px; /* 调整图标大小 */
}

.nav-text {
  font-size: 13px; /* 调整文字大小 */
  font-weight: 500;
}

.admin-content {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 20px;
  min-height: 600px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow-x: auto;
}
</style>