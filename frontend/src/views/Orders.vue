<template>
  <div class="orders-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">我的订单</h1>
      <div class="header-placeholder"></div>
    </div>

    <!-- 订单筛选 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeFilter === 'ALL' }"
          @click="setFilter('ALL')"
        >
          全部
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeFilter === 'SUCCESS' }"
          @click="setFilter('SUCCESS')"
        >
          已完成
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeFilter === 'PROCESSING' }"
          @click="setFilter('PROCESSING')"
        >
          处理中
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="filteredOrders.length === 0" class="empty-orders">
        <div class="empty-icon">📭</div>
        <div class="empty-text">暂无订单记录</div>
      </div>
      
      <div v-else>
        <div 
          v-for="order in filteredOrders" 
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
              <span class="label">订单号</span>
              <span class="value order-no">{{ order.orderNo }}</span>
            </div>
            
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
  </div>
</template>

<script>
import { purchaseApi } from '@/api'

export default {
  name: 'Orders',
  data() {
    return {
      orders: [],
      loading: false,
      activeFilter: 'ALL',
      userInfo: {}
    }
  },
  computed: {
    filteredOrders() {
      if (this.activeFilter === 'ALL') {
        return this.orders
      }
      return this.orders.filter(order => order.status === this.activeFilter)
    }
  },
  created() {
    // 从localStorage获取用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      this.userInfo = JSON.parse(userInfoStr)
      this.loadOrders()
    }
  },
  methods: {
    async loadOrders() {
      if (!this.userInfo.userId) return
      
      this.loading = true
      try {
        const response = await purchaseApi.getUserOrders(this.userInfo.userId)
        // API拦截器已更新，现在返回Result对象 {code: 200, message: "...", data: [...]}
        this.orders = (response && response.data) || []
      } catch (error) {
        console.error('加载订单列表失败', error)
        alert('加载订单列表失败: ' + error.message)
      } finally {
        this.loading = false
      }
    },
    
    setFilter(filter) {
      this.activeFilter = filter
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'SUCCESS': '已完成',
        'PROCESSING': '处理中',
        'PENDING': '待支付',
        'FAILED': '已失败',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || status
    },
    
    formatCurrency(amount) {
      return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    },
    
    formatDateTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    
    viewOrderDetail(order) {
      // 跳转到订单详情页面
      this.$router.push(`/order/${order.orderNo}`)
    }
  }
}
</script>

<style scoped>
.orders-page {
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

.header-placeholder {
  width: 32px;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 16px;
  margin: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-tabs {
  display: flex;
  gap: 16px;
}

.tab-item {
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #f0f0f0;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 订单列表 */
.orders-list {
  padding: 0 16px;
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
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-product {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.order-status {
  font-size: 12px;
  padding: 4px 12px;
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

.order-status.FAILED {
  background-color: rgba(231, 76, 60, 0.2);
  color: #c0392b;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #999;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.value.income {
  color: #5dd39e;
}

.order-no {
  font-family: monospace;
  font-size: 12px;
}
</style>