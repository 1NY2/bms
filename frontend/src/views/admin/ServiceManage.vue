<template>
  <div class="service-manage">
    <div class="page-header">
      <h2>原子服务管理</h2>
      <p class="page-desc">查看和管理系统中的所有原子服务</p>
    </div>

    <div class="service-stats">
      <div class="stat-card">
        <div class="stat-value">{{ services.length }}</div>
        <div class="stat-label">原子服务总数</div>
      </div>
    </div>

    <div class="service-list">
      <div v-for="service in services" :key="service.serviceCode" class="service-card">
        <div class="service-header">
          <h3>{{ service.serviceName }}</h3>
          <span class="service-code">{{ service.serviceCode }}</span>
        </div>
        
        <p class="service-desc">{{ service.serviceDescription }}</p>
        
        <div class="service-params">
          <div class="param-section">
            <h4>📥 输入参数</h4>
            <div class="param-list">
              <div v-for="(desc, key) in service.inputParams" :key="key" class="param-item">
                <span class="param-name">{{ key }}</span>
                <span class="param-desc">{{ desc }}</span>
              </div>
              <div v-if="Object.keys(service.inputParams).length === 0" class="no-params">无</div>
            </div>
          </div>
          
          <div class="param-section">
            <h4>📤 输出参数</h4>
            <div class="param-list">
              <div v-for="(desc, key) in service.outputParams" :key="key" class="param-item">
                <span class="param-name">{{ key }}</span>
                <span class="param-desc">{{ desc }}</span>
              </div>
              <div v-if="Object.keys(service.outputParams).length === 0" class="no-params">无</div>
            </div>
          </div>
        </div>

        <div class="service-actions">
          <button @click="testService(service)" class="btn-test">测试服务</button>
        </div>
      </div>
    </div>

    <!-- 测试服务弹窗 -->
    <div v-if="showTestDialog" class="dialog-overlay" @click="closeTestDialog">
      <div class="dialog" @click.stop>
        <h3>测试服务: {{ selectedService?.serviceName }}</h3>
        
        <div class="dialog-content">
          <div class="form-group">
            <label>输入上下文（JSON格式）:</label>
            <textarea v-model="testContext" rows="10" class="form-textarea"></textarea>
          </div>
        </div>
        
        <div v-if="testResult" class="test-result">
          <h4>执行结果:</h4>
          <pre>{{ testResult }}</pre>
        </div>
        
        <div class="dialog-actions">
          <button @click="executeTest" class="btn-primary">执行测试</button>
          <button @click="closeTestDialog" class="btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { atomicApi } from '@/api'

const services = ref([])
const showTestDialog = ref(false)
const selectedService = ref(null)
const testContext = ref('')
const testResult = ref(null)

// 获取所有服务
const fetchServices = async () => {
  try {
    const response = await atomicApi.listServices()
    if (response.data && response.data.success) {
      services.value = response.data.data
    }
  } catch (error) {
    console.error('获取服务列表失败:', error)
    alert('获取服务列表失败')
  }
}

// 测试服务
const testService = (service) => {
  selectedService.value = service
  showTestDialog.value = true
  testResult.value = null
  
  // 生成示例输入
  const example = {}
  if (service.inputParams) {
    Object.keys(service.inputParams).forEach(key => {
      example[key] = ''
    })
  }
  testContext.value = JSON.stringify(example, null, 2)
}

// 执行测试
const executeTest = async () => {
  try {
    const context = JSON.parse(testContext.value)
    const response = await atomicApi.testService(selectedService.value.serviceCode, context)
    testResult.value = JSON.stringify(response.data, null, 2)
  } catch (error) {
    console.error('执行测试失败:', error)
    testResult.value = JSON.stringify({ error: error.message }, null, 2)
  }
}

// 关闭测试弹窗
const closeTestDialog = () => {
  showTestDialog.value = false
  selectedService.value = null
  testContext.value = ''
  testResult.value = null
}

onMounted(() => {
  fetchServices()
})
</script>

<style scoped>
.service-manage {
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

.service-stats {
  margin-bottom: 24px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  display: inline-block;
  min-width: 200px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.service-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
}

.service-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
  transition: all 0.3s;
}

.service-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.service-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.service-code {
  background: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.service-desc {
  color: #666;
  margin-bottom: 16px;
  font-size: 14px;
}

.service-params {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.param-section h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.param-list {
  background: white;
  border-radius: 4px;
  padding: 8px;
  min-height: 80px;
}

.param-item {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
  border-bottom: 1px solid #f0f0f0;
}

.param-item:last-child {
  border-bottom: none;
}

.param-name {
  color: #667eea;
  font-weight: 500;
  font-size: 13px;
}

.param-desc {
  color: #666;
  font-size: 12px;
}

.no-params {
  color: #999;
  font-size: 12px;
  text-align: center;
  padding: 8px;
}

.service-actions {
  text-align: right;
}

.btn-test {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-test:hover {
  background: #5568d3;
  transform: translateY(-1px);
}

/* 弹窗样式 */
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
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.dialog-content {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: monospace;
  font-size: 13px;
  resize: vertical;
}

.test-result {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.test-result h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 14px;
}

.test-result pre {
  margin: 0;
  font-size: 12px;
  overflow-x: auto;
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