<template>
    <el-button
        v-if="identity === 'TEACHER'"
        class="create-button"
        size="large"
        type="primary"
        :icon="Plus"
        @click="openDialog_create()"
    >
        Create
    </el-button>
    <div class="demo-collapse">
      <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item v-for="section in contents" :key="section.id" :title="section.title" :name="section.id">
            <div class="content">
                <div>{{ section.item.text }}</div>
                <div class="meta">
                <span class="time">{{ section.item.time }}</span>
                <span class="author">Author: {{ section.item.author }}</span>
                </div>
            </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 创建通知弹窗 -->
    <el-dialog
    title="Create Notice"
    v-model="isDialogVisible_create"
    width="60%"
    >
    <el-input v-model="title" placeholder="Title..."></el-input>
    <div style="margin: 20px 0" />
    <el-input v-model="content" placeholder="Content..." type="textarea" maxlength="100" show-word-limit></el-input>
    <template #footer>
        <div class="dialog-footer">
        <el-button @click="isDialogVisible_create = false">cancel</el-button>
        <el-button type="primary" @click="handleCreate()">save</el-button>
        </div>
    </template>
    </el-dialog>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import { ElMessage, type CollapseModelValue } from 'element-plus'
  import axios from 'axios';
  import { Plus } from '@element-plus/icons-vue';

  // Props 接收从父组件传递过来的数据
  const props = defineProps<{ 
    classId: number;
  }>();
  
  const identity = localStorage.getItem('role');
  const classId = ref(props.classId);
  const contents = ref<{ id: string; title: string; item: { text: string; time: string; author: string } }[]>([])
  const activeNames = ref(['1'])
  const handleChange = (val: CollapseModelValue) => {
  }

  // 从后端获取通知列表数据
const fetchNotifications = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/class/getNotice?classId=${classId.value}`) 
    const notifications = response.data;

    contents.value = notifications.map((notification: any, index: number) => ({
      id: `${index + 1}`,
      title: notification.title,
      item: {
        text: notification.content,
        time: notification.createTime,
        author: notification.teacherName,
      },
    }))
  } catch (error) {
    console.error('Error fetching notifications:', error)
  }
}

  onMounted(() => {
    fetchNotifications()
  })

  // 创建通知
  const isDialogVisible_create = ref(false);
  const title = ref<string>("");
  const content = ref<string>("");

  const openDialog_create = () => {
    isDialogVisible_create.value = true;
  };

  const handleCreate = async () => {
    if (!title.value.trim()||!title.value.trim()) {
      ElMessage.warning('title or content cannot be empty!');
      return;
    }

    try {
      const response = await axios.post(
        `http://localhost:8048/class/createNotice`,
        {
          classId: classId.value,
          title: title.value,
          content: content.value
        },
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      ElMessage.success('Created!');
      fetchNotifications();
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage.error('Failed to create notice.');
    } finally {
      isDialogVisible_create.value = false; 
    }
  };
  </script>

<style scoped>
.demo-collapse {
  max-width: 600px;
  margin: 0 auto;
}

.content {
  margin-bottom: 16px;
}

.meta {
  font-size: 12px; /* 小字 */
  color: #666; /* 灰色 */
  margin-top: 8px;
}

.time {
  margin-right: 8px;
}

.author {
  font-style: italic; /* 斜体 */
}
</style>
  