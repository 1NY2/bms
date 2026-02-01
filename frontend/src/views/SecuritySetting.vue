<template>
  <div class="security-setting-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">安全设置</h1>
      <div class="placeholder"></div>
    </div>

    <!-- 安全设置内容 -->
    <div class="security-content">
      <!-- 实名认证 -->
      <div class="security-section">
        <div class="section-header">
          <h2 class="section-title">实名认证</h2>
        </div>
        <div class="section-content">
          <div class="certification-status" v-if="isCertified">
            <div class="status-icon">✅</div>
            <div class="status-text">
              <div class="main-text">已完成实名认证</div>
              <div class="sub-text">姓名：{{ certifiedName }} 身份证号：{{ maskedIdCard }}</div>
            </div>
          </div>
          <div class="certification-required" v-else @click="showCertificationDialog = true">
            <div class="status-icon">⚠️</div>
            <div class="status-text">
              <div class="main-text">未完成实名认证</div>
              <div class="sub-text">点击完成实名认证，保障账户安全</div>
            </div>
            <div class="arrow">›</div>
          </div>
        </div>
      </div>

      <!-- 修改密码 -->
      <div class="security-section">
        <div class="section-header">
          <h2 class="section-title">登录密码</h2>
        </div>
        <div class="section-content">
          <div class="password-setting" @click="showPasswordDialog = true">
            <div class="setting-text">
              <div class="main-text">修改登录密码</div>
              <div class="sub-text">建议定期更换密码，保护账户安全</div>
            </div>
            <div class="arrow">›</div>
          </div>
        </div>
      </div>

      <!-- 手机号绑定 -->
      <div class="security-section">
        <div class="section-header">
          <h2 class="section-title">手机号绑定</h2>
        </div>
        <div class="section-content">
          <div class="phone-setting">
            <div class="setting-text">
              <div class="main-text">绑定手机号</div>
              <div class="sub-text">{{ userInfo.phoneNumber || '未绑定' }}</div>
            </div>
            <div class="arrow">›</div>
          </div>
        </div>
      </div>
      
      <!-- 邮箱绑定 -->
      <div class="security-section">
        <div class="section-header">
          <h2 class="section-title">邮箱绑定</h2>
        </div>
        <div class="section-content">
          <div class="email-setting">
            <div class="setting-text">
              <div class="main-text">绑定邮箱</div>
              <div class="sub-text">{{ userInfo.email || '未绑定' }}</div>
            </div>
            <div class="arrow">›</div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 实名认证对话框 -->
  <div class="dialog-overlay" v-if="showCertificationDialog" @click="showCertificationDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h3>实名认证</h3>
        <div class="close-btn" @click="showCertificationDialog = false">×</div>
      </div>
      <div class="dialog-content">
        <div class="form-group">
          <label>真实姓名</label>
          <input 
            type="text" 
            v-model="certificationForm.name" 
            placeholder="请输入真实姓名"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label>身份证号</label>
          <input 
            type="text" 
            v-model="certificationForm.idCard" 
            placeholder="请输入身份证号"
            class="form-input"
          />
        </div>
      </div>
      <div class="dialog-footer">
        <button class="cancel-btn" @click="showCertificationDialog = false">取消</button>
        <button class="confirm-btn" @click="submitCertification">确认</button>
      </div>
    </div>
  </div>

  <!-- 修改密码对话框 -->
  <div class="dialog-overlay" v-if="showPasswordDialog" @click="showPasswordDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h3>修改密码</h3>
        <div class="close-btn" @click="showPasswordDialog = false">×</div>
      </div>
      <div class="dialog-content">
        <div class="form-group">
          <label>原密码</label>
          <input 
            type="password" 
            v-model="passwordForm.oldPassword" 
            placeholder="请输入原密码"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input 
            type="password" 
            v-model="passwordForm.newPassword" 
            placeholder="请输入新密码"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label>确认新密码</label>
          <input 
            type="password" 
            v-model="passwordForm.confirmPassword" 
            placeholder="请再次输入新密码"
            class="form-input"
          />
        </div>
      </div>
      <div class="dialog-footer">
        <button class="cancel-btn" @click="showPasswordDialog = false">取消</button>
        <button class="confirm-btn" @click="changePassword">确认</button>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '@/api'

export default {
  name: 'SecuritySetting',
  data() {
    return {
      userInfo: {},
      isCertified: false,
      certifiedName: '',
      idCard: '',
      showCertificationDialog: false,
      showPasswordDialog: false,
      certificationForm: {
        name: '',
        idCard: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
  },
  computed: {
    maskedIdCard() {
      if (!this.idCard) return '';
      return this.idCard.substring(0, 4) + '**********' + this.idCard.substring(14);
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
          
          // 检查是否已完成实名认证
          if (userData.userName && userData.idCard) {
            this.isCertified = true;
            this.certifiedName = userData.userName;
            this.idCard = userData.idCard;
          }
        }
      } catch (error) {
        console.error('加载用户信息失败', error);
      }
    },
    
    submitCertification() {
      if (!this.certificationForm.name || !this.certificationForm.idCard) {
        alert('请填写完整的实名认证信息');
        return;
      }
      
      // 验证身份证号格式
      const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      if (!idCardRegex.test(this.certificationForm.idCard)) {
        alert('身份证号格式不正确');
        return;
      }
      
      // 这里应该调用后端接口进行实名认证
      // 模拟认证成功
      this.isCertified = true;
      this.certifiedName = this.certificationForm.name;
      this.idCard = this.certificationForm.idCard;
      
      // 更新用户信息
      this.userInfo.userName = this.certificationForm.name;
      this.userInfo.idCard = this.certificationForm.idCard;
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
      
      // 关闭对话框
      this.showCertificationDialog = false;
      
      alert('实名认证成功');
    },
    
    changePassword() {
      if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword || !this.passwordForm.confirmPassword) {
        alert('请填写完整的密码信息');
        return;
      }
      
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        alert('两次输入的新密码不一致');
        return;
      }
      
      // 这里应该调用后端接口修改密码
      // 模拟修改成功
      alert('密码修改成功');
      
      // 清空表单
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
      
      // 关闭对话框
      this.showPasswordDialog = false;
    }
  }
}
</script>

<style scoped>
.security-setting-page {
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
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.placeholder {
  width: 24px;
}

/* 安全设置内容 */
.security-content {
  padding: 16px;
}

.security-section {
  background: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.section-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.section-content {
  padding: 16px;
}

.certification-status, .certification-required, .password-setting, .phone-setting {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
}

.status-icon {
  font-size: 24px;
  margin-right: 12px;
}

.setting-text {
  flex: 1;
}

.main-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.sub-text {
  font-size: 12px;
  color: #999;
}

.arrow {
  color: #ccc;
  font-size: 18px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  font-size: 24px;
  cursor: pointer;
  padding: 4px;
}

.dialog-content {
  padding: 16px;
}

.form-group {
  margin-bottom: 16px;
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

.dialog-footer {
  display: flex;
  padding: 16px;
  gap: 12px;
  border-top: 1px solid #eee;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  border: none;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

.confirm-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
</style>