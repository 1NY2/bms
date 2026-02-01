<template>
  <div class="user-center-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">个人中心</h1>
      <div class="settings-btn" @click="goToSettings">
        <span>⚙️</span>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-avatar">
        <div class="avatar">{{ userInfo.userName?.charAt(0) || userInfo.realName?.charAt(0) || 'U' }}</div>
      </div>
      <div class="user-info">
        <div class="user-name">{{ userInfo.userName || userInfo.realName || '未知用户' }}</div>
        <div class="user-id">ID: {{ userInfo.userId || '未知' }}</div>
        <div class="user-status" :class="userStatusClass">
          {{ userStatusText }}
        </div>
      </div>
      <div class="user-assets">
        <div class="asset-item">
          <div class="asset-label">账户余额</div>
          <div class="asset-value">¥{{ formatCurrency(userAssets.balance || 0) }}</div>
        </div>
        <div class="asset-item">
          <div class="asset-label">累计收益</div>
          <div class="asset-value income">¥{{ formatCurrency(userAssets.totalIncome || 0) }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷功能 -->
    <div class="quick-actions">
      <div class="action-item" @click="goToOrders">
        <div class="action-icon">📋</div>
        <div class="action-text">我的订单</div>
      </div>
      <div class="action-item" @click="goToProducts">
        <div class="action-icon">🏦</div>
        <div class="action-text">理财产品</div>
      </div>
      <div class="action-item" @click="goToAssets">
        <div class="action-icon">💰</div>
        <div class="action-text">资产概览</div>
      </div>
      <div class="action-item" @click="goToMessages">
        <div class="action-icon">🔔</div>
        <div class="action-text">消息通知</div>
        <div v-if="unreadMessages > 0" class="badge">{{ unreadMessages }}</div>
      </div>
    </div>

    <!-- 资产概览 -->
    <div class="assets-overview">
      <div class="section-header">
        <h2 class="section-title">资产分布</h2>
        <div class="section-action" @click="goToAssets">查看详情 ›</div>
      </div>
      <div class="assets-chart">
        <!-- 只有在数据加载完成后才渲染图表 -->
        <AssetPieChart v-if="assetsLoaded" :data="assetsBreakdown" />
        <div v-else class="loading-placeholder">加载中...</div>
      </div>
      <div class="assets-breakdown">
        <div class="breakdown-item">
          <div class="item-label">
            <span class="color-dot dot1"></span>
            定期存款
          </div>
          <div class="item-value">¥{{ formatCurrency(assetsBreakdown.fixed || 0) }}</div>
        </div>
        <div class="breakdown-item">
          <div class="item-label">
            <span class="color-dot dot2"></span>
            活期存款
          </div>
          <div class="item-value">¥{{ formatCurrency(assetsBreakdown.current || 0) }}</div>
        </div>
        <div class="breakdown-item">
          <div class="item-label">
            <span class="color-dot dot3"></span>
            其他理财
          </div>
          <div class="item-value">¥{{ formatCurrency(assetsBreakdown.other || 0) }}</div>
        </div>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="recent-orders">
      <div class="section-header">
        <h2 class="section-title">最近订单</h2>
        <div class="section-action" @click="goToOrders">查看更多 ›</div>
      </div>
      
      <div v-if="loadingOrders" class="loading">加载中...</div>
      
      <div v-else-if="recentOrders.length === 0" class="empty-orders">
        <div class="empty-icon">📭</div>
        <div class="empty-text">暂无订单记录</div>
      </div>
      
      <div v-else class="orders-list">
        <div 
          v-for="order in recentOrders" 
          :key="order.id" 
          class="order-card"
          @click="viewOrderDetail(order)"
        >
          <div class="order-header">
            <div class="order-product">{{ order.productName }}</div>
            <div class="order-status" :class="order.status">{{ getOrderStatusText(order.status) }}</div>
          </div>
          
          <div class="order-details">
            <div class="detail-row">
              <span class="label">购买金额</span>
              <span class="value">¥{{ formatCurrency(order.amount) }}</span>
            </div>
            
            <div class="detail-row">
              <span class="label">预期收益</span>
              <span class="value income">¥{{ formatCurrency(order.expectedIncome) }}</span>
            </div>
            
            <div class="detail-row">
              <span class="label">下单时间</span>
              <span class="value">{{ formatDateTime(order.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-list">
      <div class="menu-item" @click="editProfile">
        <div class="menu-icon">👤</div>
        <div class="menu-text">个人信息</div>
        <div class="menu-arrow">›</div>
      </div>
      
      <div class="menu-item" @click="goToSecurity">
        <div class="menu-icon">🔒</div>
        <div class="menu-text">安全设置</div>
        <div class="menu-arrow">›</div>
      </div>
      
      <div class="menu-item" @click="goToMessages">
        <div class="menu-icon">🔔</div>
        <div class="menu-text">消息通知</div>
        <div class="menu-arrow">›</div>
      </div>
      
      <div class="menu-item" @click="goToHelp">
        <div class="menu-icon">❓</div>
        <div class="menu-text">帮助中心</div>
        <div class="menu-arrow">›</div>
      </div>
      
      <div class="menu-item" @click="contactService">
        <div class="menu-icon">👨‍💼</div>
        <div class="menu-text">联系客服</div>
        <div class="menu-arrow">›</div>
      </div>
    </div>

    <!-- 底部退出按钮 -->
    <div class="logout-section">
      <button class="logout-btn" @click="logout">
        退出登录
      </button>
    </div>
  </div>
</template>

<script>
import { purchaseApi, userApi } from '@/api'
import AssetPieChart from '@/components/AssetPieChart.vue'

export default {
  name: 'UserCenter',
  components: {
    AssetPieChart
  },
  data() {
    return {
      userInfo: {},
      userAssets: {
        balance: 0,
        totalIncome: 0
      },
      assetsBreakdown: {
        fixed: 0,
        current: 0,
        other: 0
      },
      assetsLoaded: false, // 新增：标记资产数据是否已加载
      orders: [],
      loadingOrders: false,
      unreadMessages: 3 // 模拟未读消息数量
    }
  },
  computed: {
    // 用户状态文本
    userStatusText() {
      const status = this.userInfo.status || '';
      switch (status) {
        case 'NORMAL': return '正常';
        case 'LOCKED': return '已锁定';
        case 'FROZEN': return '已冻结';
        default: return '未知状态';
      }
    },
    
    // 用户状态样式
    userStatusClass() {
      const status = this.userInfo.status || '';
      switch (status) {
        case 'NORMAL': return 'status-normal';
        case 'LOCKED': return 'status-locked';
        case 'FROZEN': return 'status-frozen';
        default: return '';
      }
    },
    
    // 最近3个订单
    recentOrders() {
      return this.orders.slice(0, 3);
    }
  },
  created() {
    // 从localStorage获取用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      try {
        this.userInfo = JSON.parse(userInfoStr);
        // 加载真实的用户信息和资产数据
        this.loadUserInfo();
      } catch (e) {
        console.error('解析用户信息失败', e);
        // 如果解析失败，使用空对象
        this.userInfo = {};
      }
    } else {
      // 如果没有用户信息，尝试从路由参数获取
      if (this.$route.params.userId) {
        this.userInfo.userId = this.$route.params.userId;
        this.loadUserInfo();
      } else {
        console.warn('未找到用户信息');
        // 可以选择重定向到登录页
        // this.$router.push('/login');
      }
    }
    
    // 加载订单列表
    this.loadOrders();
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
        // 用户API返回完整response对象，需要从data中提取
        // 需要检查响应结构
        let userData = null;
        if (userInfoRes && typeof userInfoRes === 'object') {
          if (userInfoRes.data && typeof userInfoRes.data === 'object') {
            userData = userInfoRes.data;
          } else if (typeof userInfoRes === 'object' && userInfoRes.userId) {
            userData = userInfoRes;
          }
        }
        if (userData && userData.userId) {
          // 更新用户信息
          this.userInfo = {
            ...this.userInfo,
            ...userData
          };
          
          // 更新用户资产信息
          this.userAssets = {
            balance: userData.balance || 0,
            totalIncome: 0 // 累计收益需要从订单计算得出
          };
          
          // 模拟资产分布数据（实际应该从后端获取）
          this.assetsBreakdown = {
            fixed: userData.balance * 0.68 || 0,
            current: userData.balance * 0.24 || 0,
            other: userData.balance * 0.08 || 0
          };
          
          // 标记资产数据已加载完成
          this.assetsLoaded = true;
        } else {
          console.warn('获取用户信息为空');
        }
      } catch (error) {
        console.error('加载用户信息失败', error);
        // 不再弹出alert，改为控制台输出
        // alert('加载用户信息失败: ' + error.message);
      }
    },
    
    async loadOrders() {
      // 确保有用户ID
      if (!this.userInfo.userId) {
        console.warn('缺少用户ID，无法加载订单信息');
        return;
      }
      
      this.loadingOrders = true;
      try {
        const ordersRes = await purchaseApi.getUserOrders(this.userInfo.userId);
        // API拦截器已更新，现在返回Result对象 {code: 200, message: "...", data: [...]}，直接从响应中提取数据
        // 需要检查响应结构，确保orders是数组
        let ordersData = [];
        if (ordersRes && typeof ordersRes === 'object') {
          if (ordersRes.data && Array.isArray(ordersRes.data)) {
            ordersData = ordersRes.data;
          } else if (Array.isArray(ordersRes)) {
            ordersData = ordersRes;
          }
        }
        this.orders = ordersData;
        
        // 计算累计收益
        let totalIncome = 0;
        if (Array.isArray(this.orders)) {
          this.orders.forEach(order => {
            if (order.status === 'SUCCESS' && order.expectedIncome) {
              totalIncome += parseFloat(order.expectedIncome);
            }
          });
        }
        
        this.userAssets.totalIncome = totalIncome;
      } catch (error) {
        console.error('加载订单列表失败', error);
        // 不再弹出alert，改为控制台输出
        // alert('加载订单列表失败: ' + error.message);
        this.orders = [];
      } finally {
        this.loadingOrders = false;
      }
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'SUCCESS': '已完成',
        'PROCESSING': '处理中',
        'PENDING': '待支付',
        'FAILED': '已失败',
        'CANCELLED': '已取消'
      };
      return statusMap[status] || status;
    },
    
    formatCurrency(amount) {
      return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    
    formatDateTime(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },
    
    viewOrderDetail(order) {
      // 跳转到订单详情页面
      this.$router.push(`/order/${order.orderNo}`);
    },
    
    goToOrders() {
      // 跳转到订单列表页面
      this.$router.push('/orders');
    },
    
    goToProducts() {
      this.$router.push('/products');
    },
    
    goToAssets() {
      // 跳转到资产概览页面
      this.$router.push('/assets');
    },
    
    goToMessages() {
      // 跳转到消息通知页面
      this.$router.push('./messages');
    },
    
    editProfile() {
      // 跳转到个人信息编辑页面
      this.$router.push('./profile/edit');
    },
    
    goToSettings() {
      // 跳转到安全设置页面（原来指向了不存在的/settings路径）
      this.$router.push('./security');
    },
    
    goToSecurity() {
      // 跳转到安全设置页面
      this.$router.push('./security');
    },
    
    goToHelp() {
      // 跳转到帮助中心页面
      this.$router.push('./help');
    },
    
    contactService() {
      // 联系客服
      alert('客服电话：400-123-4567');
    },
    
    logout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        this.$router.push('./login');
      }
    }
  }
}
</script>

