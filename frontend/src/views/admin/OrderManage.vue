<template>
  <div class="order-manage">
    <div class="page-header">
      <h2>订单管理</h2>
      <p class="page-desc">查看和管理所有用户订单</p>
    </div>
    
    <!-- 订单筛选 -->
    <div class="filter-section">
      <div class="filter-item">
        <label for="filterStatus">订单状态：</label>
        <select id="filterStatus" v-model="filterStatus" @change="loadOrders">
          <option value="">全部</option>
          <option value="PENDING">待支付</option>
          <option value="PROCESSING">处理中</option>
          <option value="SUCCESS">成功</option>
          <option value="FAILED">失败</option>
          <option value="CANCELLED">已取消</option>
        </select>
      </div>
      
      <div class="filter-item">
        <label for="filterUserId">用户ID：</label>
        <input type="text" id="filterUserId" v-model="filterUserId" placeholder="请输入用户ID">
      </div>
      
      <div class="filter-item">
        <label for="filterOrderNo">订单号：</label>
        <input type="text" id="filterOrderNo" v-model="filterOrderNo" placeholder="请输入订单号">
      </div>
      
      <button class="btn-search" @click="loadOrders">搜索</button>
      <button class="btn-reset" @click="resetFilters">重置</button>
    </div>
    
    <!-- 订单表格 -->
    <div class="table-container">
      <table class="order-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户ID</th>
            <th>产品名称</th>
            <th>购买金额</th>
            <th>预期收益</th>
            <th>下单时间</th>
            <th>订单状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="loading">加载中...</td>
          </tr>
          <tr v-else-if="paginatedOrders.length === 0">
            <td colspan="8" class="empty">暂无订单数据</td>
          </tr>
          <tr v-else v-for="order in paginatedOrders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.userId }}</td>
            <td>{{ order.productName }}</td>
            <td>¥{{ formatCurrency(order.amount) }}</td>
            <td>¥{{ formatCurrency(order.expectedIncome) }}</td>
            <td>{{ formatDate(order.createTime) }}</td>
            <td>
              <span class="status-badge" :class="order.status">
                {{ getOrderStatusText(order.status) }}
              </span>
            </td>
            <td>
              <button class="btn-action btn-detail" @click="viewOrderDetail(order)">详情</button>
              <button 
                class="btn-action btn-status" 
                @click="showStatusModal(order)"
                :disabled="order.status === 'SUCCESS'"
              >
                修改状态
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button 
        class="btn-page" 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="page-info">
        第 {{ currentPage }} 页，共 {{ totalPages }} 页
      </span>
      <button 
        class="btn-page" 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>
    
    <!-- 订单详情模态框 -->
    <div class="modal-overlay" v-if="showDetailModal">
      <div class="modal-content detail-modal">
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="modal-close" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="detail-row">
              <span class="label">订单号：</span>
              <span class="value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="detail-row">
              <span class="label">用户ID：</span>
              <span class="value">{{ selectedOrder.userId }}</span>
            </div>
            <div class="detail-row">
              <span class="label">产品名称：</span>
              <span class="value">{{ selectedOrder.productName }}</span>
            </div>
            <div class="detail-row">
              <span class="label">购买金额：</span>
              <span class="value">¥{{ formatCurrency(selectedOrder.amount) }}</span>
            </div>
            <div class="detail-row">
              <span class="label">年化利率：</span>
              <span class="value">{{ selectedOrder.annualRate }}%</span>
            </div>
            <div class="detail-row">
              <span class="label">存款期限：</span>
              <span class="value">{{ selectedOrder.duration }}天</span>
            </div>
            <div class="detail-row">
              <span class="label">预期收益：</span>
              <span class="value income">¥{{ formatCurrency(selectedOrder.expectedIncome) }}</span>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>时间信息</h4>
            <div class="detail-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatDateTime(selectedOrder.createTime) }}</span>
            </div>
            <div class="detail-row" v-if="selectedOrder.updateTime">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDateTime(selectedOrder.updateTime) }}</span>
            </div>
          </div>
          
          <div class="detail-section">
            <h4>状态信息</h4>
            <div class="detail-row">
              <span class="label">订单状态：</span>
              <span class="value">
                <span class="status-badge-inline" :class="selectedOrder.status">
                  {{ getOrderStatusText(selectedOrder.status) }}
                </span>
              </span>
            </div>
          </div>
          
          <div class="detail-section" v-if="selectedOrder.remark">
            <h4>备注信息</h4>
            <div class="detail-row">
              <span class="value remark">{{ selectedOrder.remark }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 状态修改模态框 -->
    <div class="modal-overlay" v-if="showStatusModalFlag">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改订单状态</h3>
          <button class="modal-close" @click="closeStatusModal">×</button>
        </div>
        <div class="modal-body">
          <p>订单号：{{ selectedOrder.orderNo }}</p>
          <p>当前状态：
            <span class="status-badge-inline" :class="selectedOrder.status">
              {{ getOrderStatusText(selectedOrder.status) }}
            </span>
          </p>
          <div class="form-group">
            <label for="newStatus">新状态：</label>
            <select id="newStatus" v-model="newStatus">
              <option value="PENDING">待支付</option>
              <option value="PROCESSING">处理中</option>
              <option value="SUCCESS">成功</option>
              <option value="FAILED">失败</option>
              <option value="CANCELLED">已取消</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeStatusModal">取消</button>
          <button class="btn-confirm" @click="updateOrderStatus" :disabled="updatingStatus">
            {{ updatingStatus ? '更新中...' : '确认更新' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { purchaseApi } from '@/api'

export default {
  name: 'OrderManage',
  data() {
    return {
      orders: [],
      loading: false,
      showStatusModalFlag: false,
      showDetailModal: false,
      selectedOrder: {},
      newStatus: '',
      updatingStatus: false,
      
      // 筛选条件
      filterStatus: '',
      filterUserId: '',
      filterOrderNo: '',
      
      // 分页
      currentPage: 1,
      pageSize: 10,
      totalOrders: 0
    }
  },
  
  computed: {
    filteredOrders() {
      // 确保 this.orders 是一个数组
      let result = Array.isArray(this.orders) ? this.orders : []
      console.log('过滤前的订单数据:', result.length)
      
      // 状态筛选
      if (this.filterStatus) {
        result = result.filter(order => order.status === this.filterStatus)
      }
      
      // 用户ID筛选
      if (this.filterUserId) {
        result = result.filter(order => order.userId.includes(this.filterUserId))
      }
      
      // 订单号筛选
      if (this.filterOrderNo) {
        result = result.filter(order => order.orderNo.includes(this.filterOrderNo))
      }
      
      console.log('过滤后的订单数据:', result.length)
      // 直接返回结果
      return result
    },
    
    paginatedOrders() {
      // 确保 filteredOrders 是一个数组
      const filtered = Array.isArray(this.filteredOrders) ? this.filteredOrders : []
      console.log('分页前的数据:', filtered.length)
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      const result = filtered.slice(start, end)
      console.log('分页后的数据:', result.length)
      // 直接返回结果
      return result
    },
    
    totalPages() {
      // 确保 filteredOrders 是一个数组
      const filtered = Array.isArray(this.filteredOrders) ? this.filteredOrders : []
      const pages = Math.ceil(filtered.length / this.pageSize)
      console.log('总页数:', pages)
      return pages
    }
  },
  
  created() {
    this.loadOrders()
  },
  
  methods: {
    async loadOrders() {
      this.loading = true
      try {
        const response = await purchaseApi.getAllOrders()
        // 管理端订单API返回Result对象 {code: 200, message: "...", data: [...]}
        // 由于API拦截器设置，这里response是完整响应对象，需要从response.data中获取Result对象，再从Result对象的data属性中获取实际订单数据
        const resultData = response && response.data
        const ordersData = (resultData && resultData.data) || []
        this.orders = Array.isArray(ordersData) ? ordersData : []
        this.totalOrders = this.orders.length
      } catch (error) {
        console.error('加载订单列表失败', error)
        alert('加载订单列表失败: ' + error.message)
        // 发生错误时确保 orders 是一个空数组
        this.orders = []
      } finally {
        this.loading = false
      }
    },
    
    resetFilters() {
      this.filterStatus = ''
      this.filterUserId = ''
      this.filterOrderNo = ''
      this.currentPage = 1
      this.loadOrders()
    },
    
    changePage(page) {
      this.currentPage = page
    },
    
    async viewOrderDetail(order) {
      try {
        // 获取完整订单详情
        const response = await purchaseApi.getOrderDetail(order.orderNo)
        // 管理端订单详情API返回Result对象 {code: 200, message: "...", data: {...}}
        // 由于API拦截器设置，这里response是Result对象，直接从response.data中获取实际订单数据
        this.selectedOrder = (response && response.data) || {}
        this.showDetailModal = true
      } catch (error) {
        console.error('获取订单详情失败', error)
        alert('获取订单详情失败: ' + error.message)
      }
    },
    
    closeDetailModal() {
      this.showDetailModal = false
      this.selectedOrder = {}
    },
    
    showStatusModal(order) {
      this.selectedOrder = order
      this.newStatus = order.status
      this.showStatusModalFlag = true
    },
    
    closeStatusModal() {
      this.showStatusModalFlag = false
      this.selectedOrder = {}
      this.newStatus = ''
    },
    
    async updateOrderStatus() {
      if (this.updatingStatus) return
      
      if (!this.newStatus) {
        alert('请选择新的订单状态')
        return
      }
      
      this.updatingStatus = true
      try {
        const response = await purchaseApi.updateOrderStatus(this.selectedOrder.orderNo, this.newStatus)
        // 管理端API返回Result对象 {code: 200, message: "...", data: {...}}
        // 由于API拦截器设置，这里response是Result对象，直接使用响应
        if (response && response.success) {
          alert(response.message || '订单状态更新成功')
        } else {
          alert((response) ? response.message : '订单状态更新成功')
        }
        
        // 更新本地数据
        const orderIndex = this.orders.findIndex(o => o.orderNo === this.selectedOrder.orderNo)
        if (orderIndex > -1) {
          this.orders[orderIndex].status = this.newStatus
          this.orders[orderIndex].updateTime = new Date().toISOString()
        }
        
        this.closeStatusModal()
      } catch (error) {
        console.error('更新订单状态失败', error)
        alert('更新订单状态失败: ' + error.message)
      } finally {
        this.updatingStatus = false
      }
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        'PENDING': '待支付',
        'PROCESSING': '处理中',
        'SUCCESS': '成功',
        'FAILED': '失败',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || status
    },
    
    formatCurrency(amount) {
      return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    
    formatDateTime(dateStr) {
      return this.formatDate(dateStr)
    }
  }
}
</script>

<style scoped>
.order-manage {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
}

.page-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.filter-section {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: end;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-weight: 500;
  color: #555;
  white-space: nowrap;
}

.filter-item input,
.filter-item select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
  min-width: 100px;
  appearance: none; /* 添加以支持现代浏览器 */
  -webkit-appearance: none; /* Safari兼容性 */
  -moz-appearance: none; /* Firefox兼容性 */
}

.btn-search,
.btn-reset {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.btn-search {
  background: #667eea;
  color: white;
}

.btn-search:hover {
  background: #5568d3;
}

.btn-reset {
  background: #6c757d;
  color: white;
}

.btn-reset:hover {
  background: #545b62;
}

.table-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow-x: auto;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 600px;
}

.order-table th,
.order-table td {
  padding: 10px 8px;
  text-align: left;
  border-bottom: 1px solid #eee;
  white-space: nowrap;
  font-size: 13px;
}

.order-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #555;
}

.loading,
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status-badge.PENDING {
  background: #fff3cd;
  color: #856404;
}

.status-badge.PROCESSING {
  background: #cce5ff;
  color: #004085;
}

.status-badge.SUCCESS {
  background: #d4edda;
  color: #155724;
}

.status-badge.FAILED {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.CANCELLED {
  background: #e2e3e5;
  color: #383d41;
}

/* 操作按钮样式优化 */
.btn-action {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 5px;
  transition: all 0.2s;
  min-width: 60px;
}

.btn-detail {
  background: #17a2b8;
  color: white;
}

.btn-detail:hover {
  background: #138496;
}

.btn-status {
  background: #ffc107;
  color: #212529;
}

.btn-status:hover {
  background: #e0a800;
}

.btn-status:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.btn-page {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.btn-page:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  white-space: nowrap;
}

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
  max-height: 90vh;
  overflow-y: auto;
}

.detail-modal {
  max-width: 600px;
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

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  appearance: none; /* 添加以支持现代浏览器 */
  -webkit-appearance: none; /* Safari兼容性 */
  -moz-appearance: none; /* Firefox兼容性 */
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
  background: #667eea;
  color: white;
}

.btn-confirm:hover {
  background: #5568d3;
}

.btn-confirm:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 订单详情模态框样式 */
.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start;
}

.detail-row .label {
  font-weight: 500;
  color: #666;
  width: 100px;
  flex-shrink: 0;
}

.detail-row .value {
  flex: 1;
  color: #333;
  word-break: break-word;
}

.detail-row .value.income {
  color: #28a745;
  font-weight: 600;
}

.detail-row .value.remark {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  border-left: 3px solid #667eea;
}

.status-badge-inline {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge-inline.PENDING {
  background: #fff3cd;
  color: #856404;
}

.status-badge-inline.PROCESSING {
  background: #cce5ff;
  color: #004085;
}

.status-badge-inline.SUCCESS {
  background: #d4edda;
  color: #155724;
}

.status-badge-inline.FAILED {
  background: #f8d7da;
  color: #721c24;
}

.status-badge-inline.CANCELLED {
  background: #e2e3e5;
  color: #383d41;
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-item {
    width: 100%;
  }
  
  .filter-item input,
  .filter-item select {
    flex: 1;
  }
  
  .order-table {
    font-size: 12px;
  }
  
  .order-table th,
  .order-table td {
    padding: 8px 10px;
  }
  
  .btn-action {
    padding: 4px 8px;
    font-size: 11px;
    min-width: 50px;
  }
  
  .detail-row .label {
    width: 80px;
    font-size: 12px;
  }
  
  .detail-row .value {
    font-size: 12px;
  }
}
</style>