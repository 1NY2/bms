<template>
  <div class="product-list-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">存款产品</h1>
      <div class="user-center-btn" ref="userCenterBtn">
        <span>👤</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-container">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="搜索产品名称" 
          class="search-input"
          @input="handleSearch"
        />
      </div>
    </div>

    <!-- 产品分类标签 -->
    <div class="category-tabs">
      <div 
        v-for="category in categories" 
        :key="category.key"
        :class="['tab', { active: activeCategory === category.key }]"
        @click="switchCategory(category.key)"
      >
        {{ category.name }}
      </div>
    </div>

    <!-- 推荐产品区域 -->
    <div v-if="recommendedProducts.length > 0" class="recommended-section">
      <div class="section-header">
        <h2 class="section-title">🔥 热门推荐</h2>
      </div>
      <div class="recommended-products">
        <div 
          v-for="product in recommendedProducts" 
          :key="product.id" 
          class="recommended-product-card"
          @click="goToDetail(product.productCode)"
        >
          <div class="product-header">
            <div class="product-name">{{ product.productName }}</div>
            <div class="product-tag hot-tag">热门</div>
          </div>
          <div class="product-rate">
            <span class="rate-value">{{ product.annualRate }}</span>
            <span class="rate-unit">%</span>
          </div>
          <div class="rate-label">年化利率</div>
        </div>
      </div>
    </div>

    <!-- 产品列表 -->
    <div class="product-list-section">
      <div class="section-header">
        <h2 class="section-title">所有产品</h2>
        <div class="sort-options">
          <select v-model="sortBy" @change="sortProducts" class="sort-select">
            <option value="rate">按利率排序</option>
            <option value="term">按期限排序</option>
          </select>
        </div>
      </div>
      
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="filteredProducts.length === 0" class="empty">
        <div class="empty-icon">📭</div>
        <div class="empty-text">暂无符合条件的产品</div>
      </div>
      
      <div 
        v-else 
        v-for="product in filteredProducts" 
        :key="product.id" 
        class="product-card"
        @click="goToDetail(product.productCode)"
      >
        <!-- 产品标题 -->
        <div class="product-header">
          <div class="product-name">{{ product.productName }}</div>
          <div class="product-period">{{ product.durationDesc }}</div>
        </div>

        <!-- 年化利率 -->
        <div class="product-rate">
          <span class="rate-value">{{ product.annualRate }}</span>
          <span class="rate-unit">%</span>
        </div>
        <div class="rate-label">年化利率</div>

        <!-- 产品信息 -->
        <div class="product-info">
          <div class="info-item">
            <span class="info-label">起存金额</span>
            <span class="info-value">{{ product.minAmount }}元</span>
          </div>
          <div class="info-item">
            <span class="info-label">递增金额</span>
            <span class="info-value">{{ product.incrementAmount }}元</span>
          </div>
        </div>

        <!-- 查看详情箭头 -->
        <div class="arrow">&gt;</div>
      </div>
    </div>
  </div>
</template>

<script>
import { productApi } from '@/api'

