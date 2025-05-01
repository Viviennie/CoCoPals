<template>
    <div class="wrapper-a">
      <!-- 界面导航 -->
      <el-segmented
        v-model="value"
        :options="options"
        direction='vertical'
        class="nav"
      >
        <template #default="scope">
          <div
            :class="[
              'flex',
              'items-center',
              'gap-2',
              'flex-col',
              { active: activeSection === scope.item.label },
              'el-button-custom',
            ]"
            @click="setActiveSection(scope.item.label)"
          >
            <el-icon size="20">
              <component :is="scope.item.icon" />
            </el-icon>
            <div>{{ scope.item.label }}</div>
          </div>
        </template>
      </el-segmented>

      <el-card class="content">
        <div v-if="activeSection === 'Notice'">
            <ClassNotice 
              :classId=classId
            />
        </div>
        <div v-else-if="activeSection === 'Homework'">
            <Homework 
              :classId=classId
            />
        </div>
        <div v-if="activeSection === 'People'">
            <Classmates
              :classId=classId
            />
        </div>
        <div v-if="activeSection === 'Group'">
            <ClassChat
              :classId=classId
            />
        </div>
      </el-card>
    </div>
  </template>
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import {
    BellFilled,
    List,
    Avatar,
    Comment
  } from '@element-plus/icons-vue'
  import ClassNotice from '../components/ClassNotice.vue';
  import Homework from '../components/Homework.vue';
  import Classmates from '../components/Classmates.vue';
  import ClassChat from '../components/ClassChat.vue';
  import { useRoute } from 'vue-router';
  
  const route = useRoute(); 
  const value = ref('Notice')
  
  const options = [
    {
      label: 'Notice',
      value: 'Notice',
      icon: BellFilled,
    },
    {
      label: 'Homework',
      value: 'Homework',
      icon: List,
    },
    {
      label: 'People',
      value: 'People',
      icon: Avatar,
    },
    {
      label: 'Group',
      value: 'Group',
      icon: Comment,
    },
  ]

  const activeSection = ref('Notice');
  // 设置当前激活的展示内容
  const setActiveSection = (section: string) => {
    activeSection.value = section;
  };

  // 管理在 Main 组件中共享的状态
  const classId = ref<number>(0); 

  onMounted(() => {
    // 初始化从路由获取的参数
    console.log(route.query.classId);
    classId.value = Number(route.query.classId);
  });
  </script>

  <style scoped>
    .wrapper-a {
      padding: 50px;
      display: flex;
      align-items: start;
      justify-content:center;
      gap: 50px;
    }

    .nav {
        position: sticky;
        top: 0; 
        align-self: flex-start; 
    }

    .content {
        width: 800px;
        height: 1000px;
    }
  </style>