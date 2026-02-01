<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>银行存款产品服务编排系统</h1>
        <p class="subtitle">{{ isLogin ? '用户登录' : '用户注册' }}</p>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="请输入用户名"
            class="form-input"
            required
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            class="form-input"
            required
          />
        </div>

        <div v-if="!isLogin">
          <div class="form-group">
            <label>手机号</label>
            <input 
              v-model="form.phone" 
              type="text" 
              placeholder="请输入手机号"
              class="form-input"
              required
            />
          </div>

          <div class="form-group">
            <label>邮箱</label>
            <input 
              v-model="form.email" 
              type="email" 
              placeholder="请输入邮箱"
              class="form-input"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="submit-btn">
            {{ isLogin ? '登录' : '注册' }}
          </button>
        </div>
      </form>

      <div class="toggle-mode">
        <p>
          {{ isLogin ? '还没有账号？' : '已有账号？' }}
          <a href="#" @click="toggleMode">
            {{ isLogin ? '立即注册' : '立即登录' }}
          </a>
        </p>
      </div>

      <!-- 测试账号信息 -->
      <div class="test-accounts" v-if="isLogin">
        <h3>测试账号</h3>
        <div class="account-item">
          <strong>管理员：</strong>admin / admin123
        </div>
        <div class="account-item">
          <strong>普通用户：</strong>testuser / user123
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useRouter } from 'vue-router'

const API_BASE = 'http://localhost:8080/api'

export default {
  name: 'Login',
  data() {
    return {
      isLogin: true,
      form: {
        username: '',
        password: '',
        phone: '',
        email: ''
      }
    }
  },
  methods: {
    toggleMode() {
      this.isLogin = !this.isLogin
      this.form = {
        username: '',
        password: '',
        phone: '',
        email: ''
      }
    },

    async handleSubmit() {
      if (!this.form.username || !this.form.password) {
        alert('请填写用户名和密码')
        return
      }

      if (!this.isLogin) {
        if (!this.form.phone) {
          alert('请填写手机号')
          return
        }
      }

      try {
        const url = this.isLogin ? '/auth/login' : '/auth/register'
        
        // 根据是登录还是注册，发送不同的数据
        let requestData = {}
        if (this.isLogin) {
          // 登录只需要用户名和密码
          requestData = {
            username: this.form.username,
            password: this.form.password
          }
        } else {
          // 注册需要字段（不含真实姓名和身份证号）
          requestData = {
            username: this.form.username,
            password: this.form.password,
            phone: this.form.phone,
            email: this.form.email
          }
        }
        
        const response = await axios.post(`${API_BASE}${url}`, requestData)
        
        if (response.data.success) {
          const data = response.data.data
          
          // 保存token和用户信息
          localStorage.setItem('token', data.token)
          localStorage.setItem('userInfo', JSON.stringify({
            username: data.username,
            realName: data.realName,
            role: data.role,
            userId: data.userId
          }))

          alert(response.data.message)

          // 根据角色跳转到不同页面
          if (data.role === 'ADMIN') {
            this.$router.push('/admin')
          } else {
            this.$router.push('/products')
          }
        } else {
          alert(response.data.message)
        }
      } catch (error) {
        console.error('请求失败:', error)
        alert('操作失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  max-width: 450px;
  width: 100%;
  overflow: hidden;
}

.login-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
}

.login-header h1 {
  font-size: 24px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.login-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-actions {
  margin-top: 30px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.toggle-mode {
  text-align: center;
  padding: 0 30px 30px;
  color: #666;
}

.toggle-mode a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.toggle-mode a:hover {
  text-decoration: underline;
}

.test-accounts {
  background: #f8f9fa;
  border-top: 1px solid #eee;
  padding: 20px;
}

.test-accounts h3 {
  font-size: 16px;
  margin: 0 0 12px 0;
  color: #333;
}

.account-item {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.account-item:last-child {
  margin-bottom: 0;
}
</style>