export default {
  name: 'ProductList',
  data() {
    return {
      loading: false,
      products: [],
      searchKeyword: '',
      activeCategory: 'all',
      sortBy: 'rate',
      categories: [
        { key: 'all', name: '全部' },
        { key: 'fixed', name: '定期存款' },
        { key: 'notice', name: '通知存款' }
      ]
    }
  },
  computed: {
    // 推荐产品（年化利率最高的前3个）
    recommendedProducts() {
      return [...this.products]
        .sort((a, b) => parseFloat(b.annualRate) - parseFloat(a.annualRate))
        .slice(0, 3)
    },
    
    // 过滤后的产品列表
    filteredProducts() {
      let filtered = this.products
      
      // 搜索过滤
      if (this.searchKeyword) {
        filtered = filtered.filter(product => 
          product.productName.toLowerCase().includes(this.searchKeyword.toLowerCase())
        )
      }
      
      // 分类过滤
      if (this.activeCategory !== 'all') {
        // 这里可以根据实际需求添加分类逻辑
        // 示例：根据产品名称关键词分类
        if (this.activeCategory === 'fixed') {
          filtered = filtered.filter(product => 
            product.productName.includes('定期') || product.productName.includes('整存')
          )
        } else if (this.activeCategory === 'notice') {
          filtered = filtered.filter(product => 
            product.productName.includes('通知') || product.productName.includes('活期')
          )
        }
      }
      
      // 排序
      return this.sortProductList(filtered)
    }
  },
  created() {
    this.loadProducts()
  },
  mounted() {
    // 添加原生事件监听器确保点击事件被正确捕获
    if (this.$refs.userCenterBtn) {
      this.$refs.userCenterBtn.addEventListener('click', this.goToUserCenter)
    }
  },
  beforeUnmount() {
    // 清理事件监听器
    if (this.$refs.userCenterBtn) {
      this.$refs.userCenterBtn.removeEventListener('click', this.goToUserCenter)
    }
  },
  methods: {
    async loadProducts() {
      this.loading = true
      try {
        // 由于这是管理端API，需要访问response.data
        const response = await productApi.getProductList()
        // 管理端API返回 {success: true, data: [...]} 结构
        this.products = (response.data && response.data.data) || []
      } catch (error) {
        console.error('加载产品列表失败', error)
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      // 搜索功能已在computed中实现
    },
    
    switchCategory(category) {
      this.activeCategory = category
    },
    
    sortProducts() {
      // 排序功能已在computed中实现
    },
    
    sortProductList(products) {
      return [...products].sort((a, b) => {
        if (this.sortBy === 'rate') {
          return parseFloat(b.annualRate) - parseFloat(a.annualRate)
        } else if (this.sortBy === 'term') {
          return a.duration - b.duration
        }
        return 0
      })
    },
    
    goToDetail(productCode) {
      this.$router.push(`/product/${productCode}`)
    },
    
    goToUserCenter() {
      this.$router.push('/user-center')
    }
  }
}
</script>

<style scoped>
.product-list-page {
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

.user-center-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
}

/* 搜索栏 */
.search-container {
  padding: 16px;
  background-color: white;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 20px;
  padding: 8px 16px;
}

.search-icon {
  margin-right: 8px;
  color: #999;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  color: #333;
}

/* 分类标签 */
.category-tabs {
  background-color: white;
  display: flex;
  padding: 0 16px;
  border-bottom: 1px solid #e5e5e5;
  overflow-x: auto;
}

.tab {
  padding: 16px 12px;
  margin-right: 20px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.3s;
  position: relative;
  color: #666;
}

.tab.active {
  color: #667eea;
  font-weight: 600;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

/* 推荐产品区域 */
.recommended-section {
  background-color: white;
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
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.recommended-products {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.recommended-product-card {
  flex: 0 0 auto;
  min-width: 140px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 16px;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.recommended-product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.recommended-product-card .product-name {
  color: white;
  font-size: 13px;
}

.hot-tag {
  background-color: #ff6b6b;
  color: white;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
}

.recommended-product-card .product-rate {
  margin-bottom: 4px;
}

.recommended-product-card .rate-value {
  font-size: 24px;
  font-weight: 600;
  color: white;
}

.recommended-product-card .rate-unit {
  font-size: 14px;
  color: white;
  margin-left: 2px;
}

.recommended-product-card .rate-label {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.8);
}

/* 产品列表区域 */
.product-list-section {
  background-color: white;
  margin: 0 16px 16px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.sort-select {
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  outline: none;
  background-color: white;
}

/* 产品列表 */
.product-list {
  padding: 0;
}

.loading, .empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 14px;
}

/* 产品卡片 */
.product-card {
  background-color: #fafafa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #eee;
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.product-card:active {
  transform: scale(0.98);
}

.product-card .product-header {
  margin-bottom: 16px;
}

.product-card .product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.product-period {
  font-size: 12px;
  color: #666;
  background-color: #f0f0f0;
  padding: 4px 10px;
  border-radius: 12px;
}

.product-rate {
  margin-bottom: 4px;
}

.rate-value {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
}

.rate-unit {
  font-size: 16px;
  color: #667eea;
  margin-left: 4px;
}

.rate-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 16px;
}

.product-info {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.arrow {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  color: #ccc;
}
</style>