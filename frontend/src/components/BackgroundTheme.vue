<!-- src/components/ThemeSelector.vue -->
<template>
    <div class="theme-btn-container">
      <div
        v-for="(theme, index) in themes"
        :key="index"
        class="theme-btn"
        :style="{ backgroundColor: theme.background, width: '25px', height: '25px' }"
        @click="setTheme(theme)"
      ></div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, defineProps, watch } from 'vue';

  // 定义 Theme 接口
  interface Theme {
    background: string;
    color: string;
    primaryColor: string;
  }
  
  // 传入默认主题和当前主题的 props
  const props = defineProps<{ initialTheme: Theme }>();
  
  // 定义主题数组
  const themes: Theme[] = [
    {
      background: '#231F20',
      color: '#FFF',
      primaryColor: '#BB4430'
    },
    {
      background: '#1A1A2E',
      color: '#FFFFFF',
      primaryColor: '#0F3460'
    },
    {
      background: '#192A51',
      color: '#FFFFFF',
      primaryColor: '#967AA1'
    },
    {
      background: '#513533',
      color: '#FFFFFF',
      primaryColor: '#e2b9b3'
    },
    {
      background: '#e3e1c8',
      color: '#000000',
      primaryColor: '#518f8b'
    },
    {
      background: '#ebedec',
      color: '#000000',
      primaryColor: '#30A9DE'
    }
  ];
  
  // 当前主题使用 ref 来追踪
  const currentTheme = ref<Theme>(props.initialTheme);
  
  // 当主题变更时更新样式
  watch(currentTheme, (newTheme) => {
    document.documentElement.style.setProperty('--background', newTheme.background);
    document.documentElement.style.setProperty('--color', newTheme.color);
    document.documentElement.style.setProperty('--primary-color', newTheme.primaryColor);
  });
  
  // 设置新主题的函数
  const setTheme = (theme: Theme) => {
    currentTheme.value = theme;
  };
  </script>
  
  <style scoped>
  .theme-btn-container {
    position: fixed;
    left: 0;
    bottom: 2rem;
    z-index: 1000;
  }
    
  .theme-btn {
    cursor: pointer;
    transition: all 0.3s ease-in;
  }
  
  .theme-btn:hover {
    width: 40px !important;
  }

  </style>
  