<template>
    <section class="wrapper-no">
      <StickyNavbar />
      <div  class="container">
        <!-- 侧边导航栏 -->
        <Sidebar
          :currentProblemId="currentProblemId"
          @updateProblem="handleSelectProblem"
        />
    
        <!-- 主内容区 -->
        <div class="main-section">
          <!-- 显示题目信息 -->
          <el-scrollbar class="problem" v-if="currentProblem">
            <h2>{{ currentProblem.title }}</h2>
            <MarkdownRenderer :markdown-text="currentProblem.description" />
            <el-button type="text">answer</el-button>
          </el-scrollbar>
    
          <div class="coding">
            <!-- 代码结果判题器 -->
            <CodeReviewer
              :code="code"
              :selectedLanguage="selectedLanguage"
              :problemId="currentProblemId"
            />
    
            <!-- 语言选择器和主题选择器以及代码编辑器 -->
            <LanguageAndThemeSelector
              v-model:code="code"
              v-model:selectedLanguage="selectedLanguage"
              height= "400px"
              width="780px"
            />
    
            <!-- 代码运行器 -->
            <CodeRunner
              :code="code"
              :selectedLanguage="selectedLanguage"
              height= "150px"
              width="730px"
            />
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup lang="ts">
  import { ref, watch, onMounted } from 'vue';
  import LanguageAndThemeSelector from '../components/LanguageAndThemeSelector.vue'; 
  import MarkdownRenderer from '../components/MarkdownRenderer.vue';
  import CodeRunner from '../components/CodeRunner.vue'; 
  import CodeReviewer from '../components/CodeReviewer.vue'; 
  import StickyNavbar from '../components/Navbar.vue';
  import Sidebar from '../components/Sidebar.vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';

  // 管理在 Main 组件中共享的状态
  const route = useRoute();
  const code = ref<string>("");  // 保存编辑器中的代码
  const selectedLanguage = ref<string>("python");  // 保存用户选择的语言
  const currentProblemId = ref<number>(Number(route.query.currentProblemId));  // 当前选中的题目 ID
  const currentProblem = ref<{ title: string; description: string } | null>(null);  // 当前题目信息

  // 请求具体题目的数据
  const loadProblem = async (id: number) => {
    try {
      const response = await axios.get(`http://localhost:8048/question/get`, {
        params: { questionId: id },  // 使用params传递查询参数
      });
      currentProblem.value = response.data;
    } catch (error) {
      console.error('Error loading problem:', error);
    }
  };

  // 当用户选择某个题目时，更新 currentProblemId 并加载对应的题目
  const handleSelectProblem = (id: number) => {
    currentProblemId.value = id;
    console.log(id);
  };

  // 监听 currentProblemId 变化，自动加载对应题目
  watch(currentProblemId, (newId) => {
    if (newId) {
      loadProblem(newId);
    }
  });

  // 页面加载时调用
  onMounted(() => {
    loadProblem(currentProblemId.value);  // 初始加载时获取默认题目
  });
  
  </script>
  
  <style scoped>  
  .wrapper-no {
    margin-top: 50px;
  }
  
  .container {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .el-menu-vertical-demo {
    margin: 10px;
    border-radius: 10px;
    height: 98%;
  }

  .main-section {
    flex-grow: 1; 
    padding: 30px;
    display: flex;
    justify-content: space-around;
    gap: 20px;
    height: 100vh;
  }
  
  .problem {
    width: 45%;
    padding: 0 20px;
  }

  .coding {
    width: 55%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  </style>
  