<template>
  <div class="workflow-manage">
    <div class="page-header">
      <h2>流程编排管理</h2>
      <p class="page-desc">配置和管理产品购买流程</p>
    </div>

    <div class="workflow-actions">
      <button @click="openCreateDialog" class="btn-create">+ 新建流程</button>
    </div>

    <div class="workflow-list">
      <div v-for="workflow in workflows" :key="workflow.id" class="workflow-card">
        <div class="workflow-header">
          <div>
            <h3>{{ workflow.workflowName }}</h3>
            <p class="workflow-code">{{ workflow.workflowCode }}</p>
          </div>
          <div>
            <span :class="['status-badge', workflow.status]">
              {{ workflow.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </span>
          </div>
        </div>
        
        <div class="workflow-info">
          <div class="info-item">
            <span class="info-label">关联产品:</span>
            <span class="info-value">{{ workflow.productCode }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">更新时间:</span>
            <span class="info-value">{{ formatDate(workflow.updateTime) }}</span>
          </div>
        </div>

        <div class="workflow-actions-row">
          <button @click="visualizeWorkflow(workflow)" class="btn-visualize">可视化</button>
          <button @click="viewWorkflow(workflow)" class="btn-view">查看流程</button>
          <button @click="editWorkflow(workflow)" class="btn-edit">编辑</button>
          <button v-if="workflow.status === 'DRAFT'" 
                  @click="publishWorkflow(workflow)" 
                  class="btn-publish">发布</button>
          <button v-if="workflow.status === 'PUBLISHED'" 
                  @click="unpublishWorkflow(workflow)" 
                  class="btn-unpublish">下线</button>
          <button @click="testWorkflow(workflow)" class="btn-test">测试</button>
          <button @click="deleteWorkflow(workflow)" class="btn-delete">删除</button>
        </div>
      </div>
    </div>

    <!-- 编辑流程弹窗 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog-large" @click.stop>
        <h3>{{ dialogMode === 'create' ? '新建流程' : '编辑流程' }}</h3>
        
        <div class="dialog-content">
          <div class="form-row">
            <div class="form-group">
              <label>流程编号 *</label>
              <input v-model="formData.workflowCode" :disabled="dialogMode === 'edit'" class="form-input" />
            </div>
            <div class="form-group">
              <label>流程名称 *</label>
              <input v-model="formData.workflowName" class="form-input" />
            </div>
          </div>

          <div class="form-group">
            <label>关联产品编号 *</label>
            <input v-model="formData.productCode" class="form-input" />
          </div>

          <div class="form-group">
            <label>流程定义 (JSON格式) *</label>
            <textarea v-model="formData.flowDefinition" rows="20" class="form-textarea-json"></textarea>
            <div class="helper-text">
              提示：流程定义采用JSON格式，包含nodes（节点）和edges（连线）两部分
            </div>
          </div>
        </div>
        
        <div class="dialog-actions">
          <button @click="saveWorkflow" class="btn-primary">保存</button>
          <button @click="closeDialog" class="btn-secondary">取消</button>
        </div>
      </div>
    </div>

    <!-- 查看流程弹窗 -->
    <div v-if="showViewDialog" class="dialog-overlay" @click="closeViewDialog">
      <div class="dialog-large" @click.stop>
        <h3>查看流程: {{ viewData.workflowName }}</h3>
        
        <div class="dialog-content">
          <div class="view-section">
            <h4>基本信息</h4>
            <p><strong>流程编号:</strong> {{ viewData.workflowCode }}</p>
            <p><strong>关联产品:</strong> {{ viewData.productCode }}</p>
            <p><strong>状态:</strong> {{ viewData.status === 'PUBLISHED' ? '已发布' : '草稿' }}</p>
          </div>

          <div class="view-section">
            <h4>流程定义</h4>
            <pre class="json-view">{{ formatJSON(viewData.flowDefinition) }}</pre>
          </div>
        </div>
        
        <div class="dialog-actions">
          <button @click="closeViewDialog" class="btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- 测试流程弹窗 -->
    <div v-if="showTestDialog" class="dialog-overlay" @click="closeTestDialog">
      <div class="dialog" @click.stop>
        <h3>测试流程: {{ testData.workflowName }}</h3>
        
        <div class="dialog-content">
          <div class="form-group">
            <label>测试上下文 (JSON格式):</label>
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

    <!-- 可视化流程弹窗 -->
    <div v-if="showVisualizeDialog" class="dialog-overlay" @click="closeVisualizeDialog">
      <div class="dialog-visualize" @click.stop>
        <h3>流程可视化: {{ visualizeData.workflowName }}</h3>
        
        <div class="dialog-content">
          <div ref="workflowGraphRef" class="workflow-graph-container"></div>
        </div>
        
        <div class="dialog-actions">
          <button @click="closeVisualizeDialog" class="btn-secondary">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { workflowApi } from '@/api'
// 动态导入 mermaid 以避免 SSR 问题
let mermaid = null;

// 创建一个ref来引用图表容器
const workflowGraphRef = ref(null);

// 在组件挂载时初始化 mermaid
onMounted(async () => {
  // 使用 nextTick 确保DOM完全加载
  await nextTick();
  
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    // 检查是否支持SVG
    if (typeof document.createElementNS === 'undefined') {
      console.error('当前环境不支持SVG，无法初始化Mermaid');
      return;
    }
    
    // 额外检查：尝试创建一个 SVG 元素来验证完整的 SVG 支持
    try {
      const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
      if (!testSvg || typeof testSvg.appendChild !== 'function') {
        console.error('SVG环境不完整，无法初始化Mermaid');
        return;
      }
    } catch (e) {
      console.error('SVG环境不可用，无法初始化Mermaid:', e);
      return;
    }
    
    try {
      const mermaidModule = await import('mermaid');
      mermaid = mermaidModule.default;
      
      // 配置Mermaid
      mermaid.initialize({
        startOnLoad: false,
        theme: 'default',
        securityLevel: 'loose',
        fontFamily: 'sans-serif', // 支持中文字体
        flowchart: {
          useMaxWidth: true,
          htmlLabels: true,
          curve: 'basis'
        },
        themeCSS: `
          .node rect,
          .node circle,
          .node ellipse,
          .node polygon,
          .node path {
            stroke: #9370DB;
            fill: #E0E0FF;
            stroke-width: 1px;
          }
          .node .label {
            text-align: center;
            font-family: sans-serif, "Microsoft YaHei", "SimHei", "PingFang SC", "Hiragino Sans GB", "Source Han Sans CN", "Noto Sans CJK SC";
            font-style: normal; /* 确保中文不使用斜体 */
          }
          .node.clickable {
            cursor: pointer;
          }
          .arrowheadPath {
            fill: #333;
          }
          .edgePath .path {
            stroke: #333;
            stroke-width: 1.5px;
          }
        `
      });
      
      console.log('Mermaid初始化成功');
    } catch (error) {
      console.error('Mermaid初始化失败:', error);
      // 在初始化失败时，将mermaid设为null以避免后续错误
      mermaid = null;
    }
  }
})



const workflows = ref([])
const showDialog = ref(false)
const showViewDialog = ref(false)
const showTestDialog = ref(false)
const showVisualizeDialog = ref(false)
const dialogMode = ref('create')
const formData = ref({})
const viewData = ref({})
const testData = ref({})
const testContext = ref('')
const testResult = ref(null)
const visualizeData = ref({})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const formatJSON = (json) => {
  try {
    if (typeof json === 'string') {
      return JSON.stringify(JSON.parse(json), null, 2)
    }
    return JSON.stringify(json, null, 2)
  } catch (e) {
    return json
  }
}

const fetchWorkflows = async () => {
  try {
    const response = await workflowApi.listWorkflows()
    if (response.data && response.data.success) {
      workflows.value = response.data.data
    }
  } catch (error) {
    console.error('获取流程列表失败:', error)
    alert('获取流程列表失败')
  }
}

const openCreateDialog = () => {
  dialogMode.value = 'create'
  const exampleFlow = {
    workflowCode: 'FLOW_EXAMPLE',
    workflowName: '示例流程',
    nodes: [
      { nodeId: 'start', nodeType: 'start' },
      { 
        nodeId: 'service1', 
        nodeType: 'service', 
        serviceCode: 'USER_VERIFY',
        inputMapping: { userId: '${context.userId}' },
        outputMapping: { verifyResult: 'verified' }
      },
      { nodeId: 'end', nodeType: 'end' }
    ],
    edges: [
      { from: 'start', to: 'service1' },
      { from: 'service1', to: 'end' }
    ]
  }
  
  formData.value = {
    workflowCode: '',
    workflowName: '',
    productCode: '',
    flowDefinition: JSON.stringify(exampleFlow, null, 2)
  }
  showDialog.value = true
}

const editWorkflow = (workflow) => {
  dialogMode.value = 'edit'
  formData.value = {
    id: workflow.id,
    workflowCode: workflow.workflowCode,
    workflowName: workflow.workflowName,
    productCode: workflow.productCode,
    flowDefinition: formatJSON(workflow.flowDefinition)
  }
  showDialog.value = true
}

const viewWorkflow = (workflow) => {
  viewData.value = workflow
  showViewDialog.value = true
}

const visualizeWorkflow = async (workflow) => {
  visualizeData.value = workflow
  showVisualizeDialog.value = true
  
  // 使用 nextTick 确保DOM元素渲染完成后再绘制图形
  await nextTick()
  // 再次使用 nextTick 确保对话框完全渲染
  await nextTick()
  
  // 确保在下一个事件循环中渲染图表，确保DOM已完全更新
  setTimeout(async () => {
    // 等待DOM更新
    await nextTick();
    
    // 确保 mermaid 已初始化后再渲染
    if (mermaid) {
      // 检查并等待容器在DOM中
      if (workflowGraphRef.value && document.contains(workflowGraphRef.value)) {
        // 验证SVG环境
        if (typeof window !== 'undefined' && typeof document !== 'undefined' && typeof document.createElementNS !== 'undefined') {
          try {
            // 尝试创建一个 SVG 元素来验证当前上下文的DOM功能
            const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            if (testSvg && typeof testSvg.appendChild === 'function') {
              renderWorkflowGraph(workflow);
            } else {
              if (workflowGraphRef.value) {
                workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: SVG环境不完整</p>`
              }
            }
          } catch (e) {
            console.error('DOM环境不支持SVG操作:', e);
            if (workflowGraphRef.value) {
              workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: DOM环境不支持SVG操作</p>`
            }
          }
        } else {
          if (workflowGraphRef.value) {
            workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: SVG环境不可用</p>`
          }
        }
      } else {
        // 如果容器不在DOM中，设置一个重试机制
        const waitForContainer = (maxRetries = 10, retryCount = 0) => {
          if (retryCount >= maxRetries) {
            if (workflowGraphRef.value) {
              workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: 容器始终未挂载</p>`
            }
            return;
          }
          
          setTimeout(async () => {
            await nextTick();
            if (workflowGraphRef.value && document.contains(workflowGraphRef.value)) {
              // 验证SVG环境
              if (typeof window !== 'undefined' && typeof document !== 'undefined' && typeof document.createElementNS !== 'undefined') {
                try {
                  const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                  if (testSvg && typeof testSvg.appendChild === 'function') {
                    renderWorkflowGraph(workflow);
                  } else {
                    if (workflowGraphRef.value) {
                      workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: SVG环境不完整</p>`
                    }
                  }
                } catch (e) {
                  console.error('DOM环境不支持SVG操作:', e);
                  if (workflowGraphRef.value) {
                    workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: DOM环境不支持SVG操作</p>`
                  }
                }
              } else {
                if (workflowGraphRef.value) {
                  workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: SVG环境不可用</p>`
                }
              }
            } else {
              waitForContainer(maxRetries, retryCount + 1);
            }
          }, 200);
        };
        
        waitForContainer();
      }
    } else {
      if (workflowGraphRef.value) {
        workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: Mermaid组件尚未初始化</p>`
      }
    }
  }, 100);
}

// 安全渲染Mermaid图表的辅助函数
const safeRenderMermaid = async (mermaidStr) => {
  return new Promise((resolve, reject) => {
    // 使用 setTimeout 确保在下一个事件循环中执行，给环境更多准备时间
    setTimeout(async () => {
      // 每次渲染前都重新检查环境
      if (typeof window === 'undefined' || typeof document === 'undefined') {
        reject(new Error('无法在非浏览器环境中渲染图表'));
        return;
      }
      
      // 检查document.createElementNS是否可用
      if (typeof document.createElementNS === 'undefined') {
        reject(new Error('当前环境不支持SVG')); 
        return;
      }
      
      // 额外验证SVG功能
      try {
        const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        if (!testSvg || typeof testSvg.appendChild !== 'function') {
          reject(new Error('SVG环境不完整，无法渲染图表'));
          return;
        }
      } catch (e) {
        reject(new Error('SVG环境不可用'));
        return;
      }
      
      try {
        // 确保mermaid实例仍然有效
        if (!mermaid || typeof mermaid.render !== 'function') {
          reject(new Error('Mermaid实例无效'));
          return;
        }
        
        // 在调用mermaid.render前再次检查环境
        if (typeof document.createElementNS === 'undefined') {
          reject(new Error('document.createElementNS在渲染前变为undefined'));
          return;
        }
        
        // 检查document对象是否仍然完整
        if (typeof document.createElementNS === 'undefined') {
          reject(new Error('document.createElementNS在渲染前变为undefined'));
          return;
        }
        
        // 再次验证SVG环境
        try {
          const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
          if (!testSvg || typeof testSvg.appendChild !== 'function') {
            reject(new Error('SVG环境在渲染前变为不可用'));
            return;
          }
          
          // 检查是否支持中文文本渲染
          const testText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
          testText.textContent = '测试'; // 中文测试字符
          if (typeof testText.textContent !== 'string') {
            console.warn('当前环境可能对中文文本支持有限');
          }
        } catch (e) {
          reject(new Error('SVG环境在渲染前异常'));
          return;
        }
        
        let result;
        try {
          result = await mermaid.render('mermaid-diagram', mermaidStr);
        } catch (renderError) {
          console.error('Mermaid渲染失败:', renderError);
          reject(new Error(`Mermaid渲染失败: ${renderError.message}`));
          return;
        }
        
        // 确保返回结果有效
        if (!result || !result.svg) {
          reject(new Error('渲染结果无效'));
          return;
        }
        
        resolve(result);
      } catch (error) {
        reject(error);
      }
    }, 100); // 延迟100ms以确保环境稳定
  });
};

const renderWorkflowGraph = async (workflow) => {
  try {
    // 解析流程定义
    let flowDefinition;
    if (typeof workflow.flowDefinition === 'string') {
      flowDefinition = JSON.parse(workflow.flowDefinition)
    } else {
      flowDefinition = workflow.flowDefinition
    }

    if (!flowDefinition.nodes || !flowDefinition.edges) {
      if (workflowGraphRef.value) {
        workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">流程定义格式不正确，缺少nodes或edges</p>`
      }
      return
    }

    // 生成Mermaid流程图定义
    let mermaidStr = "flowchart TD\n    %% 设置中文字体支持\n\n"
    
    // 中文节点类型映射
    const nodeTypeMap = {
      'start': '开始',
      'end': '结束',
      'service': '服务',
      'condition': '条件',
      'task': '任务',
      'process': '处理',
      'decision': '决策',
      'input': '输入',
      'output': '输出'
    };
    
    // 服务代码中文映射
    const serviceCodeMap = {
      'USER_VERIFY': '用户验证',
      'AMOUNT_VALIDATE': '金额验证',
      'BALANCE_CHECK': '余额检查',
      'ID_CHECK': '身份验证',
      'INTEREST_CALC': '利息计算',
      'INVENTORY_LOCK': '库存锁定',
      'INVENTORY_UPDATE': '库存更新',
      'INVENTORY_RELEASE': '库存释放',
      'LIMIT_CHECK': '限额检查',
      'LOG_RECORD': '日志记录',
      'ORDER_CREATE': '订单创建',
      'REGION_CHECK': '地区检查',
      'REJECT': '拒绝',
      'TAG_CHECK': '标签检查',
      'WHITELIST_CHECK': '白名单检查',
      'DEDUCT_BALANCE': '扣款',
      'DEDUCT_BALANCE_SERVICE': '扣款服务',
      'BALANCE_CHECK_SERVICE': '余额检查服务',
      'ORDER_CREATE_SERVICE': '订单创建服务',
      'INVENTORY_LOCK_SERVICE': '库存锁定服务',
      'INVENTORY_UPDATE_SERVICE': '库存更新服务',
      'INTEREST_CALC_SERVICE': '利息计算服务',
      'USER_VERIFY_SERVICE': '用户验证服务',
      'AMOUNT_VALIDATE_SERVICE': '金额验证服务',
      'ID_CHECK_SERVICE': '身份验证服务',
      'LIMIT_CHECK_SERVICE': '限额检查服务',
      'REGION_CHECK_SERVICE': '地区检查服务',
      'TAG_CHECK_SERVICE': '标签检查服务',
      'WHITELIST_CHECK_SERVICE': '白名单检查服务',
      'LOG_RECORD_SERVICE': '日志记录服务',
      'REJECT_SERVICE': '拒绝服务'
    };
    
    // 定义节点
    const nodeMap = {}
    flowDefinition.nodes.forEach(node => {
      nodeMap[node.nodeId] = node
      
      // 优先使用用户定义的标签，否则使用中文类型说明（不显示英文ID）
      let displayLabel = node.label || nodeTypeMap[node.nodeType] || node.nodeId;
      
      // 如果节点类型有中文映射，则只使用中文标签，不显示英文
      if (nodeTypeMap[node.nodeType]) {
        // 优先使用用户定义的标签，否则只使用中文类型说明
        displayLabel = node.label || nodeTypeMap[node.nodeType];
      }
      
      if (node.serviceCode) {
        // 只使用服务代码的中文映射，如果没有映射则使用原服务代码
        const serviceCodeChinese = serviceCodeMap[node.serviceCode] || node.serviceCode;
        // 清理特殊字符，保留中文字符，避免Mermaid语法错误
        const cleanServiceCode = serviceCodeChinese.replace(/[\[\]{}<>"'`]/g, '_');
        // 如果当前displayLabel为空或与节点ID相同，则直接使用中文服务代码
        if (!displayLabel || displayLabel === node.nodeId) {
          displayLabel = cleanServiceCode;
        } else {
          // 否则将中文服务代码追加到现有标签
          displayLabel = `${displayLabel} ${cleanServiceCode}`;
        }
      }
      
      // 根据节点类型定义样式
      let nodeShape = ""
      if (node.nodeType === "start") {
        // 清理特殊字符，保留中文字符
        const cleanLabel = displayLabel.replace(/[\[\]{}<>"'`]/g, '_');
        nodeShape = `("${cleanLabel}")`
      } else if (node.nodeType === "end") {
        // 清理特殊字符，保留中文字符
        const cleanLabel = displayLabel.replace(/[\[\]{}<>"'`]/g, '_');
        nodeShape = `("${cleanLabel}")`
      } else if (node.nodeType === "condition") {
        // 清理特殊字符，保留中文字符
        const cleanLabel = displayLabel.replace(/[\[\]{}<>"'`]/g, '_');
        nodeShape = `{"${cleanLabel}"}`
      } else {
        // 清理特殊字符，保留中文字符
        const cleanLabel = displayLabel.replace(/[\[\]{}<>"'`]/g, '_');
        nodeShape = `["${cleanLabel}"]`
      }
      
      // 避免与Mermaid保留关键字冲突
      const reservedKeywords = ['end', 'subgraph', 'graph', 'flowchart', 'sequenceDiagram', 'classDiagram', 'stateDiagram', 'gantt', 'pie'];
      const nodeId = reservedKeywords.includes(node.nodeId.toLowerCase()) ? `_${node.nodeId}` : node.nodeId;
      mermaidStr += `    ${nodeId}${nodeShape};\n`
    })

    mermaidStr += '\n'; // 在节点和连接之间添加空行

    // 定义连接
    // 条件标签中文映射
    const conditionMap = {
      'true': '是',
      'false': '否',
      'success': '成功',
      'failed': '失败',
      'pass': '通过',
      'reject': '拒绝',
      'valid': '有效',
      'invalid': '无效',
      'approved': '已批准',
      'rejected': '已拒绝',
      'sufficient': '充足',
      'insufficient': '不足',
      'exists': '存在',
      'not_exists': '不存在',
      'allowed': '允许',
      'not_allowed': '不允许',
      'match': '匹配',
      'not_match': '不匹配',
      'verified': '已验证',
      'not_verified': '未验证',
      'qualified': '合格',
      'not_qualified': '不合格',
      'available': '可用',
      'not_available': '不可用',
      'enabled': '启用',
      'disabled': '禁用',
      'active': '激活',
      'inactive': '未激活'
    };
    
    flowDefinition.edges.forEach(edge => {
      let edgeLabel = '';
      if (edge.condition) {
        // 首先尝试使用条件的中文映射，如果没有映射则使用原条件值
        const conditionChinese = conditionMap[edge.condition.toLowerCase()] || edge.condition;
        // 清理条件中的特殊字符，保留中文字符，使用正确的Mermaid语法
        const cleanCondition = conditionChinese.replace(/[\[\]{}<>"'`]/g, '_');
        edgeLabel = `|${cleanCondition}|`;
      }
      // 验证节点引用是否有效，避免引用不存在的节点
      if (nodeMap[edge.from] && nodeMap[edge.to]) {
        // 避免与Mermaid保留关键字冲突
        const reservedKeywords = ['end', 'subgraph', 'graph', 'flowchart', 'sequenceDiagram', 'classDiagram', 'stateDiagram', 'gantt', 'pie'];
        const fromId = reservedKeywords.includes(edge.from.toLowerCase()) ? `_${edge.from}` : edge.from;
        const toId = reservedKeywords.includes(edge.to.toLowerCase()) ? `_${edge.to}` : edge.to;
        mermaidStr += `    ${fromId} -->${edgeLabel} ${toId};\n`
      } else {
        console.warn(`连接引用了不存在的节点: ${edge.from} --> ${edge.to}`);
      }
    })

    // 使用 ref 获取容器
    const container = workflowGraphRef.value;
    if (!container) {
      console.error('找不到工作流图容器');
      return;
    }
    
    // 确保容器在DOM中且 mermaid 已初始化
    if (mermaid) {
      // 确保容器已添加到DOM中
      if (!document.contains(container)) {
        console.log('容器尚未添加到DOM，等待200ms');
        await new Promise(resolve => setTimeout(resolve, 200));
        
        // 再次检查容器是否在DOM中
        if (!document.contains(container)) {
          console.error('容器仍然不在DOM中');
          container.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: 容器未正确挂载</p>`;
          return;
        }
      }
      
      // 清空容器
      container.innerHTML = "";
      
      // 在渲染前再次验证SVG环境
      if (typeof window === 'undefined' || typeof document === 'undefined' || typeof document.createElementNS === 'undefined') {
        console.error('SVG环境不可用');
        container.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: SVG环境不可用</p>`;
        return;
      }
      
      try {
        // 验证完整的SVG环境
        const testSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        if (!testSvg || typeof testSvg.appendChild !== 'function') {
          throw new Error('SVG环境不完整');
        }
        
        console.log('准备渲染的Mermaid字符串:', mermaidStr); // 调试信息
        
        // 使用安全的渲染函数
        const result = await safeRenderMermaid(mermaidStr);
        
        // 确保结果存在且有效
        if (result && result.svg) {
          // 将SVG内容插入到容器中
          container.innerHTML = result.svg;
          
          // 绑定可能的事件
          if (result.bindFunctions) {
            result.bindFunctions(container);
          }
        } else {
          throw new Error('渲染结果无效');
        }
      } catch (error) {
        console.error('渲染Mermaid图表时出错:', error);
        console.error('错误堆栈:', error.stack);
        
        // 提供降级的文本显示
        try {
          container.innerHTML = `
            <div style="padding: 20px; text-align: center; color: #666;">
              <p style="color: red; margin-bottom: 10px;">图表渲染失败: ${error.message}</p>
              <p>流程定义:</p>
              <pre style="text-align: left; background: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto;">
                ${mermaidStr.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')}
              </pre>
            </div>
          `;
        } catch (innerError) {
          console.error('设置降级内容时出错:', innerError);
          container.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: ${error.message}</p>`;
        }
      }
    } else {
      console.error('Mermaid未初始化，请等待组件加载完成');
      container.innerHTML = `<p style="color: red; padding: 20px;">图表渲染失败: Mermaid未初始化</p>`
    }
  } catch (error) {
    console.error("渲染流程图失败:", error)
    console.error('错误详情:', error.stack);
    if (workflowGraphRef.value) {
      workflowGraphRef.value.innerHTML = `<p style="color: red; padding: 20px;">流程图渲染失败: ${error.message}</p>`
    }
  }
}

const saveWorkflow = async () => {
  try {
    // 验证JSON格式
    try {
      JSON.parse(formData.value.flowDefinition)
    } catch (e) {
      alert('流程定义JSON格式不正确')
      return
    }

    const response = await workflowApi.saveWorkflow(formData.value)
    if (response.data && response.data.success) {
      alert('保存成功')
      closeDialog()
      await fetchWorkflows()
    } else {
      alert('保存失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('保存流程失败:', error)
    alert('保存失败')
  }
}

const publishWorkflow = async (workflow) => {
  if (!confirm(`确定要发布流程"${workflow.workflowName}"吗？`)) return
  
  try {
    const response = await workflowApi.publishWorkflow(workflow.id)
    if (response.data && response.data.success) {
      alert('发布成功')
      await fetchWorkflows()
    } else {
      alert('发布失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('发布流程失败:', error)
    alert('发布失败')
  }
}

const unpublishWorkflow = async (workflow) => {
  if (!confirm(`确定要下线流程"${workflow.workflowName}"吗？`)) return
  
  try {
    const response = await workflowApi.unpublishWorkflow(workflow.id)
    if (response.data && response.data.success) {
      alert('下线成功')
      await fetchWorkflows()
    } else {
      alert('下线失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('下线流程失败:', error)
    alert('下线失败')
  }
}

const deleteWorkflow = async (workflow) => {
  if (!confirm(`确定要删除流程"${workflow.workflowName}"吗？此操作不可恢复！`)) return
  
  try {
    const response = await workflowApi.deleteWorkflow(workflow.id)
    if (response.data && response.data.success) {
      alert('删除成功')
      await fetchWorkflows()
    } else {
      alert('删除失败: ' + (response.data ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('删除流程失败:', error)
    alert('删除失败')
  }
}

const testWorkflow = (workflow) => {
  testData.value = workflow
  testContext.value = JSON.stringify({
    userId: 'U001',
    productCode: workflow.productCode,
    amount: 10000,
    quantity: 1
  }, null, 2)
  testResult.value = null
  showTestDialog.value = true
}

const executeTest = async () => {
  try {
    const context = JSON.parse(testContext.value)
    const response = await workflowApi.testWorkflow(testData.value.workflowCode, context)
    testResult.value = JSON.stringify(response.data || response, null, 2)
  } catch (error) {
    console.error('测试流程失败:', error)
    testResult.value = JSON.stringify({ error: error.message }, null, 2)
  }
}

const closeDialog = () => {
  showDialog.value = false
  formData.value = {}
}

const closeViewDialog = () => {
  showViewDialog.value = false
  viewData.value = {}
}

const closeTestDialog = () => {
  showTestDialog.value = false
  testData.value = {}
  testContext.value = ''
  testResult.value = null
}

const closeVisualizeDialog = () => {
  showVisualizeDialog.value = false
  visualizeData.value = {}
}

onMounted(() => {
  fetchWorkflows()
})

onUnmounted(() => {
  // 清理 mermaid 引用
  mermaid = null;
})
</script>

<style scoped>
.workflow-manage {
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

.workflow-actions {
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

.workflow-list {
  display: grid;
  gap: 20px;
}

.workflow-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
  transition: all 0.3s;
}

.workflow-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.workflow-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.workflow-header h3 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 18px;
}

.workflow-code {
  margin: 0;
  color: #999;
  font-size: 13px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.PUBLISHED {
  background: #d4edda;
  color: #155724;
}

.status-badge.DRAFT {
  background: #fff3cd;
  color: #856404;
}

.workflow-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  font-size: 14px;
}

.info-label {
  color: #666;
  margin-right: 8px;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.workflow-actions-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-visualize, .btn-view, .btn-edit, .btn-publish, .btn-unpublish, .btn-test, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-visualize {
  background: #6f42c1;
  color: white;
}
.btn-visualize:hover {
  background: #5a32a3;
}

.btn-view {
  background: #667eea;
  color: white;
}

.btn-view:hover {
  background: #5568d3;
}

.btn-edit {
  background: #ffc107;
  color: white;
}

.btn-edit:hover {
  background: #e0a800;
}

.btn-publish {
  background: #28a745;
  color: white;
}

.btn-publish:hover {
  background: #218838;
}

.btn-unpublish {
  background: #6c757d;
  color: white;
}

.btn-unpublish:hover {
  background: #5a6268;
}

.btn-test {
  background: #17a2b8;
  color: white;
}

.btn-test:hover {
  background: #138496;
}

.btn-delete {
  background: #dc3545;
  color: white;
}

.btn-delete:hover {
  background: #c82333;
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

.dialog, .dialog-large, .dialog-visualize {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog {
  max-width: 600px;
}

.dialog-large {
  max-width: 900px;
}

.dialog-visualize {
  max-width: 1200px;
  height: 80vh;
  display: flex;
  flex-direction: column;
}

.dialog h3, .dialog-large h3, .dialog-visualize h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.dialog-content {
  margin-bottom: 20px;
  flex: 1;
  min-height: 0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
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

.form-input, .form-textarea, .form-textarea-json {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-textarea-json {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  resize: vertical;
}

.form-input:focus, .form-textarea:focus, .form-textarea-json:focus {
  outline: none;
  border-color: #667eea;
}

.helper-text {
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.view-section {
  margin-bottom: 24px;
}

.view-section h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
}

.view-section p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.json-view {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  overflow-x: auto;
  margin: 0;
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

.workflow-graph-container {
  width: 100%;
  height: 100%;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: auto;
  background: white;
  padding: 20px;
}
</style>