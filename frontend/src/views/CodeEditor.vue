<template>
  <StickyNavbar/>
  <UserList 
    :users="fetchedUsers"
  />
  <div class="wrapper-ce">
    <div class="inner-wrapper">
      <!-- 代码运行器 -->
      <CodeRunner
        :code="code"
        :selectedLanguage="selectedLanguage"
        height= "500px"
        width="200px"
      />
      <div class="code-section">
        <div class="title">
          <p><strong>Title:</strong> {{ fileInfo.title }}</p>
          <p><strong>Owner:</strong> {{ fileInfo.owner }}</p>
          <p><strong>Create Time:</strong> {{ fileInfo.createdAt }}</p>
        </div>
        <!-- 语言选择器和主题选择器以及代码编辑器 -->
        <SharedbCodeMirror
          v-if="documentId !== 0"
          v-model:code="code"
          v-model:selectedLanguage="selectedLanguage"
          height= "500px"
          width="900px"
          :documentId=documentId
        />
      </div>
      <!-- <div class="chat-section">
        <ChatApp
          height= "770px"
          width="300px"
        />
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import SharedbCodeMirror from '../components/SharedbCodeMirror.vue'; 
import CodeRunner from '../components/CodeRunner.vue'; 
import StickyNavbar from '../components/Navbar.vue';
import UserList from '../components/PeopleList.vue';
import axios from 'axios';
// import ChatApp from '../components/ChatApp.vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
  
// 获取路由中的参数
const route = useRoute();

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
    ElMessage.error('Error fetching file infomation');
    console.error('Error fetching file infomation:', error)
  }
}

const fetchedUsers = ref([]);

const fetchUsers = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getUsersByDocumentId?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
    console.log(response.data);
    fetchedUsers.value = response.data.map((user:any) => ({
      id: user.id,
      name: user.username,
    }));
    console.log(fetchedUsers.value)
  } catch (error) {
    ElMessage.error('Error fetching collaborators');
    console.error('Error fetching collaborators:', error)
  }
}

onMounted(() => {
  // 初始化从路由获取的参数
  console.log(route.query.documentId);
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
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
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
  gap:20px;
}

.chat-section {

}

</style>