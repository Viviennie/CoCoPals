<template>
  <el-button
      v-if="identity === 'TEACHER'"
      class="invite-button"
      size="large"
      type="primary"
      :icon="Plus"
      @click="inviteOthers()"
  >
      邀请
  </el-button>
  <el-table :data="filterTableData" stripe="true" style="width: 100%">
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="邮箱" prop="email" />
      <el-table-column align="right">
          <template #header>
            <el-input v-model="search" size="small" placeholder="输入搜索内容" />
          </template>
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleChat(scope.$index, scope.row)">
              聊天
            </el-button>
          </template>
        </el-table-column>
  </el-table>

  <!-- 弹窗展示邀请码 -->
  <el-dialog
    title="您的邀请码"
    v-model="dialogVisible"
    width="400px"
  >
  <div style="display: flex;justify-content: space-around;align-items: center;">
    <p>{{ inviteCode }}</p>
    <el-button 
      type="primary"
      circle
      @click="copyInviteCode" 
      :disabled="!inviteCode"
    >
    <el-icon><CopyDocument /></el-icon>
    </el-button>
  </div>
  <template #footer>
    <div class="dialog-footer">
      <p style="color: red;font-size: small;">注意：10分钟内有效</p>
      <el-button @click="dialogVisible = false" type="primary">关闭</el-button>
    </div>
  </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { CopyDocument, Plus } from '@element-plus/icons-vue';
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const identity = localStorage.getItem('role');

interface Stu {
  username: string;
  email: string;
}
const tableData = ref<Stu[]>([]);
const search = ref('')
const filterTableData = computed(() =>
  tableData.value.filter(
      (data:any) =>
      !search.value ||
      data.ownerName.toLowerCase().includes(search.value.toLowerCase()) ||
      data.title.toLowerCase().includes(search.value.toLowerCase())
  )
);

// 获取班级成员列表
const fetchStuList = async()=> {
  try {
    const response = await axios.get(`http://localhost:8048/class/getstulist?classId=${classId.value}`);
    tableData.value = response.data.map((item:Stu) => ({
      name: item.username,
      email: item.email,
    }));
  } catch (error) {
    console.error('获取列表失败:', error);
    ElMessage.error('获取列表失败');
  }
}

onMounted(async () => {
  fetchStuList();
});

const props = defineProps<{ 
  classId: number;
}>();

const classId = ref(props.classId);
const inviteCode = ref<string>('');
const dialogVisible = ref(false);

const inviteOthers= async()=> {
  if (classId.value !== null) {
      try {
      const response = await axios.post(
          `http://localhost:8048/class/generatecode?classId=${classId.value}`,
          {
          headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
          }
      );
      inviteCode.value = response.data;
      console.log("成功生成邀请码!");
      dialogVisible.value = true; 
      } catch (error:any) {
      if(error.response){
          ElMessage.error('错误：请检查Redis服务器');
      }
      console.error('获取邀请码失败:', error);
      inviteCode.value = '';
      }
  }
}

// 复制邀请码到剪贴板
const copyInviteCode = () => {
  if (inviteCode.value) {
      navigator.clipboard.writeText(inviteCode.value)
      .then(() => {
          ElMessage.success('已复制!');
      })
      .catch((err) => {
          console.error('复制失败:', err);
      });
  }
};

</script>

<style scoped>
.dialog-footer {
display: flex;
justify-content: space-between;
align-items: center;
}
</style>