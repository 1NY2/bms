<template>
  <div class="user-manage">
    <h2>用户管理</h2>
    
    <div class="user-table-container">
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录" width="180">
          <template #default="scope">
            {{ scope.row.lastLoginAt ? formatDate(scope.row.lastLoginAt) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'"
              @click="toggleUserStatus(scope.row)">
              {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="small" 
              type="info"
              @click="showResetPasswordDialog(scope.row.username)">
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordDialogVisible" title="重置密码" width="400px">
      <el-form>
        <el-form-item label="用户名">
          <el-input v-model="currentUsername" disabled></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="newPassword" type="password" placeholder="请输入新密码，留空则使用默认密码123456"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="resetPassword">确认重置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminUserApi } from '@/api'

const users = ref([])
const loading = ref(false)

// 重置密码对话框相关
const resetPasswordDialogVisible = ref(false)
const currentUsername = ref('')
const newPassword = ref('')

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await adminUserApi.getAllUsers()
    if (response.data && response.data.success) {
      users.value = response.data.data
    } else {
      ElMessage.error((response.data ? response.data.message : '') || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 切换用户状态
const toggleUserStatus = (user) => {
  const newStatus = user.status === 'ACTIVE' ? 'LOCKED' : 'ACTIVE'
  const actionText = user.status === 'ACTIVE' ? '禁用' : '启用'
  
  ElMessageBox.confirm(
    `确定要${actionText}用户 ${user.username} 吗？`,
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await adminUserApi.updateUserStatus(user.username, newStatus)
      
      if (response.data && response.data.success) {
        ElMessage.success((response.data ? response.data.message : '') || `${actionText}用户成功`)
        // 更新本地数据
        user.status = newStatus
      } else {
        ElMessage.error((response.data ? response.data.message : '') || `${actionText}用户失败`)
      }
    } catch (error) {
      ElMessage.error(`${actionText}用户失败: ` + error.message)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 显示重置密码对话框
const showResetPasswordDialog = (username) => {
  currentUsername.value = username
  newPassword.value = ''
  resetPasswordDialogVisible.value = true
}

// 重置密码
const resetPassword = async () => {
  try {
    const response = await adminUserApi.resetUserPassword(
      currentUsername.value,
      newPassword.value || undefined // 如果为空则不传递此参数
    )
    
    if (response.data && response.data.success) {
      ElMessage.success((response.data ? response.data.message : '') || '重置密码成功')
      resetPasswordDialogVisible.value = false
    } else {
      ElMessage.error((response.data ? response.data.message : '') || '重置密码失败')
    }
  } catch (error) {
    ElMessage.error('重置密码失败: ' + error.message)
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'LOCKED':
      return 'warning'
    case 'DELETED':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'ACTIVE':
      return '正常'
    case 'LOCKED':
      return '已锁定'
    case 'DELETED':
      return '已删除'
    default:
      return status
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-')
  } catch (e) {
    return dateString
  }
}

// 组件挂载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.user-manage h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
}

.user-table-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
</style>