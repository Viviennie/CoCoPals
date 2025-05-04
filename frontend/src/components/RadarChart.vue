<template>
  <div class="radar-chart-container">
    <div ref="chart" class="radar-chart"></div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';

// 雷达图数据
const indicatorData = [
{ name: '互动教学', max: 50 },  <!-- 翻译为中文 -->
{ name: '团队项目', max: 50 },  <!-- 翻译为中文 -->
{ name: '团队竞赛', max: 50 },  <!-- 翻译为中文 -->
{ name: '同伴代码\n评审', max: 50 },  <!-- 翻译为中文 -->
{ name: '自由探索', max: 50 },  <!-- 翻译为中文 -->
{ name: '个人练习', max: 50 },  <!-- 翻译为中文 -->
];

// 雷达图数据系列
const seriesData = [
  {
    value: [10, 5, 20, 35, 20, 10],
    name: '时间占比/%',
    itemStyle: {
      color: '#216484',
    },
    areaStyle: {
      opacity: 0.2,
    },
  },
];

// 图表的 DOM 节点引用
const chart = ref<HTMLElement | null>(null);

// 图表实例
let myChart: echarts.ECharts | null = null;

const renderChart = () => {
  if (!chart.value) return;

  // 初始化 ECharts 实例
  myChart = echarts.init(chart.value);

  // 设置雷达图选项
  const chartOptions = {
    tooltip: {
      trigger: 'item',
    },
    radar: {
      shape: 'polygon',
      indicator: indicatorData,
      axisName: {
        color: '#888888',
        fontSize: 8,
        formatter: (value:any) => value.split('\n').join('\n'), // 允许分行
      },
      splitLine: {
        lineStyle: {
          color: '#e8e8e8',
        },
      },
      splitArea: {
        areaStyle: {
          color: ['#f8f8f8', '#fff'],
        },
      },
      center: ['50%', '50%'],
      radius: '65%',
    },
    series: [
      {
        name: '时间占比/%',
        type: 'radar',
        data: seriesData,
        lineStyle: {
          color: '#216484',
          width: 2,
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#216484',
        },
        areaStyle: {
          color: '#216484',
          opacity: 0.2,
        },
      },
    ],
  };

  // 使用配置项显示图表
  myChart.setOption(chartOptions);
};

watch(() => 'yellow', renderChart);
watch(() => 'black', renderChart);

// 组件挂载后渲染图表
onMounted(() => {
  renderChart();
});

// 响应窗口大小变化自动调整图表大小
window.addEventListener('resize', () => {
  if (myChart) {
    myChart.resize();
  }
});
</script>

<style scoped>
.radar-chart-container {
  display: flex;
  position: relative;
  justify-content: center;
  align-items: center;
  width: 330px;
  height: 290px;
  max-width: 600px;
  margin: 0 auto;
  padding: 5px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(73, 72, 72, 0.1);
}

.radar-chart {
  border-radius: 0;
  position: absolute;
  left:5%;
  width: 90%;
  height: 90%;
  margin:0;
  padding: 0;
}
</style>