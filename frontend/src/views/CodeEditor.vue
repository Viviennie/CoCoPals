<template>
  <StickyNavbar/>
  <UserList
      :documentId="documentId"
  />
  <div class="wrapper-ce">
    <div class="inner-wrapper">

      <!-- 代码运行器 -->
      <div class="component-wrapper">
        <CodeRunner
            :code="code"
            :selectedLanguage="selectedLanguage"
            height="500px"
            width="200px"
            :canCollaborate="canCollaborate"
        />
      </div>

      <div class="code-section">
        <div class="title">
          <p><strong>标题：</strong> {{ fileInfo.title }}</p>
          <p><strong>所有者：</strong> {{ fileInfo.owner }}</p>
          <p><strong>创建时间：</strong> {{ fileInfo.createdAt }}</p>
          <div class="class-control">
            <button
                v-if="userRole === 'TEACHER'"
                @click="endClass"
                class="end-class-btn"
            >
              结束课堂
            </button>
            <button
                v-else
                @click="exitClass"
                class="exit-class-btn"
            >
              退出课堂
            </button>
          </div>
        </div>
        <!-- 语言选择器和主题选择器以及代码编辑器 -->
        <div class="component-wrapper">
          <SharedbCodeMirror
              v-if="documentId !== 0"
              v-model:code="code"
              v-model:selectedLanguage="selectedLanguage"
              height="500px"
              width="900px"
              :documentId=documentId
              :canCollaborate="canCollaborate"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import SharedbCodeMirror from '../components/SharedbCodeMirror.vue';
import CodeRunner from '../components/CodeRunner.vue';
import StickyNavbar from '../components/Navbar.vue';
import UserList from '../components/PeopleList.vue';
import {useClassStore} from '../stores/classStore';
import axios from 'axios';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';

// 获取路由中的参数
const route = useRoute();
const router = useRouter();
const classStore = useClassStore();

// 管理在 Main 组件中共享的状态
const documentId = ref<number>(0);
const code = ref<string>("");  // 保存编辑器中的代码
const selectedLanguage = ref<string>("python");  // 保存用户选择的语言

const fileInfo = ref({
  title: '',
  owner: '',
  createdAt: '',
});

const fetchFileInfo = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getfileinfo?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
    fileInfo.value = {
      title: response.data.title,
      owner: response.data.ownerName,
      createdAt: response.data.createTime,
    };
    return fileInfo.value.title; // 返回文档名称
  } catch (error) {
    ElMessage.error('获取文件信息失败');
    console.error('获取文件信息失败:', error)
    return '';
  }
}

// 获取用户角色
const userRole = ref(localStorage.getItem("role") || '');
const userId = ref(localStorage.getItem("id")||'');

const fetchUsers = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getUsersByDocumentId?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });

    console.log(response.data)
    console.log(fileInfo.value.owner)
    return response.data.map((user: any) => ({
      id: user.id,
      name: user.username,
      canCollaborate: user.username === fileInfo.value.owner,
      micEnabled: false,
    }));
  } catch (error) {
    ElMessage.error('获取协作者列表失败');
    console.error('获取协作者列表失败:', error)
    return [];
  }
}

// 计算当前课程
const currentClass = computed(() => {
  return classStore.classes.find(c => c.documentId === documentId.value);
});

// 计算当前用户的协作权限
const canCollaborate = computed(() => {
  const user = currentClass.value?.users.find(u => u.id === Number(userId.value));
  return user?.canCollaborate || false;
});

// 结束课堂 (老师)
const endClass = async () => {
  // const documentName = await fetchFileInfo();
  // classStore.endClass(documentName);
  router.back();
}

const exitClass = () => {
  router.back();
}

onMounted(async () => {
  documentId.value = Number(route.query.documentId);
  await fetchFileInfo();
  const users = await fetchUsers();

  // 添加到课程列表
  classStore.addClass(documentId.value, users);
});
</script>

<style scoped>
.wrapper-ce {
  margin-top: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.inner-wrapper {
  display: flex;
  justify-content: center;
  align-items: end;
  border-radius: 10px;
  padding: 10px;
  gap: 10px;
}

.code-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.title {
  display: flex;
  justify-content: space-between;
  align-items:center;
  width:100%;
}

.class-control {
  display: flex;
  align-items:center;
}

.end-class-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.end-class-btn:hover {
  background-color: #ff7875;
}

.exit-class-btn {
  background-color: transparent;
  color: #ff4d4f;
  border: 1px solid #ff4d4f;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.exit-class-btn:hover {
  background-color: #fff2f0;
}
</style>