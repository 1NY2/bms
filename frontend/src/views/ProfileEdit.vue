<template>
  <div class="profile-edit-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>×</span>
      </div>
      <h1 class="title">编辑资料</h1>
      <div class="save-btn" @click="saveProfile">
        保存
      </div>
    </div>

    <!-- 编辑表单 -->
    <div class="edit-form">
      <div class="form-group">
        <label>用户名</label>
        <input 
          type="text" 
          v-model="editableUserInfo.username" 
          placeholder="请输入用户名"
          class="form-input"
          disabled
        />
        <div class="hint">用户名一经设置无法修改</div>
      </div>
      
      <div class="form-group">
        <label>手机号</label>
        <input 
          type="text" 
          v-model="editableUserInfo.phoneNumber" 
          placeholder="请输入手机号"
          class="form-input"
        />
      </div>
      
      <div class="form-group">
        <label>邮箱</label>
        <input 
          type="email" 
          v-model="editableUserInfo.email" 
          placeholder="请输入邮箱"
          class="form-input"
        />
      </div>
      
      <div class="form-group">
        <label>所在地区</label>
        <input 
          type="text" 
          v-model="editableUserInfo.region" 
          placeholder="请输入所在地区"
          class="form-input"
        />
      </div>
      
      <div class="form-group">
        <label>真实姓名</label>
        <input 
          type="text" 
          v-model="editableUserInfo.userName" 
          placeholder="请在安全设置中进行实名认证"
          class="form-input"
          :disabled="isCertified"
        />
        <div class="hint" v-if="!isCertified">请前往安全设置完成实名认证</div>
        <div class="hint" v-else>实名认证信息一经设置无法修改</div>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '@/api'

export default {
  name: 'ProfileEdit',
  data() {
    return {
      userInfo: {},
      isCertified: false,
      editableUserInfo: {
        username: '',
        phoneNumber: '',
        email: '',
        region: '',
        userName: '' // 真实姓名
      }
    }
  },
  created() {
    // 从localStorage获取用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      try {
        this.userInfo = JSON.parse(userInfoStr);
        // 加载真实的用户信息
        this.loadUserInfo();
      } catch (e) {
        console.error('解析用户信息失败', e);
        this.userInfo = {};
      }
    }
  },
  methods: {
    async loadUserInfo() {
      // 确保有用户ID
      if (!this.userInfo.userId) {
        console.warn('缺少用户ID，无法加载用户信息');
        return;
      }
      
      try {
        // 获取真实的用户信息
        const userInfoRes = await userApi.getUserInfo(this.userInfo.userId);
        // 客户端API返回Result对象 {code: 200, message: "...", data: {...}}
        const userData = (userInfoRes && userInfoRes.data) || null;
        if (userData && userData.userId) {
          // 更新用户信息
          this.userInfo = { ...this.userInfo, ...userData };
          
          // 设置可编辑的用户信息
          this.editableUserInfo = {
            username: this.userInfo.username || '',
            phoneNumber: userData.phoneNumber || '',
            email: userData.email || '',
            region: userData.region || '',
            userName: userData.userName || '' // 真实姓名
          };
          
          // 检查是否已完成实名认证
          this.isCertified = !!userData.userName;
        }
      } catch (error) {
        console.error('加载用户信息失败', error);
      }
    },
    
    async saveProfile() {
      // 确保有用户ID
      if (!this.userInfo.userId) {
        alert('缺少用户ID，无法保存用户信息');
        return;
      }
      
      try {
        // 准备要更新的用户信息（不包括真实姓名，因为需要通过实名认证设置）
        const updateData = {
          phoneNumber: this.editableUserInfo.phoneNumber,
          email: this.editableUserInfo.email,
          region: this.editableUserInfo.region
        };
        
        // 调用后端接口更新用户信息
        const updatedUserInfoRes = await userApi.updateUserInfo(this.userInfo.userId, updateData);
        // 客户端API返回Result对象 {code: 200, message: "...", data: {...}}
        const updatedUserInfo = (updatedUserInfoRes && updatedUserInfoRes.data) || null;
        
        if (updatedUserInfo) {
          // 更新本地存储的用户信息
          const updatedLocalUserInfo = {
            ...this.userInfo,
            ...updatedUserInfo
          };
          localStorage.setItem('userInfo', JSON.stringify(updatedLocalUserInfo));
          
          // 更新当前用户信息
          this.userInfo = updatedLocalUserInfo;
          
          alert('保存成功');
          this.$router.back();
        }
      } catch (error) {
        console.error('保存用户信息失败', error);
        alert('保存失败: ' + error.message);
      }
    }
  }
}
</script>

<style scoped>
.profile-edit-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 页面头部 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  font-size: 24px;
  cursor: pointer;
  padding: 4px 8px;
  font-weight: 300;
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.save-btn {
  font-size: 16px;
  cursor: pointer;
  padding: 4px 8px;
  font-weight: 500;
}

/* 编辑表单 */
.edit-form {
  padding: 16px;
  background: white;
  margin-top: 16px;
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

.form-input:disabled {
  background-color: #f5f5f5;
  color: #999;
}

.hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>