<style scoped>
.user-center-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
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

.settings-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
}

/* 用户信息卡片 */
.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  margin: 16px;
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.user-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(30deg);
}

.user-card > * {
  position: relative;
  z-index: 1;
}

.user-avatar {
  margin-bottom: 16px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: 600;
}

.user-info {
  margin-bottom: 20px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
}

.user-id {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.user-status {
  display: inline-block;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.2);
}

.status-normal {
  background-color: rgba(46, 204, 113, 0.3);
}

.status-locked {
  background-color: rgba(231, 76, 60, 0.3);
}

.status-frozen {
  background-color: rgba(241, 196, 15, 0.3);
}

.user-assets {
  display: flex;
  gap: 20px;
}

.asset-item {
  flex: 1;
}

.asset-label {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 4px;
}

.asset-value {
  font-size: 18px;
  font-weight: 600;
}

.asset-value.income {
  color: #5dd39e;
}

/* 快捷功能 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 0 16px 16px;
}

.action-item {
  background-color: white;
  border-radius: 12px;
  padding: 16px 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.action-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.action-text {
  font-size: 12px;
  color: #333;
}

.badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #ff4757;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

/* 资产概览 */
.assets-overview {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.section-action {
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
}

.assets-chart {
  height: 200px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-placeholder {
  color: #999;
  font-size: 14px;
}

.assets-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.item-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.color-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.dot1 {
  background-color: #667eea;
}

.dot2 {
  background-color: #764ba2;
}

.dot3 {
  background-color: #f093fb;
}

.item-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

/* 最近订单 */
.recent-orders {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.loading, .empty-orders {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.order-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  background: #f0f0f0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.order-product {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.order-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #f0f0f0;
}

.order-status.SUCCESS {
  background-color: rgba(46, 204, 113, 0.2);
  color: #27ae60;
}

.order-status.PROCESSING {
  background-color: rgba(241, 196, 15, 0.2);
  color: #f39c12;
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}

.value.income {
  color: #5dd39e;
}

/* 功能菜单 */
.menu-list {
  background: white;
  margin: 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: #f8f9fa;
}

.menu-icon {
  font-size: 20px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.menu-arrow {
  color: #ccc;
  font-size: 18px;
}

/* 退出按钮 */
.logout-section {
  padding: 16px;
  text-align: center;
}

.logout-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 14px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}
</style>