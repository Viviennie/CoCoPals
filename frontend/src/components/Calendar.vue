<template>
    <div class="calendar-container">
      <div class="calendar-header">
        <button @click="prevMonth" class="button">&lt;</button>
              <!-- 点击显示年份选择框 -->
        <span @click="toggleYearSelector" class="month-year">{{ currentMonthYear }}</span>
        <button @click="nextMonth" class="button">&gt;</button>
      </div>

      <!-- 年份选择下拉框，仅在显示时渲染 -->
    <div v-if="showYearSelector" class="year-select-container">
      <select v-model="selectedYear" @change="changeYear">
        <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
      </select>
    </div>

      <div class="calendar-weekdays">
        <span v-for="day in weekDays" :key="day">{{ day }}</span>
      </div>
      <div class="calendar-days">
        <span v-for="day in blankDays" :key="'blank-' + day"></span>
        <span
          v-for="day in daysInMonth"
          :key="day"
          :class="{ 'today': isToday(day), 'selected': isSelected(day) }"
          @click="selectDate(day)"
          :style="getDayStyle(day)"
        >
          {{ day }}
        </span>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, computed } from 'vue';

  // 获取日期样式的方法
    const getDayStyle = (day: number) => {
    if (isSelected(day)) {
        return {
        backgroundColor: 'black', 
        color: 'yellow', 
        };
    }
    return {};
    };
  // 日期数据
  const currentDate = ref(new Date());
  const selectedDate = ref(new Date());
  const showYearSelector = ref(false); // 控制年份选择框的显示
  
  // 星期标题
  const weekDays = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
  
  // 当前月年份显示
  const currentMonthYear = computed(() =>
    currentDate.value.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
  );
  
  // 获取当月的天数
  const daysInMonth = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month + 1, 0).getDate();
  });
  
  // 获取月份的第一天是星期几
  const blankDays = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month, 1).getDay();
  });

  // 生成年份选项列表
    const years = computed(() => {
    const currentYear = new Date().getFullYear();
    const startYear = currentYear - 10; // 从当前年份前10年
    const endYear = currentYear + 10; // 到当前年份后10年
    const yearList = [];
    return Array.from({ length: endYear - startYear + 1 }, (_, i) => startYear + i);
    });
    // 选中的年份
    const selectedYear = ref(currentDate.value.getFullYear());
  
  // 切换到上个月
  const prevMonth = () => {
    const date = new Date(currentDate.value); // 创建一个新的日期实例
    date.setMonth(date.getMonth() - 1);
    currentDate.value = date; // 重新赋值，触发响应式更新
  };
  
  // 切换到下个月
  const nextMonth = () => {
    const date = new Date(currentDate.value); // 创建一个新的日期实例
    date.setMonth(date.getMonth() + 1);
    currentDate.value = date; // 重新赋值，触发响应式更新
  };

  // 显示或隐藏年份选择框
    const toggleYearSelector = () => {
    showYearSelector.value = !showYearSelector.value;
    };

    // 切换到选择的年份
    const changeYear = () => {
    currentDate.value = new Date(selectedYear.value, currentDate.value.getMonth(), 1);
    showYearSelector.value = false; // 隐藏年份选择框
    };
  
  // 判断是否是今天
  const isToday = (day: number) => {
    const today = new Date();
    return (
      day === today.getDate() &&
      currentDate.value.getMonth() === today.getMonth() &&
      currentDate.value.getFullYear() === today.getFullYear()
    );
  };
  
  // 判断是否是选中的日期
  const isSelected = (day: number) => {
    return (
      day === selectedDate.value.getDate() &&
      currentDate.value.getMonth() === selectedDate.value.getMonth() &&
      currentDate.value.getFullYear() === selectedDate.value.getFullYear()
    );
  };
  
  // 选择日期
  const selectDate = (day: number) => {
    selectedDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
  };
  </script>
  
  <style scoped>
  .calendar-container {
    width: 300px;
    height: 280px;
    background-color: #e8e8e8;
    border-radius: 10px;
    padding: 10px;
    text-align: center;
    font-family: Arial, sans-serif;
    color: #333;
  }
  
  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .calendar-header button {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 18px;
  }
  
  .calendar-weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    font-size: 12px;
    color: #888;
    margin-bottom: 5px;
  }
  
  .calendar-days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 5px;
  }
  
  .calendar-days span {
    display: inline-block;
    width: 100%;
    height: 30px;
    line-height: 30px;
    text-align: center;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .calendar-days span.today {
    background-color: #d89081;
    color: white;
  }
  
  .calendar-days span.selected {
    

    color: white;
  }
  
  .calendar-days span:hover {
    background-color: #d0d0d0;
  }

  .year-select-container {
  margin-top: 5px;
    }

  </style>
  