<template>
  <StickyNavbar/>
  <UserList
      :users="fetchedUsers"
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
        />
        <div v-if="!canCollaborate" class="overlay"></div>
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
          />
          <div v-if="!canCollaborate" class="overlay"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue';
import SharedbCodeMirror from '../components/SharedbCodeMirror.vue';
import CodeRunner from '../components/CodeRunner.vue';
import StickyNavbar from '../components/Navbar.vue';
import UserList from '../components/PeopleList.vue';
import axios from 'axios';
import { useRoute,useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

// 获取路由中的参数
const route = useRoute();
const router = useRouter();

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
  } catch (error) {
    ElMessage.error('获取文件信息失败');
    console.error('获取文件信息失败:', error)
  }
}

// 获取用户角色
const userRole = ref(localStorage.getItem("role") || '');
const userId = ref(localStorage.getItem("id")||'');

interface Users{
  id: number;
  name: string;
  micEnabled: boolean;
  canCollaborate: boolean;
}

const fetchedUsers = ref<Users[]>([]);
const canCollaborate = ref(false);

const fetchUsers = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getUsersByDocumentId?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
    fetchedUsers.value = response.data.map((user:any) => ({
      id: user.id,
      name: user.username,
      canCollaborate: false,
      micEnabled: false,
    }));

    const currentUser = fetchedUsers.value.find(user => user.id === Number(userId.value));
    if(currentUser){
      currentUser.canCollaborate = userRole.value === "TEACHER";
      canCollaborate.value = currentUser.canCollaborate;
    }
  } catch (error) {
    ElMessage.error('获取协作者列表失败');
    console.error('获取协作者列表失败:', error)
  }
}

// 时间戳记录
const classStartTime = ref<Date | null>(null);
const classEndTime = ref<Date | null>(null);

// 结束课堂 (老师)
const endClass = async () => {
  classEndTime.value = new Date();
  console.log('课堂结束时间:', classEndTime.value.toLocaleString());
  router.back();
}

const exitClass = () =>{
  router.back();
}

onMounted(() => {
  if(userRole.value==="TEACHER"){
    classStartTime.value = new Date();
    console.log('课堂开始时间:', classStartTime.value.toLocaleString());
  }
  documentId.value = Number(route.query.documentId);
  fetchFileInfo();
  fetchUsers();
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

.component-wrapper {
  position: relative;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
  pointer-events: all;
  cursor: not-allowed;
}
</style>