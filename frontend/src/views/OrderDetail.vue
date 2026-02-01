<template>
  <div class="order-detail-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">订单详情</h1>
      <div class="header-placeholder"></div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="order" class="content">
      <!-- 订单状态 -->
      <div class="status-card">
        <div class="status-icon" :class="order.status">{{ getOrderStatusText(order.status) }}</div>
        <div class="status-text">{{ getOrderStatusText(order.status) }}</div>
      </div>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="card-title">基本信息</div>
        
        <div class="detail-item">
          <span class="label">订单号</span>
          <span class="value order-no">{{ order.orderNo }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">产品名称</span>
          <span class="value">{{ order.productName }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">购买金额</span>
          <span class="value">¥{{ formatCurrency(order.amount) }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">年化利率</span>
          <span class="value">{{ order.annualRate }}%</span>
        </div>
        
        <div class="detail-item">
          <span class="label">存款期限</span>
          <span class="value">{{ order.duration }}天</span>
        </div>
        
        <div class="detail-item">
          <span class="label">预期收益</span>
          <span class="value income">¥{{ formatCurrency(order.expectedIncome) }}</span>
        </div>
      </div>

      <!-- 时间信息 -->
      <div class="info-card">
        <div class="card-title">时间信息</div>
        
        <div class="detail-item">
          <span class="label">起息日期</span>
          <span class="value">{{ formatDate(order.startDate) }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">到期日期</span>
          <span class="value">{{ formatDate(order.endDate) }}</span>
        </div>
        
        <div class="detail-item">
          <span class="label">下单时间</span>
          <span class="value">{{ formatDateTime(order.createTime) }}</span>
        </div>
        
        <div class="detail-item" v-if="order.updateTime">
          <span class="label">更新时间</span>
          <span class="value">{{ formatDateTime(order.updateTime) }}</span>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="info-card">
        <div class="card-title">备注信息</div>
        <div v-if="!isEditingRemark" class="remark-content">
          <div v-if="order.remark">{{ order.remark }}</div>
          <div v-else class="no-remark">暂无备注</div>
          <button class="edit-remark-btn" @click="startEditRemark">编辑备注</button>
        </div>
        <div v-else class="remark-edit-form">
          <textarea 
            v-model="editedRemark" 
            placeholder="请输入备注信息"
            class="remark-input"
            maxlength="200"
            rows="3"
          ></textarea>
          <div class="remark-count">{{ editedRemark.length }}/200</div>
          <div class="remark-actions">
            <button class="cancel-btn" @click="cancelEditRemark">取消</button>
            <button class="save-btn" @click="saveRemark" :disabled="savingRemark">
              {{ savingRemark ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="canCancelOrder">
        <button class="cancel-order-btn" @click="showCancelConfirm">取消订单</button>
      </div>
    </div>
    
    <!-- 取消订单确认对话框 -->
    <div class="modal-overlay" v-if="showCancelConfirmFlag">
      <div class="modal-content confirm-modal">
        <div class="modal-header">
          <h3>确认取消订单</h3>
          <button class="modal-close" @click="hideCancelConfirm">×</button>
        </div>
        <div class="modal-body">
          <p>您确定要取消此订单吗？</p>
          <p>订单号：{{ order.orderNo }}</p>
          <p>产品名称：{{ order.productName }}</p>
          <p>购买金额：¥{{ formatCurrency(order.amount) }}</p>
          <p class="warning-text">取消后资金将退回您的账户</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="hideCancelConfirm">取消</button>
          <button class="btn-confirm btn-danger" @click="confirmCancelOrder" :disabled="cancellingOrder">
            {{ cancellingOrder ? '取消中...' : '确认取消' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { purchaseApi } from '@/api'

export default {
  name: 'OrderDetail',
  data() {
    return {
      order: null,
      loading: false,
      isEditingRemark: false, // 是否正在编辑备注
      editedRemark: '', // 编辑中的备注
      savingRemark: false, // 是否正在保存备注
      showCancelConfirmFlag: false, // 是否显示取消确认对话框
      cancellingOrder: false // 是否正在取消订单
    }
  },
  computed: {
    // 判断是否可以取消订单
    canCancelOrder() {
      if (!this.order) return false
      // 只有待支付或处理中的订单可以取消
      return this.order.status === 'PENDING' || this.order.status === 'PROCESSING'
    }
  },
  created() {
    this.loadOrderDetail()
  },
  methods: {
    async loadOrderDetail() {
      const orderNo = this.$route.params.orderNo
      if (!orderNo) {
        this.$router.back()
        return
      }
      
      this.loading = true
      try {
        // 使用后端提供的根据订单号查询订单详情的接口
        const response = await purchaseApi.getOrderDetail(orderNo)
        // 客户端API返回Result对象 {code: 200, message: "...", data: {...}}
        // 从响应中提取实际订单数据
        this.order = (response && response.data) || null
        // 初始化编辑备注的值
        if (this.order) {
          this.editedRemark = this.order.remark || ''
        }
      } catch (error) {
        console.error('加载订单详情失败', error)
        alert('加载订单详情失败: ' + error.message)
      } finally {
        this.loading = false
      }
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'PENDING': '待支付',
        'PROCESSING': '处理中',
        'SUCCESS': '已完成',
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
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 开始编辑备注
    startEditRemark() {
      this.isEditingRemark = true
      this.editedRemark = this.order.remark || ''
    },
    
    // 取消编辑备注
    cancelEditRemark() {
      this.isEditingRemark = false
      this.editedRemark = this.order.remark || ''
    },
    
    // 保存备注
    async saveRemark() {
      if (this.savingRemark) return
      
      this.savingRemark = true
      try {
        // 调用后端API保存备注
        const response = await purchaseApi.updateOrderRemark(this.order.orderNo, this.editedRemark)
        // 客户端API返回处理后的数据，直接使用响应
        if (response && response.success) {
          // 更新本地订单数据
          this.order.remark = this.editedRemark
          this.isEditingRemark = false
          
          alert(response.message || '备注保存成功')
        } else {
          alert(response ? response.message : '备注保存失败')
        }
      } catch (error) {
        console.error('保存备注失败', error)
        alert('保存备注失败: ' + error.message)
      } finally {
        this.savingRemark = false
      }
    },
    
    // 显示取消订单确认对话框
    showCancelConfirm() {
      this.showCancelConfirmFlag = true
    },
    
    // 隐藏取消订单确认对话框
    hideCancelConfirm() {
      this.showCancelConfirmFlag = false
    },
    
    // 确认取消订单
    async confirmCancelOrder() {
      if (this.cancellingOrder) return
      
      this.cancellingOrder = true
      try {
        // 从localStorage获取用户信息
        const userInfoStr = localStorage.getItem('userInfo')
        if (!userInfoStr) {
          throw new Error('用户未登录')
        }
        
        const userInfo = JSON.parse(userInfoStr)
        
        // 调用后端API取消订单
        const response = await purchaseApi.cancelOrder(this.order.orderNo, userInfo.userId)
        // 客户端API返回处理后的数据，直接使用响应
        if (response && response.success) {
          // 更新本地订单状态
          this.order.status = 'CANCELLED'
          this.order.updateTime = new Date().toISOString()
          this.order.endDate = new Date().toISOString() // 设置结束时间为当前时间
          
          // 隐藏确认对话框
          this.hideCancelConfirm()
          
          alert(response.message || '订单取消成功，资金已退回您的账户')
        } else {
          alert(response ? response.message : '订单取消失败')
        }
      } catch (error) {
        console.error('取消订单失败', error)
        alert('取消订单失败: ' + error.message)
      } finally {
        this.cancellingOrder = false
      }
    }
  }
}
</script>

<style scoped>
.order-detail-page {
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

/* 内容区域 */
.content {
  padding: 16px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 订单状态 */
.status-card {
  background: white;
  border-radius: 12px;
  padding: 30px 20px;
  margin-bottom: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.status-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  margin: 0 auto 16px;
  color: white;
}

.status-icon.PENDING {
  background: linear-gradient(135deg, #f1c40f 0%, #f39c12 100%);
}

.status-icon.PROCESSING {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
}

.status-icon.SUCCESS {
  background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
}

.status-icon.FAILED {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}

.status-icon.CANCELLED {
  background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.label {
  color: #999;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
  text-align: right;
  flex: 1;
  margin-left: 20px;
  word-break: break-all;
}

.value.income {
  color: #5dd39e;
  font-weight: 600;
}

.order-no {
  font-family: monospace;
  font-size: 12px;
}

/* 备注信息 */
.remark-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  position: relative;
  padding-bottom: 30px;
}

.no-remark {
  color: #999;
  font-style: italic;
}

.edit-remark-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 12px;
  cursor: pointer;
}

.remark-edit-form {
  font-size: 14px;
  color: #333;
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
  margin-bottom: 8px;
}

.remark-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.remark-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-bottom: 16px;
}

.remark-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn,
.save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background-color: #e5e5e5;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.save-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.save-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 操作按钮 */
.action-buttons {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-align: center;
}

.cancel-order-btn {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  color: white;
  border: none;
  border-radius: 24px;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-order-btn:hover {
  opacity: 0.9;
}

/* 模态框 */
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
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.confirm-modal .modal-body {
  padding: 20px;
}

.confirm-modal .modal-body p {
  margin: 10px 0;
  color: #333;
}

.warning-text {
  color: #e74c3c !important;
  font-weight: 600;
  margin-top: 15px !important;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-close:hover {
  color: #666;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn-cancel,
.btn-confirm {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background: #545b62;
}

.btn-confirm {
  background: #007bff;
  color: white;
}

.btn-confirm:hover {
  background: #0056b3;
}

.btn-confirm:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-danger {
  background: #dc3545 !important;
}

.btn-danger:hover {
  background: #c82333 !important;
}
</style>