<template>
    <div class="learning-analysis">
      <el-card class="analysis-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <h3>AI助手学情分析报告</h3>
            <el-button type="primary" @click="refreshAnalysis" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新分析
            </el-button>
          </div>
        </template>
  
        <div v-if="!loading && analysisData">
          <!-- 总体统计概览 -->
          <div class="stats-overview">
            <div class="stat-item">
              <div class="stat-number">{{ analysisData.chartData.totalSubmissions }}</div>
              <div class="stat-label">总提交次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ (analysisData.chartData.successRate * 100).toFixed(1) }}%</div>
              <div class="stat-label">总体成功率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ Object.keys(analysisData.chartData.knowledgeStats).length }}</div>
              <div class="stat-label">涉及知识点</div>
            </div>
          </div>
  
          <!-- 两栏布局 -->
          <div class="content-columns">
            <!-- 左栏：错误类型分布和知识点掌握情况 -->
            <div class="left-column">
              <!-- 错误类型分析图表 -->
              <div class="chart-section">
                <h4>错误类型分布</h4>
                <div class="error-chart" ref="errorChart"></div>
              </div>

              <!-- 知识点掌握情况 -->
              <div class="knowledge-section">
                <h4>知识点掌握情况</h4>
                <div class="knowledge-grid">
                  <div 
                    v-for="(stats, knowledge) in analysisData.chartData.knowledgeStats" 
                    :key="knowledge"
                    class="knowledge-item"
                  >
                    <div class="knowledge-header">
                      <span class="knowledge-name">{{ knowledge }}</span>
                      <span class="success-rate" :class="getSuccessRateClass(stats.successRate)">
                        {{ (stats.successRate * 100).toFixed(1) }}%
                      </span>
                    </div>
                    <div class="knowledge-details">
                      <div class="detail-item">
                        <span>尝试次数: {{ stats.totalAttempts }}</span>
                      </div>
                      <div class="detail-item">
                        <span>平均尝试: {{ stats.avgAttempts.toFixed(1) }}次</span>
                      </div>
                      <div class="problematic-titles">
                        <span class="label">问题题目:</span>
                        <div class="titles">
                          <el-tag 
                            v-for="title in stats.problematicTitles" 
                            :key="title"
                            size="small"
                            type="warning"
                          >
                            {{ title.trim() }}
                          </el-tag>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右栏：AI分析报告 -->
            <div class="right-column">
              <div class="analysis-text-section">
                <h4>详细分析报告</h4>
                <div class="analysis-text" v-html="formatAnalysisText(analysisData.analysisText)"></div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="!loading && error" class="error-state">
          <el-alert
            title="获取分析数据失败"
            :description="error"
            type="error"
            show-icon
          />
          <el-button type="primary" @click="refreshAnalysis" class="retry-btn">
            重试
          </el-button>
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, nextTick } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import { Refresh } from '@element-plus/icons-vue';
  import * as echarts from 'echarts';
  
  const loading = ref(false);
  const error = ref('');
  const analysisData = ref(null);
  const errorChart = ref(null);

  onMounted(async () => {
    await fetchAnalysisData();
    await nextTick(); // 确保 DOM 已更新
    initErrorChart();
  });
  
  const fetchAnalysisData = async () => {
    loading.value = true;
    error.value = '';
    
    try {
      const response = await axios.get('http://localhost:8048/api/learning-analysis', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.status === 200) {
        analysisData.value = response.data;
        // console.log('数据加载完成:', analysisData.value); // 调试日志
        // console.log('完整数据:', JSON.stringify(analysisData.value, null, 2));
        await nextTick();
        if (!loading.value && analysisData.value) {
          initErrorChart(); // 仅在条件满足时初始化图表
        }
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || '获取学情分析数据失败';
      ElMessage.error('获取学情分析数据失败');
    } finally {
      loading.value = false;
    }
  };
  
  const refreshAnalysis = () => {
    fetchAnalysisData();
  };
  
  const getSuccessRateClass = (rate: number) => {
    if (rate >= 0.8) return 'excellent';
    if (rate >= 0.6) return 'good';
    if (rate >= 0.4) return 'fair';
    return 'poor';
  };
  
  const formatAnalysisText = (text: string) => {
    return text
      .replace(/#{4}\s*(.*?)\n/g, '<h4>$1</h4>')
      .replace(/#{1,3}\s*(.*?)\n/g, '<h3>$1</h3>')
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/---/g, '<hr>')
      .replace(/\n/g, '<br>');
  };
  
  const initErrorChart = async () => {
    await nextTick();
    if (!errorChart.value) {
      console.error('错误：图表容器未找到');
      return;
    }
    if (!analysisData.value?.chartData?.errorAnalysis) {
      console.error('错误：errorAnalysis 数据缺失', analysisData.value?.chartData);
      return;
    }
    if (Object.keys(analysisData.value.chartData.errorAnalysis).length === 0) {
      console.warn('警告：errorAnalysis 数据为空，无法生成图表');
      return;
    }
    console.log('errorChart DOM:', errorChart.value);
    if (!errorChart.value) {
      console.warn('图表容器未找到，尝试重试');
      // setTimeout(initErrorChart, 100); // 100ms 后重试
      return;
    }
    if (!analysisData.value?.chartData?.errorAnalysis) {
      console.error('错误：errorAnalysis 数据缺失', analysisData.value?.chartData);
      return;
    }
    const chartInstance = echarts.init(errorChart.value);
    const errorAnalysis = analysisData.value.chartData.errorAnalysis;
    const errorTypeMap = {
      '2': '编译错误',
      '3': '测试用例失败',
      '4': '运行时错误',
      '5': '超时错误'
    };
    const data = Object.entries(errorAnalysis).map(([type, info]) => ({
      name: errorTypeMap[type] || `错误类型${type}`,
      value: info.count
    }));
    const option = {
      tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
      series: [{
        name: '错误分布',
        type: 'pie',
        radius: ['40%', '70%'],
        data: data,
        color: ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4']
      }]
    };
    chartInstance.setOption(option);
    window.addEventListener('resize', () => chartInstance.resize());
  };

  </script>
  
  <style scoped>
  .learning-analysis {
    width: 700px;
}

.analysis-card {
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
}

.stats-overview {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(45deg, #aeecf9, rgb(255, 252, 198));
  border-radius: 15px;
  color: white;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.content-columns {
  display: flex;
  gap: 20px;
  max-height: 500px; /* 限制整体高度 */
}

.left-column, .right-column {
  flex: 1;
  overflow-y: auto; /* 启用垂直滚动 */
  padding-right: 10px;
}

.left-column {
  max-width: 50%;
}

.right-column {
  max-width: 50%;
}

.chart-section, .knowledge-section, .analysis-text-section {
  margin-bottom: 20px;
}

.chart-section h4, .knowledge-section h4, .analysis-text-section h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 18px;
  border-left: 4px solid #4eaae9;
  padding-left: 10px;
}

.error-chart {
  height: 200px; /* 缩小图表高度 */
  width: 300px;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); /* 缩小知识点网格 */
  gap: 15px;
}

.knowledge-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
  border-left: 4px solid #4eaae9;
}

.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.knowledge-name {
  font-weight: bold;
  font-size: 14px;
  color: #2c3e50;
}

.success-rate {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.success-rate.excellent {
  background: #d4edda;
  color: #155724;
}

.success-rate.good {
  background: #d1ecf1;
  color: #0c5460;
}

.success-rate.fair {
  background: #fff3cd;
  color: #856404;
}

.success-rate.poor {
  background: #f8d7da;
  color: #721c24;
}

.knowledge-details {
  font-size: 12px;
  color: #6c757d;
}

.detail-item {
  margin-bottom: 6px;
}

.problematic-titles {
  margin-top: 8px;
}

.problematic-titles .label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.titles {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.analysis-text {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 12px;
  line-height: 1.6;
  color: #495057;
}

.analysis-text :deep(h5) {
  color: #2c3e50;
  font-weight: 600;
  margin: 10px 0;
}

.analysis-text :deep(strong) {
  color: #2c3e50;
  font-weight: 600;
}

.analysis-text :deep(em) {
  color: #4eaae9;
  font-style: italic;
}

.analysis-text :deep(hr) {
  border: none;
  height: 2px;
  background: linear-gradient(to right, #4eaae9, transparent);
  margin: 15px 0;
}

.error-state {
  text-align: center;
  padding: 40px;
}

.retry-btn {
  margin-top: 20px;
}
</style>