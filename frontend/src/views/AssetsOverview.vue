<template>
  <div class="assets-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">资产概览</h1>
      <div class="header-placeholder"></div>
    </div>

    <!-- 总资产 -->
    <div class="total-assets">
      <div class="assets-label">总资产</div>
      <div class="assets-amount">¥{{ formatCurrency(totalAssets) }}</div>
      <div class="assets-change positive">+{{ formatCurrency(totalIncome) }} 今日收益</div>
    </div>

    <!-- 资产分布图表 -->
    <div class="chart-section">
      <div class="section-header">
        <h2 class="section-title">资产分布</h2>
      </div>
      <div class="chart-container">
        <!-- 只有在数据加载完成后才渲染图表 -->
        <AssetPieChart v-if="dataLoaded" :data="assetsBreakdown" />
        <div v-else class="loading-placeholder">加载中...</div>
      </div>
    </div>

    <!-- 资产明细 -->
    <div class="assets-details">
      <div class="section-header">
        <h2 class="section-title">资产明细</h2>
      </div>
      
      <div class="detail-list">
        <div class="detail-item">
          <div class="item-info">
            <div class="item-icon bg-purple">📅</div>
            <div class="item-text">
              <div class="item-name">定期存款</div>
              <div class="item-desc">稳健收益</div>
            </div>
          </div>
          <div class="item-amount">¥{{ formatCurrency(assetsBreakdown.fixed || 0) }}</div>
        </div>
        
        <div class="detail-item">
          <div class="item-info">
            <div class="item-icon bg-blue">⚡</div>
            <div class="item-text">
              <div class="item-name">活期存款</div>
              <div class="item-desc">灵活存取</div>
            </div>
          </div>
          <div class="item-amount">¥{{ formatCurrency(assetsBreakdown.current || 0) }}</div>
        </div>
        
        <div class="detail-item">
          <div class="item-info">
            <div class="item-icon bg-pink">📈</div>
            <div class="item-text">
              <div class="item-name">其他理财</div>
              <div class="item-desc">高收益产品</div>
            </div>
          </div>
          <div class="item-amount">¥{{ formatCurrency(assetsBreakdown.other || 0) }}</div>
        </div>
      </div>
    </div>

    <!-- 收益统计 -->
    <div class="income-stats">
      <div class="section-header">
        <h2 class="section-title">收益统计</h2>
      </div>
      
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-label">累计收益</div>
          <div class="stat-value income">¥{{ formatCurrency(totalIncome) }}</div>
        </div>
        
        <div class="stat-card">
          <div class="stat-label">本月收益</div>
          <div class="stat-value">¥{{ formatCurrency(monthlyIncome) }}</div>
        </div>
        
        <div class="stat-card">
          <div class="stat-label">年化收益率</div>
          <div class="stat-value">{{ annualizedReturn }}%</div>
        </div>
        
        <div class="stat-card">
          <div class="stat-label">投资产品数</div>
          <div class="stat-value">{{ investmentCount }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi, purchaseApi } from '@/api'
import AssetPieChart from '@/components/AssetPieChart.vue'

export default {
  name: 'AssetsOverview',
  components: {
    AssetPieChart
  },
  data() {
    return {
      userInfo: {},
      totalAssets: 0,
      totalIncome: 0,
      monthlyIncome: 0,
      annualizedReturn: 2.8,
      investmentCount: 0,
      assetsBreakdown: {
        fixed: 0,
        current: 0,
        other: 0
      },
      dataLoaded: false, // 新增：标记数据是否已加载
      loading: false
    }
  },
  created() {
    // 从localStorage获取用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      this.userInfo = JSON.parse(userInfoStr)
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      if (!this.userInfo.userId) return
      
      this.loading = true
      try {
        // 获取用户信息
        const userInfoRes = await userApi.getUserInfo(this.userInfo.userId)
        // 客户端API返回Result对象 {code: 200, message: "...", data: {...}}
        const userData = (userInfoRes && userInfoRes.data) || null
        if (userData) {
          this.totalAssets = userData.balance || 0
          
          // 模拟资产分布
          this.assetsBreakdown = {
            fixed: userData.balance * 0.68 || 0,
            current: userData.balance * 0.24 || 0,
            other: userData.balance * 0.08 || 0
          }
        }
        
        // 获取订单信息计算收益
        const ordersRes = await purchaseApi.getUserOrders(this.userInfo.userId)
        // API拦截器已更新，现在返回Result对象 {code: 200, message: "...", data: [...]}
        const ordersData = (ordersRes && ordersRes.data) || []
        if (ordersData) {
          // 计算累计收益
          let totalIncome = 0
          let monthlyIncome = 0
          let investmentCount = 0
          
          const now = new Date()
          const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
          
          ordersData.forEach(order => {
            if (order.status === 'SUCCESS' && order.expectedIncome) {
              const income = parseFloat(order.expectedIncome)
              totalIncome += income
              investmentCount++
              
              // 检查是否是本月订单
              const orderDate = new Date(order.createTime)
              if (orderDate >= firstDayOfMonth) {
                monthlyIncome += income
              }
            }
          })
          
          this.totalIncome = totalIncome
          this.monthlyIncome = monthlyIncome
          this.investmentCount = investmentCount
        }
        
        // 标记数据已加载完成
        this.dataLoaded = true
      } catch (error) {
        console.error('加载资产数据失败', error)
        alert('加载资产数据失败: ' + error.message)
      } finally {
        this.loading = false
      }
    },
    
    formatCurrency(amount) {
      return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    }
  }
}
</script>

<style scoped>
.assets-page {
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

/* 总资产 */
.total-assets {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 16px;
  text-align: center;
  margin: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.assets-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.assets-amount {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.assets-change {
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.assets-change.positive {
  color: #5dd39e;
}

/* 图表区域 */
.chart-section {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.chart-container {
  height: 250px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-placeholder {
  color: #999;
  font-size: 14px;
}

/* 资产明细 */
.assets-details {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.bg-purple {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.bg-blue {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.bg-pink {
  background: linear-gradient(135deg, #fa709a, #fee140);
}

.item-text {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.item-desc {
  font-size: 12px;
  color: #999;
}

.item-amount {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 收益统计 */
.income-stats {
  background: white;
  margin: 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stat-value.income {
  color: #5dd39e;
}

/* 响应式设计 */
@media (min-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>