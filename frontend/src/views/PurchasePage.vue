<template>
  <div class="purchase-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">{{ product ? product.productName : '产品购买' }}</h1>
      <div class="help-btn" @click="showHelp">
        <span>?</span>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="product" class="content">
      <!-- 购买步骤指示器 -->
      <div class="steps-indicator">
        <div 
          v-for="(step, index) in steps" 
          :key="index"
          :class="['step', { active: currentStep === index, completed: currentStep > index }]"
        >
          <div class="step-number">{{ index + 1 }}</div>
          <div class="step-label">{{ step }}</div>
        </div>
      </div>

      <!-- 步骤1: 产品信息确认 -->
      <div v-show="currentStep === 0" class="step-content">
        <div class="product-info-card">
          <div class="card-header">
            <h3>产品信息确认</h3>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">产品名称</span>
              <span class="value">{{ product.productName }}</span>
            </div>
            <div class="info-item">
              <span class="label">年化利率</span>
              <span class="value highlight">{{ product.annualRate }}%</span>
            </div>
            <div class="info-item">
              <span class="label">存款期限</span>
              <span class="value">{{ product.durationDesc }}</span>
            </div>
            <div class="info-item">
              <span class="label">起存金额</span>
              <span class="value">{{ product.minAmount }}元</span>
            </div>
            <div class="info-item">
              <span class="label">递增金额</span>
              <span class="value">{{ product.incrementAmount }}元</span>
            </div>
            <div class="info-item">
              <span class="label">剩余额度</span>
              <span class="value">{{ product.inventory }}份</span>
            </div>
          </div>
        </div>

        <div class="agreement-section">
          <label class="checkbox-label">
            <input type="checkbox" v-model="agreedToTerms" class="checkbox">
            我已阅读并同意<a href="#" @click.prevent="showAgreement">《产品购买协议》</a>
          </label>
        </div>
      </div>

      <!-- 步骤2: 金额输入 -->
      <div v-show="currentStep === 1" class="step-content">
        <div class="amount-card">
          <div class="card-header">
            <h3>存入金额</h3>
          </div>
          <div class="amount-input-wrapper">
            <span class="currency">¥</span>
            <input 
              type="number" 
              v-model="amount" 
              placeholder="请输入存入金额"
              class="amount-input"
              @input="calculateIncome"
              @focus="onAmountFocus"
              @blur="onAmountBlur"
            />
          </div>
          <div class="quick-amounts">
            <div 
              v-for="quickAmount in quickAmounts" 
              :key="quickAmount"
              :class="['quick-amount', { active: amount == quickAmount }]"
              @click="selectQuickAmount(quickAmount)"
            >
              {{ quickAmount }}元
            </div>
          </div>
          <div class="amount-tips">
            起存{{ product.minAmount }}元，{{ product.incrementAmount }}元递增
          </div>
          <div v-if="amountError" class="error-message">{{ amountError }}</div>
        </div>

        <!-- 预期收益 -->
        <div class="income-card" v-if="expectedIncome > 0">
          <div class="income-row">
            <span class="label">预期收益</span>
            <span class="income-value">¥ {{ expectedIncome.toFixed(2) }}</span>
          </div>
          <div class="income-row">
            <span class="label">到期本息</span>
            <span class="income-value total">¥ {{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 步骤3: 用户信息确认 -->
      <div v-show="currentStep === 2" class="step-content">
        <div class="user-card">
          <div class="card-header">
            <h3>用户信息确认</h3>
          </div>
          <div class="user-form">
            <div class="form-group">
              <label>用户ID *</label>
              <input 
                type="text" 
                v-model="userId" 
                placeholder="请输入用户ID（如：13800138000）"
                class="form-input"
                :class="{ 'error': userIdError }"
              />
              <div v-if="userIdError" class="error-message">{{ userIdError }}</div>
            </div>
            
            <div class="form-group">
              <label>验证码</label>
              <div class="captcha-wrapper">
                <input 
                  type="text" 
                  v-model="captcha" 
                  placeholder="请输入验证码"
                  class="form-input captcha-input"
                  :class="{ 'error': captchaError }"
                />
                <button 
                  :class="['captcha-btn', { disabled: isCaptchaSending }]"
                  @click="sendCaptcha"
                  :disabled="isCaptchaSending"
                >
                  {{ isCaptchaSending ? `${countdown}秒后重发` : '发送验证码' }}
                </button>
              </div>
              <div v-if="captchaError" class="error-message">{{ captchaError }}</div>
            </div>
          </div>
          
          <div class="tips">
            <p>· 测试账号：13800138000 或 13900139000</p>
            <p>· 验证码统一为：123456</p>
          </div>
        </div>
      </div>

      <!-- 步骤4: 确认购买 -->
      <div v-show="currentStep === 3" class="step-content">
        <div class="summary-card">
          <div class="card-header">
            <h3>购买信息确认</h3>
          </div>
          <div class="summary-grid">
            <div class="summary-item">
              <span class="label">产品名称</span>
              <span class="value">{{ product.productName }}</span>
            </div>
            <div class="summary-item">
              <span class="label">年化利率</span>
              <span class="value">{{ product.annualRate }}%</span>
            </div>
            <div class="summary-item">
              <span class="label">存入金额</span>
              <span class="value">¥ {{ amount }}</span>
            </div>
            <div class="summary-item">
              <span class="label">预期收益</span>
              <span class="value income">¥ {{ expectedIncome.toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">用户ID</span>
              <span class="value">{{ userId }}</span>
            </div>
          </div>
        </div>

        <!-- 备注信息 -->
        <div class="remark-card">
          <div class="card-header">
            <h3>备注信息</h3>
          </div>
          <div class="remark-form">
            <textarea 
              v-model="remark" 
              placeholder="请输入备注信息（可选）"
              class="remark-input"
              maxlength="200"
              rows="3"
            ></textarea>
            <div class="remark-count">{{ remark.length }}/200</div>
          </div>
        </div>

        <div class="agreement-section">
          <label class="checkbox-label">
            <input type="checkbox" v-model="finalAgreed" class="checkbox">
            我确认以上信息无误，同意进行购买操作
          </label>
        </div>
      </div>

      <!-- 购买须知 -->
      <div class="notice-card">
        <div class="card-header">
          <h3>购买须知</h3>
        </div>
        <div class="notice-list">
          <div class="notice-item">
            <span class="dot">·</span>
            <span>存款起存金额：{{ product.minAmount }}元，支持{{ product.incrementAmount }}元递增</span>
          </div>
          <div class="notice-item">
            <span class="dot">·</span>
            <span>单人购买限额：{{ product.personLimit }}元</span>
          </div>
          <div class="notice-item">
            <span class="dot">·</span>
            <span>单日购买限额：{{ product.dailyLimit }}元</span>
          </div>
          <div class="notice-item">
            <span class="dot">·</span>
            <span>每日22:00前存入，当日起息（含节假日）</span>
          </div>
          <div class="notice-item">
            <span class="dot">·</span>
            <span>产品剩余额度：{{ product.inventory }}份，先到先得</span>
          </div>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="bottom-bar">
        <button 
          v-if="currentStep > 0" 
          class="prev-btn" 
          @click="prevStep"
        >
          上一步
        </button>
        <button 
          class="next-btn" 
          @click="nextStep"
          :disabled="submitting"
        >
          {{ currentStep === steps.length - 1 ? (submitting ? '提交中...' : '确认购买') : '下一步' }}
        </button>
      </div>
    </div>

    <!-- 协议弹窗 -->
    <div v-if="showAgreementModal" class="modal-overlay" @click="closeAgreement">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>产品购买协议</h3>
          <button class="close-btn" @click="closeAgreement">×</button>
        </div>
        <div class="modal-body">
          <p>这是产品购买协议的内容...</p>
          <p>用户在购买本产品前应仔细阅读并理解以下条款：</p>
          <ol>
            <li>本产品为银行存款产品，受存款保险保护</li>
            <li>用户需确保提供的信息真实、准确、完整</li>
            <li>购买成功后资金将在一个工作日内划转</li>
            <li>产品收益按约定利率计算，到期一次性支付</li>
            <li>用户可在产品到期前申请提前支取，但可能影响收益</li>
          </ol>
        </div>
        <div class="modal-footer">
          <button class="agree-btn" @click="agreeToAgreement">我已阅读并同意</button>
        </div>
      </div>
    </div>

    <!-- 帮助弹窗 -->
    <div v-if="showHelpModal" class="modal-overlay" @click="closeHelp">
      <div class="modal-content help-modal" @click.stop>
        <div class="modal-header">
          <h3>购买帮助</h3>
          <button class="close-btn" @click="closeHelp">×</button>
        </div>
        <div class="modal-body">
          <div class="help-section">
            <h4>如何购买产品？</h4>
            <p>1. 确认产品信息</p>
            <p>2. 输入购买金额</p>
            <p>3. 确认用户信息</p>
            <p>4. 阅读并同意协议</p>
            <p>5. 提交购买申请</p>
          </div>
          <div class="help-section">
            <h4>注意事项</h4>
            <p>· 购买前请确保账户余额充足</p>
            <p>· 购买金额需符合起存和递增要求</p>
            <p>· 购买成功后不可撤销</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { productApi, purchaseApi } from '@/api'

export default {
  name: 'PurchasePage',
  data() {
    return {
      loading: false,
      submitting: false,
      product: null,
      userId: '',
      amount: '',
      expectedIncome: 0,
      totalAmount: 0,
      captcha: '',
      agreedToTerms: false,
      finalAgreed: false,
      currentStep: 0,
      steps: ['产品确认', '金额输入', '信息确认', '确认购买'],
      isCaptchaSending: false,
      countdown: 60,
      countdownTimer: null,
      showAgreementModal: false,
      showHelpModal: false,
      amountError: '',
      userIdError: '',
      captchaError: '',
      remark: '', // 添加备注信息
      // 快捷金额选项
      quickAmounts: [10000, 50000, 100000, 500000]
    }
  },
  created() {
    this.loadProductDetail()
  },
  beforeUnmount() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },
  methods: {
    async loadProductDetail() {
      this.loading = true
      try {
        const productCode = this.$route.params.code
        // 由于这是管理端API，需要访问response.data.data
        const response = await productApi.getProductDetail(productCode)
        // 管理端API返回 {success: true, data: {...}} 结构
        this.product = (response.data && response.data.data) || null
        // 设置默认金额为起存金额
        if (this.product) {
          this.amount = this.product.minAmount
          this.calculateIncome()
        }
      } catch (error) {
        console.error('加载产品详情失败', error)
        this.$router.back()
      } finally {
        this.loading = false
      }
    },
    
    // 计算预期收益
    calculateIncome() {
      if (!this.amount || !this.product) {
        this.expectedIncome = 0
        this.totalAmount = 0
        return
      }
      
      const principal = parseFloat(this.amount)
      const rate = parseFloat(this.product.annualRate) / 100
      const days = this.product.duration
      
      // 收益 = 本金 × 年化利率 × 天数 / 365
      this.expectedIncome = principal * rate * days / 365
      this.totalAmount = principal + this.expectedIncome
      
      // 清除金额错误提示
      this.amountError = ''
    },
    
    // 选择快捷金额
    selectQuickAmount(amount) {
      this.amount = amount
      this.calculateIncome()
    },
    
    // 金额输入获得焦点
    onAmountFocus() {
      // 可以在这里添加一些交互效果
    },
    
    // 金额输入失去焦点
    onAmountBlur() {
      this.validateAmount()
    },
    
    // 验证金额
    validateAmount() {
      this.amountError = ''
      
      if (!this.amount || parseFloat(this.amount) <= 0) {
        this.amountError = '请输入有效的存入金额'
        return false
      }
      
      const amount = parseFloat(this.amount)
      
      // 校验起存金额
      if (amount < parseFloat(this.product.minAmount)) {
        this.amountError = `存入金额不能低于起存金额${this.product.minAmount}元`
        return false
      }
      
      // 校验递增金额
      const diff = amount - parseFloat(this.product.minAmount)
      const increment = parseFloat(this.product.incrementAmount)
      if (diff > 0 && diff % increment !== 0) {
        this.amountError = `存入金额必须是${increment}元的整数倍`
        return false
      }
      
      // 校验单人限额
      if (amount > parseFloat(this.product.personLimit)) {
        this.amountError = `存入金额不能超过单人限额${this.product.personLimit}元`
        return false
      }
      
      return true
    },
    
    // 发送验证码
    sendCaptcha() {
      if (this.isCaptchaSending) return
      
      // 简单验证用户ID
      if (!this.userId.trim()) {
        this.userIdError = '请输入用户ID'
        return
      }
      
      this.userIdError = ''
      this.isCaptchaSending = true
      this.countdown = 60
      
      // 启动倒计时
      this.countdownTimer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          this.isCaptchaSending = false
          clearInterval(this.countdownTimer)
        }
      }, 1000)
      
      // 模拟发送验证码成功
      setTimeout(() => {
        alert('验证码已发送，请使用测试验证码：123456')
      }, 500)
    },
    
    // 显示协议
    showAgreement() {
      this.showAgreementModal = true
    },
    
    // 关闭协议
    closeAgreement() {
      this.showAgreementModal = false
    },
    
    // 同意协议
    agreeToAgreement() {
      this.agreedToTerms = true
      this.closeAgreement()
    },
    
    // 显示帮助
    showHelp() {
      this.showHelpModal = true
    },
    
    // 关闭帮助
    closeHelp() {
      this.showHelpModal = false
    },
    
    // 上一步
    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--
      }
    },
    
    // 下一步
    nextStep() {
      // 根据当前步骤进行不同的验证
      switch (this.currentStep) {
        case 0: // 产品确认
          if (!this.agreedToTerms) {
            alert('请先阅读并同意产品购买协议')
            return
          }
          break
          
        case 1: // 金额输入
          if (!this.validateAmount()) {
            return
          }
          break
          
        case 2: // 用户信息确认
          if (!this.validateUserInfo()) {
            return
          }
          break
          
        case 3: // 确认购买
          if (!this.finalAgreed) {
            alert('请确认购买信息无误')
            return
          }
          this.confirmPurchase()
          return
      }
      
      // 移动到下一步
      if (this.currentStep < this.steps.length - 1) {
        this.currentStep++
      }
    },
    
    // 验证用户信息
    validateUserInfo() {
      this.userIdError = ''
      this.captchaError = ''
      
      // 校验用户ID
      if (!this.userId.trim()) {
        this.userIdError = '请输入用户ID'
        return false
      }
      
      // 校验验证码
      if (!this.captcha.trim()) {
        this.captchaError = '请输入验证码'
        return false
      }
      
      // 测试验证码
      if (this.captcha !== '123456') {
        this.captchaError = '验证码错误'
        return false
      }
      
      return true
    },
    
    // 确认购买
    async confirmPurchase() {
      // 提交购买请求
      this.submitting = true
      try {
        const response = await purchaseApi.purchaseProduct({
          userId: this.userId.trim(),
          productCode: this.product.productCode,
          amount: parseFloat(this.amount),
          remark: this.remark.trim() // 添加备注信息
        })
        
        // 检查后端返回的结果是否成功
        // 购买API现在返回完整的响应对象
        if (response && response.data) {
          const responseData = response.data
          
          // 检查是否为后端返回的错误格式 {code: 500, message: "...", data: {...}}
          if (responseData && typeof responseData === 'object' && responseData.code === 500 && responseData.data) {
            // 这是后端错误响应，从data中提取错误信息
            const errorData = responseData.data
            if (errorData && errorData.success === false) {
              const errorMessage = errorData.message || '未知错误'
              const errorType = errorData.errorType || 'PURCHASE_ERROR'
              
              this.$router.push({
                name: 'PurchaseSuccess',
                query: { 
                  result: encodeURIComponent(JSON.stringify({
                    success: false,
                    message: errorMessage,
                    errorType: errorType
                  }))
                }
              })
            }
          }
          // 检查data中是否包含错误信息
          else if (responseData && responseData.success === false) {
            // 购买失败，跳转到失败页面
            const errorMessage = responseData.message || '未知错误'
            const errorType = responseData.errorType || 'PURCHASE_ERROR'
            
            this.$router.push({
              name: 'PurchaseSuccess',
              query: { 
                result: encodeURIComponent(JSON.stringify({
                  success: false,
                  message: errorMessage,
                  errorType: errorType
                }))
              }
            })
          } else {
            // 购买成功，跳转到成功页面
            this.$router.push({
              name: 'PurchaseSuccess',
              query: { result: encodeURIComponent(JSON.stringify(responseData.data || responseData)) }
            })
          }
        } else {
          // 购买失败，跳转到失败页面
          this.$router.push({
            name: 'PurchaseSuccess',
            query: { 
              result: encodeURIComponent(JSON.stringify({
                success: false,
                message: '购买失败：未收到有效响应',
                errorType: 'PURCHASE_ERROR'
              }))
            }
          })
        }
      } catch (error) {
        console.error('购买失败', error)
        // 处理异常情况，跳转到失败页面
        let errorMessage = '购买失败: ' + (error.message || '未知错误')
        let errorType = 'NETWORK_ERROR'
        
        // 检查错误类型
        if (error.response && error.response.data) {
          const errorData = error.response.data
          if (errorData.message) {
            errorMessage = errorData.message
          }
          // 尝试从响应中获取错误类型 - 现在从data属性中获取
          if (errorData.data && errorData.data.errorType) {
            errorType = errorData.data.errorType
          } else if (errorData.errorType) {
            errorType = errorData.errorType
          }
        }
        
        this.$router.push({
          name: 'PurchaseSuccess',
          query: { 
            result: encodeURIComponent(JSON.stringify({
              success: false,
              message: errorMessage,
              errorType: errorType
            }))
          }
        })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.purchase-page {
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

.help-btn {
  font-size: 18px;
  cursor: pointer;
  padding: 4px 12px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 步骤指示器 */
.steps-indicator {
  display: flex;
  justify-content: space-between;
  padding: 20px 16px 10px;
  background-color: white;
  margin-bottom: 16px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}

.step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 12px;
  left: 50%;
  right: -50%;
  height: 2px;
  background-color: #e5e5e5;
  z-index: 1;
}

.step.active::after,
.step.completed::after {
  background-color: #667eea;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #e5e5e5;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
  position: relative;
  z-index: 2;
}

.step.active .step-number {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.step.completed .step-number {
  background-color: #667eea;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #999;
}

.step.active .step-label,
.step.completed .step-label {
  color: #333;
  font-weight: 500;
}

/* 内容区域 */
.content {
  padding: 0 16px 16px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 卡片通用样式 */
.product-info-card,
.amount-card,
.user-card,
.summary-card,
.notice-card,
.remark-card {
  background-color: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 产品信息 */
.info-grid {
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.info-value.highlight {
  color: #667eea;
  font-size: 16px;
  font-weight: 600;
}

/* 协议区域 */
.agreement-section {
  padding: 0 20px 20px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.checkbox {
  margin-right: 8px;
  margin-top: 2px;
}

.checkbox-label a {
  color: #667eea;
  text-decoration: none;
}

.checkbox-label a:hover {
  text-decoration: underline;
}

/* 金额输入 */
.amount-input-wrapper {
  display: flex;
  align-items: center;
  border-bottom: 2px solid #e5e5e5;
  padding: 0 20px 16px;
  margin: 0 20px;
}

.currency {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-right: 8px;
}

.amount-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 32px;
  font-weight: 600;
  color: #333;
}

.amount-input::placeholder {
  color: #ccc;
}

.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 20px 20px 0;
}

.quick-amount {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-amount:hover,
.quick-amount.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.amount-tips {
  font-size: 12px;
  color: #999;
  padding: 16px 20px 20px;
}

/* 预期收益 */
.income-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.income-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.income-row:last-child {
  margin-bottom: 0;
}

.income-row .label {
  font-size: 14px;
  color: #666;
}

.income-value {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.income-value.total {
  color: #5dd39e;
}

/* 用户信息 */
.user-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.form-input.error {
  border-color: #ff6b6b;
}

.captcha-wrapper {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-btn {
  padding: 12px 16px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.captcha-btn:hover:not(.disabled) {
  background-color: #5568d3;
}

.captcha-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.tips {
  font-size: 12px;
  color: #999;
  padding: 0 20px 20px;
  line-height: 1.6;
}

/* 购买信息确认 */
.summary-grid {
  padding: 20px;
  display: grid;
  gap: 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-item .label {
  font-size: 14px;
  color: #666;
}

.summary-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.summary-item .value.income {
  color: #5dd39e;
  font-weight: 600;
}

/* 备注信息 */
.remark-card {
  background-color: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.remark-form {
  padding: 20px;
}

.remark-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  resize: vertical;
  transition: all 0.3s;
  font-family: inherit;
}

.remark-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.remark-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 购买须知 */
.notice-list {
  padding: 20px;
  font-size: 13px;
  color: #666;
  line-height: 1.8;
}

.notice-item {
  display: flex;
  margin-bottom: 8px;
}

.dot {
  margin-right: 6px;
}

/* 错误信息 */
.error-message {
  font-size: 12px;
  color: #ff6b6b;
  margin-top: 6px;
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
  gap: 12px;
}

.prev-btn,
.next-btn {
  padding: 16px;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
}

.prev-btn {
  background-color: #f5f5f5;
  color: #666;
}

.prev-btn:hover {
  background-color: #e5e5e5;
}

.next-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.next-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.next-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.next-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.modal-body ol {
  padding-left: 20px;
  margin: 16px 0;
}

.modal-body li {
  margin-bottom: 8px;
}

.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #f5f5f5;
  text-align: center;
}

.agree-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.agree-btn:hover {
  opacity: 0.9;
}

.help-modal .help-section {
  margin-bottom: 24px;
}

.help-modal .help-section:last-child {
  margin-bottom: 0;
}

.help-modal h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.help-modal p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}
</style>