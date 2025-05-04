<template>
  <nav :class="['navbar', navbarClass]">
    <div class="navbar-content">
      <!-- Logo区域 -->
      <div class="logo-container" @click="gotoGuide()">
        <img src="/unicorn.png" alt="logo" class="logo" />
        <!-- <img src="/name.png" alt="name" class="name" /> -->
        <p :style="{ fontWeight: 800, margin: '10px', fontSize: '20px' }" >CoCoPals</p>
      </div>
  
      <!-- 导航菜单 -->
      <ul class="nav-links">
        <li
            v-for="(link, index) in links"
            :key="index"
            class="nav-item"
            @mouseenter="showSubMenu(index)"
            @mouseleave="hideSubMenu"
          >
          <router-link
            :to="link.path"
            class="nav-link"
            :class="{ active: activeIndex === index }"
            @click="setActiveIndex(index)"
            :style="{ color: activeIndex === index ? '#9BBCC3' : 'black' }"
          >
            {{ link.label }}
          </router-link>
  
          <!-- 子菜单 -->
          <ul v-if="link.subMenu && hoveredIndex === index" class="sub-menu">
            <li v-for="(subLink, subIndex) in link.subMenu" :key="subIndex">
              <router-link :to="subLink.path" class="sub-nav-link">
                {{ subLink.label }}
              </router-link>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>
  </template>
    
  <script lang="ts" setup>
  import { ref, onMounted, watch, onBeforeUnmount, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute(); // 获取当前路由
  const router=useRouter();
  
  // 导航链接数据 - 翻译后的菜单项
  const links = [
    { label: '首页', path: '/' },
    { label: '自由编程', path: '/editlist' },
    { label: 'OJ在线评测', path: '/problemlist' },
    { label: '班级', path: '/class' },
    {
      label: '关于我们',
      path: '/about',
      subMenu: [
        { label: '项目详情', path: '/about' },
        { label: '相关算法', path: '/aboutalgorithm' }
      ]
    },
    { label: '我的', path: '/profile' },
  ];
  
  // 当前激活的导航项索引
  const activeIndex = ref(0);
  const lastScrollY = ref(0); // 记录上一次滚动的位置
  const isNavbarVisible = ref(true); // 控制导航栏的可见性
  const hoveredIndex = ref<number | null>(null); // 子菜单可见索引
  let timeoutId: number | null = null; 
  
  // 显示子菜单
  const showSubMenu = (index: number) => {
    if (timeoutId) {
      clearTimeout(timeoutId); // 如果有定时器正在运行，清除它
      timeoutId = null;
    }
    hoveredIndex.value = index;
  };
  
  // 隐藏子菜单
  const hideSubMenu = () => {
    timeoutId = window.setTimeout(() => {
      hoveredIndex.value = null;
    }, 500); 
  };
  
  // 根据当前路由设置 `activeIndex`
  const setActiveIndexByRoute = () => {
    const index = links.findIndex(link => link.path === route.path);
    activeIndex.value = index !== -1 ? index : 0; // 若未找到匹配路径，则默认为第一个导航项
  };
  
  // 处理滚动事件
  const handleScroll = () => {
    if (window.scrollY > lastScrollY.value) {
      isNavbarVisible.value = false; // 下滑时隐藏导航栏
    } else {
      isNavbarVisible.value = true; // 上滑时显示导航栏
    }
    lastScrollY.value = window.scrollY; // 更新上一次滚动的位置
  };
  
  // 添加滚动事件监听
  onMounted(() => {
    window.addEventListener('scroll', handleScroll);
    setActiveIndexByRoute(); // 初始化激活项
  });
  
  // 组件卸载时移除事件监听
  onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll);
  });
  
  // 计算类名
  const navbarClass = computed(() => {
    return isNavbarVisible.value ? '' : 'navbar-hidden';
  });
  
  
  // 点击时设置 `activeIndex`
  const setActiveIndex = (index: number) => {
    activeIndex.value = index;
  };
  
  const gotoGuide = () => {
    router.push('/guide');
  }
  </script>
  
  <style scoped>
  .navbar {
    position: fixed;
    top: 0;
    z-index: 1000;
    width: 100%;
    height: 60px;
    display: flex;
    justify-content: center;
    backdrop-filter: blur(10px);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    background-color: rgba(255, 255, 255, 0.2);
    transition: transform 0.3s ease; /* 更快的过渡时间 */
    transform: translateY(0); /* 默认显示 */
  }
  
  .navbar-hidden {
    transform: translateY(-100%); /* 隐藏时向上移动 */
  }
  
  
  .navbar-content {
    display: flex;
    align-items: center;
    width: 100%;
    max-width: 1300px;
  }
  
  .logo-container {
    margin-right: auto;
    display: flex;
    align-items: center;  
  }
  
  .logo {
    width: 50px;
    height: auto;
  }
  
  .name {
    width: 100%;
    height: 25px;
    margin-bottom: -5px;
  }
  
  .nav-links {
    list-style: none;
    display: flex;
    gap: 20px;
    padding: 0;
    margin: 0;
  }
  
  .nav-link {
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
  }
  
  .nav-link.active {
    font-weight: bold;
  }
  
  .sub-menu {
    position: absolute;
    top: 100%; /* 将子菜单放在导航项下方 */
    left: 83%;
    background-color: rgba(255,255,255);
    list-style: none;
    padding: 10px 0;
    margin: 0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    z-index: 1000; 
  }
  
  .sub-menu li {
    padding: 5px 20px;
  }
  
  .sub-menu li:hover {
    background-color: rgb(236, 236, 236);
  }
  
  .sub-nav-link {
    text-decoration: none;
    font-weight: 400;
    color: #333;
    transition: color 0.2s ease;
  }
  </style>