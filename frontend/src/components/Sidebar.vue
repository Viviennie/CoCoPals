<!-- Sidebar.vue -->
<template>
    <div>
      <!-- 覆盖层，用于半透明遮罩背景 -->
      <div class="overlay" v-if="isOpen" @click="toggleSidebar"></div>
  
      <!-- 侧边栏 -->
      <nav class="sidebar" :class="{ open: isOpen }">
        <!-- 菜单标题 -->
        <div class="sidebar-header">
          <h2>Problem List</h2>
        </div>
  
        <!-- 题目列表 -->
        <ul class="sidebar-menu">
          <li
            v-for="problem in problems"
            :key="problem.id"
            :class="{ active: currentProblemId === problem.id }"
            @click="selectProblem(problem.id)"
          >
            {{ problem.title }}
          </li>
        </ul>
      </nav>
      <button class="hamburger" :class="isOpen ? 'is-open' : 'is-closed'" @click="toggleSidebar">
        <span class="hamb-top"></span>
        <span class="hamb-middle"></span>
        <span class="hamb-bottom"></span>
      </button>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted, defineProps, defineEmits } from 'vue';
  import axios from 'axios';
  
  const isOpen = ref(false);
  
  // 接收父组件传入的 props
  const props = defineProps<{
    currentProblemId: number;
  }>();
  
  // 切换侧边栏的打开/关闭状态
  const toggleSidebar = () => {
    isOpen.value = !isOpen.value;
  };
  
  // 定义 emits，用于向父组件传递事件
  const emit = defineEmits<{
    (e: 'updateProblem', id: number): void;
  }>();
  
  // 使用 `ref` 创建一个响应式变量 `currentProblemId`，并初始化为父组件传入的值
  const currentProblemId = ref(props.currentProblemId);
  
  // 使用 `ref` 创建 `problems`，并确保类型为 `Array<{ id: number; title: string }>`
  const problems = ref<{ id: number; title: string }[]>([]);
  
  // 更新当前题目的函数
  const selectProblem = (id: number) => {
    currentProblemId.value = id;
    // 使用 emit 发送事件
    emit('updateProblem', id);
  };
  
  // 加载数据
  onMounted(async () => {
    try {
      if (!problems.value.length) {
        const response = await axios.get('http://localhost:8048/question/getList');
        problems.value = response.data;
      }
    } catch (error) {
      console.error('Failed to load problems:', error);
    }
  });
  </script>
  
  
  <style scoped>
/* 侧边栏样式 */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 250px;
  height: 100%;
  background-color: rgba(255,255,255,0.3);
  backdrop-filter: blur(10px); 
  color: #fff;
  padding-top: 20px;
  transform: translateX(-100%); /* 初始状态隐藏 */
  transition: transform 0.3s ease; /* 平滑动画效果 */
  z-index: 1000;
}

.sidebar.open {
  transform: translateX(0); /* 打开状态平滑滑出 */
}

.close-icon {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 24px;
  cursor: pointer;
}

/* 菜单标题样式 */
.sidebar-header {
  text-align: center;
  padding: 10px;
  font-size: 1rem;
  font-weight: bold;
  color: #ffffff;
}

/* 菜单样式 */
.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 20px 0;
}

.sidebar-menu li {
  padding: 15px 20px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sidebar-menu li:hover {
  background-color: rgba(0,0,0,0.1);
}

.sidebar-menu li.active {
  background-color: rgba(0,0,0,0.4);
  color: rgb(223, 222, 222);
}

/* 覆盖层样式 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明背景 */
  z-index: 900;
}

/* 汉堡菜单样式 */
.hamburger {
  background: transparent;
  border: none;
  display: block;
  height: 32px;
  margin-left: 15px;
  position: fixed;
  top: 15px;
  left: 10px;
  width: 32px;
  z-index: 1100;
  cursor: pointer;
  transition: left 0.3s ease; /* 汉堡位置的平滑过渡 */
}

.hamburger:hover .hamb-top {
  transform: translate(0, -50%);
}

.hamburger:hover .hamb-bottom {
    transform: translate(0, 50%);
}

/* 点击后移动汉堡按钮到侧边栏右侧 */
.hamburger.is-open {
  left: 270px; /* 侧边栏宽度加上偏移量 */
}

.hamburger span {
  display: block;
  height: 4px;
  width: 100%;
  background-color: #ffffff;
  border-radius: 1px;
  position: absolute;
  transition: all 0.35s ease-in-out;
}

.hamburger.is-closed .hamb-top {
  top: 5px;
}

.hamburger.is-closed .hamb-middle {
  top: 50%;
  transform: translateY(-50%);
}

.hamburger.is-closed .hamb-bottom {
  bottom: 5px;
}

.hamburger.is-open .hamb-top {
  top: 50%;
  transform: rotate(45deg);
  background-color: #ffffff;
}

.hamburger.is-open .hamb-middle {
  display: none;
}

.hamburger.is-open .hamb-bottom {
  top: 50%;
  transform: rotate(-45deg);
  background-color: #ffffff;
}

</style>
