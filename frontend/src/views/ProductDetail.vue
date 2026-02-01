<template>
  <div class="product-detail-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">产品详情</h1>
      <div class="share-btn" @click="shareProduct">
        <span>📤</span>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="product" class="content">
      <!-- 产品基本信息 -->
      <div class="product-card">
        <div class="product-name">{{ product.productName }}</div>
        
        <div class="rate-section">
          <div class="rate-value">{{ product.annualRate }}%</div>
          <div class="rate-label">年化利率</div>
        </div>

        <div class="product-tags">
          <span class="tag">{{ product.durationDesc }}</span>
          <span class="tag" :class="riskLevelClass">{{ product.riskLevel }}风险</span>
          <span class="tag">{{ product.interestMethod }}</span>
          <span v-if="isRecommended" class="tag recommended-tag">推荐</span>
        </div>
        
        <div class="countdown-section" v-if="product.endDate">
          <div class="countdown-label">距离结束</div>
          <div class="countdown-value">{{ daysUntilEnd }}天</div>
        </div>
      </div>

      <!-- 收益计算器 -->
      <div class="calculator-card">
        <div class="card-title">收益计算器</div>
        <div class="calculator-content">
          <div class="input-group">
            <label>投资金额（元）</label>
            <input 
              type="number" 
              v-model="investmentAmount" 
              placeholder="请输入投资金额"
              class="amount-input"
              @input="calculateIncome"
            />
          </div>
          
          <div class="result-section">
            <div class="result-item">
              <div class="result-label">预期收益</div>
              <div class="result-value income">¥ {{ expectedIncome.toFixed(2) }}</div>
            </div>
            <div class="result-item">
              <div class="result-label">到期本息</div>
              <div class="result-value total">¥ {{ totalAmount.toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 产品优势 -->
      <div class="advantages-card">
        <div class="card-title">产品优势</div>
        <div class="advantages-list">
          <div class="advantage-item">
            <div class="advantage-icon">🔒</div>
            <div class="advantage-text">资金安全保障</div>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon">📈</div>
            <div class="advantage-text">收益稳定增长</div>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon">⚡</div>
            <div class="advantage-text">起息快速</div>
          </div>
          <div class="advantage-item">
            <div class="advantage-icon">📱</div>
            <div class="advantage-text">随时查看</div>
          </div>
        </div>
      </div>

      <!-- 产品详细信息 -->
      <div class="detail-card">
        <div class="detail-title">存款详情</div>
        
        <div class="detail-item">
          <span class="label">存款期限</span>
          <span class="value">{{ product.duration }}天</span>
        </div>
        
        <div class="detail-item">
          <span class="label">起存金额</span>
          <span class="value">{{ product.minAmount }}元</span>
        </div>
        
        <div class="detail-item">
          <span class="label">递增金额</span>
          <span class="value">{{ product.incrementAmount }}元</span>
        </div>
        
        <div class="detail-item">
          <span class="label">单人限额</span>
          <span class="value">{{ product.personLimit }}元</span>
        </div>
        
        <div class="detail-item">
          <span class="label">单日限额</span>
          <span class="value">{{ product.dailyLimit }}元</span>
        </div>
        
        <div class="detail-item">
          <span class="label">剩余额度</span>
          <span class="value">{{ product.inventory }}份</span>
        </div>
      </div>

      <!-- 交易规则 -->
      <div class="detail-card">
        <div class="detail-title">交易规则</div>
        
        <div class="detail-item">
          <span class="label">起息日期</span>
          <span class="value">{{ formatDate(product.startDate) }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">到期日期</span>
          <span class="value">{{ formatDate(product.endDate) }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">结息方式</span>
          <span class="value">{{ product.interestMethod }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">产品状态</span>
          <span class="value" :class="productStatusClass">{{ productStatusText }}</span>
        </div>
      </div>

      <!-- 购买须知 -->
      <div class="detail-card">
        <div class="detail-title">购买须知</div>
        <div class="notice-content">
          <p>· 存款起存金额：{{ product.minAmount }}元，支持{{ product.incrementAmount }}元递增</p>
          <p>· 每日22:00前存入，当日起息（含节假日）</p>
          <p>· 单人购买限额：{{ product.personLimit }}元</p>
          <p>· 单日购买限额：{{ product.dailyLimit }}元</p>
          <p>· 产品剩余额度：{{ product.inventory }}份，先到先得</p>
          <p v-if="product.description">· {{ product.description }}</p>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="bottom-bar">
        <button class="favorite-btn" @click="toggleFavorite">
          <span v-if="isFavorite">❤️</span>
          <span v-else>🤍</span>
        </button>
        <button class="purchase-btn" @click="goToPurchase">立即存入</button>
      </div>
    </div>
  </div>
</template>

<script>
import { productApi } from '@/api'

export default {
  name: 'ProductDetail',
  data() {
    return {
      loading: false,
      product: null,
      investmentAmount: 10000,
      expectedIncome: 0,
      totalAmount: 0,
      isFavorite: false
    }
  },
  computed: {
    // 风险等级样式
    riskLevelClass() {
      const level = this.product?.riskLevel || '';
      switch (level) {
        case '低': return 'low-risk';
        case '中': return 'medium-risk';
        case '高': return 'high-risk';
        default: return '';
      }
    },
    
    // 产品状态文本
    productStatusText() {
      const status = this.product?.status || '';
      switch (status) {
        case 'ACTIVE': return '销售中';
        case 'INACTIVE': return '已停售';
        default: return status;
      }
    },
    
    // 产品状态样式
    productStatusClass() {
      const status = this.product?.status || '';
      return status === 'ACTIVE' ? 'status-active' : 'status-inactive';
    },
    
    // 是否推荐产品
    isRecommended() {
      // 简单判断：年化利率大于4%且剩余额度大于100为推荐产品
      return this.product && 
             parseFloat(this.product.annualRate) > 4.0 && 
             this.product.inventory > 100;
    },
    
    // 距离结束天数
    daysUntilEnd() {
      if (!this.product || !this.product.endDate) return 0;
      const endDate = new Date(this.product.endDate);
      const today = new Date();
      const diffTime = endDate - today;
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }
  },
  created() {
    this.loadProductDetail();
    this.calculateIncome();
  },
  methods: {
    async loadProductDetail() {
      this.loading = true;
      try {
        const productCode = this.$route.params.code;
        // 由于这是管理端API，需要访问response.data
        const response = await productApi.getProductDetail(productCode);
        // 管理端API返回 {success: true, data: {...}} 结构
        this.product = (response.data && response.data.data) || null;
      } catch (error) {
        console.error('加载产品详情失败', error);
        this.$router.back();
      } finally {
        this.loading = false;
      }
    },
    
    // 计算收益
    calculateIncome() {
      if (!this.investmentAmount || !this.product) {
        this.expectedIncome = 0;
        this.totalAmount = 0;
        return;
      }
      
      const principal = parseFloat(this.investmentAmount);
      const rate = parseFloat(this.product.annualRate) / 100;
      const days = this.product.duration;
      
      // 收益 = 本金 × 年化利率 × 天数 / 365
      this.expectedIncome = principal * rate * days / 365;
      this.totalAmount = principal + this.expectedIncome;
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleDateString('zh-CN');
    },
    
    goToPurchase() {
      this.$router.push(`/purchase/${this.product.productCode}`);
    },
    
    toggleFavorite() {
      this.isFavorite = !this.isFavorite;
      // 这里可以调用API保存收藏状态
    },
    
    shareProduct() {
      // 这里可以实现分享功能
      alert('分享功能待开发');
    }
  }
}
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

/* 页面头部 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
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
  margin-left: 12px;
  flex: 1;
}

.share-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.content {
  padding: 16px;
}

/* 产品卡片 */
.product-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
  color: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  position: relative;
}

.product-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
}

.rate-section {
  text-align: center;
  padding: 20px 0;
}

.rate-value {
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
}

.rate-label {
  font-size: 14px;
  margin-top: 8px;
  opacity: 0.9;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.tag {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.low-risk {
  background-color: rgba(46, 204, 113, 0.3);
}

.medium-risk {
  background-color: rgba(241, 196, 15, 0.3);
}

.high-risk {
  background-color: rgba(231, 76, 60, 0.3);
}

.recommended-tag {
  background-color: rgba(255, 87, 34, 0.3);
}

.countdown-section {
  margin-top: 20px;
  text-align: center;
  padding: 12px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.countdown-label {
  font-size: 12px;
  margin-bottom: 4px;
}

.countdown-value {
  font-size: 18px;
  font-weight: 600;
}

/* 收益计算器 */
.calculator-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.input-group {
  margin-bottom: 16px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.amount-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

.amount-input:focus {
  outline: none;
  border-color: #667eea;
}

.result-section {
  display: flex;
  gap: 16px;
}

.result-item {
  flex: 1;
  text-align: center;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.result-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.result-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.income {
  color: #5dd39e;
}

.total {
  color: #667eea;
}

/* 产品优势 */
.advantages-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.advantages-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.advantage-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.advantage-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.advantage-text {
  font-size: 12px;
  color: #666;
}

/* 详细信息卡片 */
.detail-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.detail-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #999;
}

.value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.status-active {
  color: #5dd39e;
  font-weight: 600;
}

.status-inactive {
  color: #ff6b6b;
  font-weight: 600;
}

.notice-content {
  font-size: 12px;
  color: #666;
  line-height: 1.6;
}

.notice-content p {
  margin: 8px 0;
}

/* 底部按钮 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  gap: 16px;
}

.favorite-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 1px solid #e5e5e5;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.favorite-btn:hover {
  transform: scale(1.1);
}

.purchase-btn {
  flex: 1;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.purchase-btn:hover {
  opacity: 0.9;
}

.purchase-btn:active {
  transform: scale(0.98);
}
</style>