<template>
    <div>
      <!-- 覆盖层，用于半透明遮罩背景 -->
      <div class="overlay" v-if="isOpen" @click="toggleSidebar"></div>
  
      <!-- 侧边栏 -->
      <nav class="sidebar" :class="{ open: isOpen }">
        <!-- 菜单标题 -->
        <div class="sidebar-header">
          <h2>User List</h2>
        </div>
  
        <ul class="user-item"> 
            <!-- 用户列表 -->
            <li
                v-for="user in props.users"
                :key="user.id"
                >
                <img src="https://img.icons8.com/bubbles/50/000000/user.png" alt="avatar" class="avatar" />
                <span class="user-name">{{ user.name }}</span>
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
  import { ref, reactive, defineProps } from 'vue';
  
  const isOpen = ref(false);
  
  // 切换侧边栏的打开/关闭状态
  const toggleSidebar = () => {
    isOpen.value = !isOpen.value;
  };

  const props = defineProps<{ 
    users: Coworker[];
  }>();

  // 消息数据类型
  interface Coworker {
    id: number;
    name: string;
  }

  </script>
  
  <style scoped>
  /* 侧边栏样式 */
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    width: 250px;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(10px);
    color: #fff;
    padding-top: 20px;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    z-index: 1000;
  }
  
  .sidebar.open {
    transform: translateX(0);
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
  
  /* 用户项样式 */
  .user-item {
    list-style: none;
    align-items: center;
    gap: 10px;
    padding: 0;
  }

  .user-item li {
    display: flex;
    align-items: center;
    padding:10px 20px;
    gap:10px;
  }

  .user-item :hover {
    background-color: rgba(0,0,0,0.1);
  }
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  
  /* 覆盖层样式 */
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
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
    transition: left 0.3s ease;
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
  }
  
  .hamburger.is-open .hamb-middle {
    display: none;
  }
  
  .hamburger.is-open .hamb-bottom {
    top: 50%;
    transform: rotate(-45deg);
  }
  </style>
  