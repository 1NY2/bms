<template>
  <div class="product-manage">
    <div class="page-header">
      <h2>产品属性配置</h2>
      <p class="page-desc">管理银行存款产品的基础属性</p>
    </div>

    <div class="product-actions">
      <button @click="openCreateDialog" class="btn-create">+ 新建产品</button>
    </div>

    <div class="product-table">
      <table>
        <thead>
          <tr>
            <th>产品编号</th>
            <th>产品名称</th>
            <th>年化利率</th>
            <th>期限</th>
            <th>起存金额</th>
            <th>库存</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>{{ product.productCode }}</td>
            <td>{{ product.productName }}</td>
            <td>{{ product.annualRate }}%</td>
            <td>{{ product.durationDesc }}</td>
            <td>¥{{ product.minAmount }}</td>
            <td>{{ product.inventory }}</td>
            <td>
              <span :class="['status-badge', product.status]">
                {{ getStatusText(product.status) }}
              </span>
            </td>
            <td>
              <button @click="editProduct(product)" class="btn-edit">编辑</button>
              <button @click="toggleStatus(product)" class="btn-toggle">
                {{ product.status === 'ACTIVE' ? '下架' : '上架' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 产品编辑弹窗 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <h3>{{ dialogMode === 'create' ? '新建产品' : '编辑产品' }}</h3>
        
        <div class="dialog-content">
          <div class="form-row">
            <div class="form-group">
              <label>产品编号 *</label>
              <input v-model="formData.productCode" :disabled="dialogMode === 'edit'" class="form-input" />
            </div>
            <div class="form-group">
              <label>产品名称 *</label>
              <input v-model="formData.productName" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>年化利率(%) *</label>
              <input v-model.number="formData.annualRate" type="number" step="0.01" class="form-input" />
            </div>
            <div class="form-group">
              <label>期限(天) *</label>
              <input v-model.number="formData.duration" type="number" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>期限说明</label>
              <input v-model="formData.durationDesc" placeholder="如: 180天, 3个月" class="form-input" />
            </div>
            <div class="form-group">
              <label>起存金额(元) *</label>
              <input v-model.number="formData.minAmount" type="number" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>递增金额(元) *</label>
              <input v-model.number="formData.incrementAmount" type="number" class="form-input" />
            </div>
            <div class="form-group">
              <label>库存数量 *</label>
              <input v-model.number="formData.inventory" type="number" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>单人限额(元) *</label>
              <input v-model.number="formData.personLimit" type="number" class="form-input" />
            </div>
            <div class="form-group">
              <label>单日限额(元) *</label>
              <input v-model.number="formData.dailyLimit" type="number" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>风险等级</label>
              <select v-model="formData.riskLevel" class="form-input">
                <option value="LOW">低风险</option>
                <option value="MEDIUM">中风险</option>
                <option value="HIGH">高风险</option>
              </select>
            </div>
            <div class="form-group">
              <label>结息方式</label>
              <input v-model="formData.interestMethod" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>起息日期 *</label>
              <input v-model="formData.startDate" type="date" class="form-input" />
            </div>
            <div class="form-group">
              <label>到期日期 *</label>
              <input v-model="formData.endDate" type="date" class="form-input" />
            </div>
          </div>

          <div class="form-group">
            <label>产品描述</label>
            <textarea v-model="formData.description" rows="3" class="form-textarea"></textarea>
          </div>
        </div>
        
        <div class="dialog-actions">
          <button @click="saveProduct" class="btn-primary">保存</button>
          <button @click="closeDialog" class="btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi } from '@/api'

const products = ref([])
const showDialog = ref(false)
const dialogMode = ref('create')
const formData = ref({})

const getStatusText = (status) => {
  const map = { 'ACTIVE': '在售', 'PAUSED': '暂停', 'EXPIRED': '已过期' }
  return map[status] || status
}

const fetchProducts = async () => {
  try {
    const response = await productApi.getProductList()
    if (response.data && response.data.success) {
      products.value = response.data.data
    }
  } catch (error) {
    console.error('获取产品列表失败:', error)
    alert('获取产品列表失败')
  }
}

const openCreateDialog = () => {
  dialogMode.value = 'create'
  formData.value = {
    productCode: '',
    productName: '',
    annualRate: 0,
    duration: 0,
    durationDesc: '',
    minAmount: 0,
    incrementAmount: 0,
    personLimit: 0,
    dailyLimit: 0,
    riskLevel: 'LOW',
    interestMethod: '到期一次性结息',
    startDate: '',
    endDate: '',
    inventory: 1000,
    description: ''
  }
  showDialog.value = true
}

const editProduct = (product) => {
  dialogMode.value = 'edit'
  formData.value = { ...product }
  showDialog.value = true
}

const saveProduct = async () => {
  try {
    const response = await productApi.saveProduct(formData.value)
    if (response.data && response.data.success) {
      alert('保存成功')
      closeDialog()
      await fetchProducts()
    } else {
      alert('保存失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('保存产品失败:', error)
    alert('保存失败')
  }
}

const toggleStatus = async (product) => {
  const newStatus = product.status === 'ACTIVE' ? 'PAUSED' : 'ACTIVE'
  try {
    const response = await productApi.updateProductStatus(product.productCode, newStatus)
    if (response.data && response.data.success) {
      alert('状态更新成功')
      await fetchProducts()
    } else {
      alert('状态更新失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    alert('更新状态失败')
  }
}

const closeDialog = () => {
  showDialog.value = false
  formData.value = {}
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-manage {
  padding: 0;
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

.product-actions {
  margin-bottom: 20px;
}

.btn-create {
  background: #667eea;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-create:hover {
  background: #5568d3;
}

.product-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

thead {
  background: #f5f5f5;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
  font-size: 14px;
}

th {
  font-weight: 600;
  color: #333;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.ACTIVE {
  background: #d4edda;
  color: #155724;
}

.status-badge.PAUSED {
  background: #fff3cd;
  color: #856404;
}

.status-badge.EXPIRED {
  background: #f8d7da;
  color: #721c24;
}

.btn-edit, .btn-toggle {
  padding: 6px 12px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-edit {
  background: #667eea;
  color: white;
}

.btn-edit:hover {
  background: #5568d3;
}

.btn-toggle {
  background: #e0e0e0;
  color: #666;
}

.btn-toggle:hover {
  background: #d0d0d0;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 8px;
  padding: 24px;
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.dialog-content {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-input, .form-textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-primary, .btn-secondary {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
}

.btn-secondary {
  background: #e0e0e0;
  color: #666;
}

.btn-secondary:hover {
  background: #d0d0d0;
}
</style>
