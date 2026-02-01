<template>
  <div class="chart-container">
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script>
// 修复Chart.js导入问题
import {
  Chart,
  ArcElement,
  Tooltip,
  Legend,
  PieController
} from 'chart.js'

// 注册必要的组件
Chart.register(ArcElement, Tooltip, Legend, PieController)

export default {
  name: 'AssetPieChart',
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chartInstance: null,
      resizeObserver: null
    }
  },
  mounted() {
    // 确保DOM已经渲染后再初始化图表
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeUnmount() {
    // 清理资源
    this.cleanupChart()
  },
  watch: {
    data: {
      handler() {
        // 使用防抖机制避免频繁更新
        this.debounceUpdateChart()
      },
      deep: true
    }
  },
  methods: {
    initChart() {
      // 初始化ResizeObserver监听容器大小变化
      if (window.ResizeObserver) {
        this.resizeObserver = new ResizeObserver(() => {
          if (this.chartInstance) {
            this.chartInstance.resize()
          }
        })
        this.resizeObserver.observe(this.$el)
      }
      
      this.renderChart()
    },
    
    cleanupChart() {
      // 销毁图表实例以防止内存泄漏
      if (this.chartInstance) {
        this.chartInstance.destroy()
        this.chartInstance = null
      }
      
      // 断开ResizeObserver
      if (this.resizeObserver) {
        this.resizeObserver.disconnect()
        this.resizeObserver = null
      }
    },
    
    debounceUpdateChart() {
      // 清除之前的定时器
      if (this.updateTimer) {
        clearTimeout(this.updateTimer)
      }
      
      // 设置新的定时器
      this.updateTimer = setTimeout(() => {
        this.renderChart()
        this.updateTimer = null
      }, 100)
    },
    
    renderChart() {
      // 确保canvas元素存在
      if (!this.$refs.chartCanvas) {
        console.warn('Canvas元素未找到')
        return
      }

      // 确保canvas元素已经挂载到DOM中
      if (!this.$refs.chartCanvas.ownerDocument) {
        console.warn('Canvas元素尚未挂载到DOM中')
        return
      }

      // 销毁现有的图表实例
      if (this.chartInstance) {
        this.chartInstance.destroy()
      }

      const ctx = this.$refs.chartCanvas.getContext('2d')
      
      // 准备图表数据
      const labels = ['定期存款', '活期存款', '其他理财']
      const values = [
        this.data.fixed || 0,
        this.data.current || 0,
        this.data.other || 0
      ]
      
      // 只显示非零值
      const nonZeroLabels = labels.filter((_, index) => values[index] > 0)
      const nonZeroValues = values.filter(value => value > 0)
      
      // 如果所有值都是0，显示默认数据
      if (nonZeroValues.length === 0) {
        nonZeroLabels.push('暂无资产')
        nonZeroValues.push(1)
      }
      
      const backgroundColors = [
        '#667eea',
        '#764ba2',
        '#f093fb',
        '#f5576c'
      ]

      // 创建新的图表实例
      try {
        this.chartInstance = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: nonZeroLabels,
            datasets: [{
              data: nonZeroValues,
              backgroundColor: backgroundColors.slice(0, nonZeroValues.length),
              borderWidth: 2,
              borderColor: '#fff'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom',
                labels: {
                  padding: 20,
                  usePointStyle: true,
                  font: {
                    size: 12
                  }
                }
              },
              tooltip: {
                callbacks: {
                  label: function(context) {
                    const label = context.label || ''
                    const value = context.raw || 0
                    const total = context.dataset.data.reduce((a, b) => a + b, 0)
                    const percentage = Math.round((value / total) * 100)
                    return `${label}: ¥${value.toLocaleString()} (${percentage}%)`
                  }
                }
              }
            }
          }
        })
      } catch (error) {
        console.error('创建图表时发生错误:', error)
      }
    }
  }
}
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 200px;
  width: 100%;
}
</style>