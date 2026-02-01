<template>
  <div class="log-manage">
    <h2>日志管理</h2>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索日志内容" 
            clearable
            @keyup.enter="fetchLogs"
          />
        </el-col>
        <el-col :span="4">
          <el-select 
            v-model="searchForm.logType" 
            placeholder="日志类型" 
            clearable
            @change="fetchLogs"
          >
            <el-option label="购买操作" value="PURCHASE" />
            <el-option label="用户认证" value="USER_AUTH" />
            <el-option label="订单访问" value="ORDER_ACCESS" />
            <el-option label="订单管理" value="ORDER_MANAGE" />
            <el-option label="用户信息" value="USER_INFO" />
            <el-option label="用户资产" value="USER_ASSETS" />
            <el-option label="工作流" value="WORKFLOW" />
            <el-option label="原子服务" value="ATOMIC_SERVICE" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select 
            v-model="searchForm.action" 
            placeholder="操作类型" 
            clearable
            @change="fetchLogs"
          >
            <el-option label="购买产品" value="PURCHASE_PRODUCT" />
            <el-option label="用户登录" value="USER_LOGIN" />
            <el-option label="用户注册" value="USER_REGISTER" />
            <el-option label="获取订单" value="GET_USER_ORDERS" />
            <el-option label="更新订单" value="UPDATE_ORDER_STATUS" />
            <el-option label="取消订单" value="CANCEL_ORDER" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select 
            v-model="searchForm.status" 
            placeholder="状态" 
            clearable
            @change="fetchLogs"
          >
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILED" />
            <el-option label="处理中" value="PROCESSING" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchLogs">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 时间范围选择 -->
    <div class="time-range-section">
      <el-date-picker
        v-model="searchForm.timeRange"
        type="datetimerange"
        range-separator="至"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        value-format="YYYY-MM-DD HH:mm:ss"
        @change="fetchLogs"
      />
    </div>

    <!-- 日志列表 -->
    <el-table 
      :data="logs" 
      style="width: 100%" 
      v-loading="loading"
      border
      stripe
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="logType" label="日志类型" width="120">
        <template #default="{ row }">
          <el-tag 
            :type="getLogTypeTagType(row.logType)" 
            size="small"
          >
            {{ getLogTypeName(row.logType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作类型" width="150" />
      <el-table-column prop="userId" label="用户ID" width="150" />
      <el-table-column prop="productCode" label="产品编码" width="120" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="content" label="日志内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag 
            :type="getStatusTagType(row.status)" 
            size="small"
          >
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small" 
            @click="viewLogDetails(row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; text-align: right;"
    />

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="showLogDetailsDialog"
      title="日志详情"
      width="80%"
      :before-close="closeLogDetailsDialog"
    >
      <div v-if="currentLog" class="log-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
          <el-descriptions-item label="日志类型">
            <el-tag :type="getLogTypeTagType(currentLog.logType)" size="small">
              {{ getLogTypeName(currentLog.logType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ currentLog.action }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentLog.userId }}</el-descriptions-item>
          <el-descriptions-item label="产品编码">{{ currentLog.productCode }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentLog.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentLog.status)" size="small">
              {{ getStatusName(currentLog.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentLog.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="IP地址" :span="2">{{ currentLog.ipAddress || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="日志内容" :span="2">{{ currentLog.content }}</el-descriptions-item>
          <el-descriptions-item label="请求参数" :span="2" v-if="currentLog.requestParams">
            <pre style="background: #f5f5f5; padding: 10px; border-radius: 4px; white-space: pre-wrap;">{{ currentLog.requestParams }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="响应结果" :span="2" v-if="currentLog.responseResult">
            <pre style="background: #f5f5f5; padding: 10px; border-radius: 4px; white-space: pre-wrap;">{{ currentLog.responseResult }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { logApi } from '@/api'
import { ElMessage } from 'element-plus'

// 响应式数据
const logs = ref([])
const loading = ref(false)
const showLogDetailsDialog = ref(false)
const currentLog = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  logType: '',
  action: '',
  status: '',
  timeRange: null
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 获取日志列表
const fetchLogs = async () => {
  loading.value = true
  try {
    let params = {}
    
    // 根据筛选条件构建请求参数
    if (searchForm.keyword) {
      const response = await logApi.searchLogs(searchForm.keyword)
      // 管理端API返回Result对象 {code: 200, message: "...", data: [...]}，需要从data中提取
      const resultData = response && response.data
      logs.value = (resultData && Array.isArray(resultData.data)) ? resultData.data : []
      pagination.total = logs.value.length
    } else {
      // 如果没有关键词搜索，获取所有日志
      const response = await logApi.getAllLogs()
      // 管理端API返回Result对象 {code: 200, message: "...", data: [...]}，需要从data中提取
      const resultData = response && response.data
      let allLogs = (resultData && Array.isArray(resultData.data)) ? resultData.data : []
      
      // 进一步筛选
      let filteredLogs = Array.isArray(allLogs) ? allLogs : []
      
      if (searchForm.logType) {
        filteredLogs = filteredLogs.filter(log => log.logType === searchForm.logType)
      }
      if (searchForm.action) {
        filteredLogs = filteredLogs.filter(log => log.action === searchForm.action)
      }
      if (searchForm.status) {
        filteredLogs = filteredLogs.filter(log => log.status === searchForm.status)
      }
      if (searchForm.timeRange && searchForm.timeRange.length === 2) {
        const [start, end] = searchForm.timeRange
        filteredLogs = filteredLogs.filter(log => {
          const logTime = new Date(log.createTime).getTime()
          return logTime >= new Date(start).getTime() && logTime <= new Date(end).getTime()
        })
      }
      
      // 设置总数量
      pagination.total = filteredLogs.length
      
      // 应用分页
      const start = (pagination.currentPage - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      logs.value = Array.isArray(filteredLogs) ? filteredLogs.slice(start, end) : []
    }
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.logType = ''
  searchForm.action = ''
  searchForm.status = ''
  searchForm.timeRange = null
  pagination.currentPage = 1
  fetchLogs()
}

// 分页相关方法
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchLogs()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchLogs()
}

// 查看日志详情
const viewLogDetails = (log) => {
  currentLog.value = log
  showLogDetailsDialog.value = true
}

// 关闭日志详情对话框
const closeLogDetailsDialog = () => {
  showLogDetailsDialog.value = false
  currentLog.value = null
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取日志类型标签类型
const getLogTypeTagType = (logType) => {
  const typeMap = {
    'PURCHASE': 'primary',
    'USER_AUTH': 'success',
    'ORDER_ACCESS': 'warning',
    'ORDER_MANAGE': 'danger',
    'USER_INFO': 'info',
    'USER_ASSETS': 'warning',
    'WORKFLOW': 'primary',
    'ATOMIC_SERVICE': 'info'
  }
  return typeMap[logType] || 'info'
}

// 获取日志类型名称
const getLogTypeName = (logType) => {
  const nameMap = {
    'PURCHASE': '购买操作',
    'USER_AUTH': '用户认证',
    'ORDER_ACCESS': '订单访问',
    'ORDER_MANAGE': '订单管理',
    'USER_INFO': '用户信息',
    'USER_ASSETS': '用户资产',
    'WORKFLOW': '工作流',
    'ATOMIC_SERVICE': '原子服务'
  }
  return nameMap[logType] || logType
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'SUCCESS': 'success',
    'FAILED': 'danger',
    'PROCESSING': 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态名称
const getStatusName = (status) => {
  const nameMap = {
    'SUCCESS': '成功',
    'FAILED': '失败',
    'PROCESSING': '处理中'
  }
  return nameMap[status] || status
}

// 组件挂载时获取日志列表
onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.log-manage {
  padding: 20px;
}

.search-section {
  margin-bottom: 20px;
}

.time-range-section {
  margin-bottom: 20px;
}

.log-details pre {
  margin: 0;
  font-family: monospace;
  font-size: 12px;
  line-height: 1.4;
}
</style>