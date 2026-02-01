<template>
  <div class="success-page">
    <!-- 购买失败提示 - 显示具体错误信息 -->
    <div class="error-card" v-if="!result || (result && result.success === false)">
      <div class="error-icon">⚠️</div>
      <div class="error-title">交易失败</div>
      <div class="error-message">{{ getErrorMessage() }}</div>
      <div class="error-actions">
        <button class="retry-btn" @click="goToProductList">返回产品列表</button>
        <button class="back-btn" @click="goBack">返回上一页</button>
      </div>
    </div>
    
    <!-- 成功图标 -->
    <div class="success-icon" v-else-if="result && result.orderNo">
      <div class="circle">
        <div class="checkmark">✓</div>
      </div>
    </div>

    <!-- 成功提示 -->
    <div class="success-title" v-if="result && result.orderNo">交易成功</div>
    <div class="success-amount" v-if="result && result.amount">¥{{ formatCurrency(result.amount) }}</div>

    <!-- 订单信息 -->
    <div class="order-card" v-if="result && result.orderNo">
      <div class="order-header">
        <span class="label">订单号</span>
        <span class="order-no">{{ result.orderNo }}</span>
      </div>

      <div class="order-detail">
        <div class="detail-row">
          <span class="label">存入金额</span>
          <span class="value">¥{{ formatCurrency(result.amount) }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">产品名称</span>
          <span class="value">{{ result.productName }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">存款期限</span>
          <span class="value">{{ result.duration }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">年化利率</span>
          <span class="value highlight">{{ result.annualRate }}%</span>
        </div>
        
        <div class="detail-row">
          <span class="label">预期收益</span>
          <span class="value income">¥{{ formatCurrency(result.expectedIncome) }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">起息日期</span>
          <span class="value">{{ formatDate(result.startDate) }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">到期日期</span>
          <span class="value">{{ formatDate(result.endDate) }}</span>
        </div>
        
        <div class="detail-row">
          <span class="label">购买时间</span>
          <span class="value">{{ formatDate(result.purchaseTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="actions" v-if="result && result.orderNo">
      <button class="primary-btn" @click="viewOrderDetail">查看订单详情</button>
      <button class="secondary-btn" @click="goToProductList">继续购买</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PurchaseSuccess',
  data() {
    return {
      result: null
    }
  },
  created() {
    this.loadResult()
  },
  methods: {
    loadResult() {
      // 从路由查询参数获取购买结果
      const resultParam = this.$route.query.result
      if (resultParam) {
        try {
          const parsedResult = JSON.parse(decodeURIComponent(resultParam))
          
          // 检查是否为后端返回的Result格式 {code: 500, message: "...", data: {...}}
          if (parsedResult && typeof parsedResult === 'object' && parsedResult.code === 500 && parsedResult.data) {
            // 这是后端错误响应，从data中提取错误信息
            this.result = {
              success: false,
              ...parsedResult.data  // 包含message和errorType
            }
          } 
          // 检查是否为错误结果（包含success: false）
          else if (parsedResult && typeof parsedResult === 'object' && parsedResult.success === false) {
            this.result = parsedResult
          } else if (parsedResult && typeof parsedResult === 'object' && parsedResult.hasOwnProperty('orderNo')) {
            this.result = parsedResult
          } else if (parsedResult && typeof parsedResult === 'object' && parsedResult.success === true) {
            // 如果是成功响应，但没有orderNo，则可能是在data属性中
            this.result = parsedResult
          } else {
            console.error('解析后的结果不是一个有效的对象或缺少必要属性', parsedResult)
            this.result = null
          }
        } catch (error) {
          console.error('解析购买结果失败', error)
          this.result = null
        }
      } else {
        this.result = null
      }
    },
    
    getErrorMessage() {
      if (!this.result || !this.result.success) {
        const message = this.result?.message || '购买失败，请稍后重试'
        const errorType = this.result?.errorType || 'UNKNOWN_ERROR'
        
        // 根据错误类型提供更具体的错误信息
        // 特殊处理：当错误类型是余额不足但消息是购买流程被拒绝的情况
        
        switch (errorType) {
          case 'ID_CHECK_FAILED':
            return '身份验证失败，请检查您的身份证信息是否正确'
          case 'USER_VERIFY_FAILED':
            return '用户信息校验失败，请确认您的账户状态是否正常'
          case 'AMOUNT_VALIDATE_FAILED':
            return '金额校验失败：' + message
          case 'BALANCE_INSUFFICIENT':
            return '账户余额不足，请充值后重试'
          case 'LIMIT_EXCEEDED':
            return '购买限额超限：' + message
          case 'INVENTORY_INSUFFICIENT':
            return '产品库存不足，无法购买'
          case 'REGION_RESTRICTED':
            return '您的地域不符合购买条件，无法购买此产品'
          case 'TAG_RESTRICTED':
            return '您不符合购买此产品的用户标签要求'
          case 'WHITELIST_DENIED':
            return '您不在白名单中，无法购买此产品'
          case 'PURCHASE_ERROR':
            return '购买流程执行失败，请稍后重试'
          case 'NETWORK_ERROR':
            return '网络连接失败，请检查网络后重试'
          default:
            return message
        }
      }
      return '购买失败，请稍后重试'
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    retry() {
      this.loadResult()
      // 如果仍然没有结果，则返回产品列表
      if (!this.result) {
        this.$router.push('/products')
      }
    },
    
    formatCurrency(amount) {
      if (amount === undefined || amount === null) return '0.00'
      return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    goToProductList() {
      this.$router.push('/products')
    },
    
    viewOrderDetail() {
      // 跳转到订单详情页面
      if (this.result && this.result.orderNo) {
        this.$router.push(`/order/${this.result.orderNo}`)
      }
    }
  }
}
</script>

<style scoped>
.success-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40px 16px 100px;
}

/* 成功图标 */
.success-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.circle {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #5dd39e 0%, #348f50 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(93, 211, 158, 0.3);
}

.checkmark {
  font-size: 48px;
  color: white;
  font-weight: bold;
}

/* 成功信息 */
.success-title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.success-amount {
  text-align: center;
  font-size: 40px;
  font-weight: 700;
  color: #e94e3d;
  margin-bottom: 32px;
}

/* 订单卡片 */
.order-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.order-header .label {
  font-size: 14px;
  color: #666;
}

.order-no {
  font-size: 13px;
  color: #999;
  font-family: 'Courier New', monospace;
}

.order-detail {
  /* 订单详情区域 */
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.detail-row .label {
  font-size: 14px;
  color: #666;
}

.detail-row .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.detail-row .value.highlight {
  color: #e94e3d;
  font-weight: 600;
}

.detail-row .value.income {
  color: #5dd39e;
  font-weight: 600;
}

/* 错误提示 */
.error-card {
  background-color: white;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.error-icon {
  font-size: 48px;
  color: #faad14;
  margin-bottom: 16px;
}

.error-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.error-message {
  font-size: 16px;
  color: #666;
  margin-bottom: 24px;
}

.error-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.retry-btn, .back-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.retry-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.back-btn {
  background-color: #f5f5f5;
  color: #333;
}

/* 操作按钮 */
.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0 16px;
}

.primary-btn, .secondary-btn {
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  text-align: center;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.secondary-btn {
  background-color: white;
  color: #667eea;
  border: 1px solid #667eea;
}
</style>