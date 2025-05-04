<template>
  <div class="wrapper-a">
    <div class="title">
      <h2>已加入的班级</h2>
      <div class="btn">
          <el-button
              v-if="identity === 'TEACHER'"
              class="add-button"
              size="large"
              type="primary"
              :icon="Plus"
              @click="openDialog_create()"
          >
              创建班级
          </el-button>
          <el-button
              v-else
              class="add-button"
              size="large"
              type="primary"
              :icon="Plus"
              @click="openDialog_join()"
          >
              加入班级
          </el-button>
      </div>
    </div>
    <el-table :data="filterTableData" stripe="true" style="width: 100%">
        <el-table-column label="班级ID" prop="id" />
        <el-table-column label="班级名称" prop="name" />
        <el-table-column label="教师" prop="teacher" />
        <el-table-column align="right">
          <template #header>
            <el-input v-model="search" size="small" placeholder="输入搜索内容" />
          </template>
          <template #default="scope">
            <el-button type="primary" text="true" size="small" @click="handleEnter(scope.$index, scope.row)">
              进入
            </el-button>
          </template>
        </el-table-column>
    </el-table>
  </div>
  <!-- 接受邀请弹窗 -->
  <el-dialog
  title="加入班级"
  v-model="isDialogVisible_join"
  width="30%"
  >
  <el-input v-model="joinCode" placeholder="在此粘贴您的加入代码"></el-input>
  <template #footer>
      <div class="dialog-footer">
      <el-button @click="isDialogVisible_join = false">取消</el-button>
      <el-button type="primary" @click="handleJoin()">保存</el-button>
      </div>
  </template>
  </el-dialog>

  <!-- 创建新班级弹窗 -->
  <el-dialog
  title="创建班级"
  v-model="isDialogVisible_create"
  width="30%"
  >
  <el-input v-model="classname" placeholder="在此输入班级名称"></el-input>
  <template #footer>
      <div class="dialog-footer">
      <el-button @click="isDialogVisible_create = false">取消</el-button>
      <el-button type="primary" @click="handleCreate()">保存</el-button>
      </div>
  </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import axios from 'axios'

const identity = localStorage.getItem('role');
// const identity = ref('TEACHER');
const router = useRouter();

interface Class {
id: number;
className: string;
teacher: string;
}
const tableData = ref<Class[]>([]);
const search = ref('')
const filterTableData = computed(() =>
  tableData.value.filter(
      (data:any) =>
      !search.value ||
      data.ownerName.toLowerCase().includes(search.value.toLowerCase()) ||
      data.title.toLowerCase().includes(search.value.toLowerCase())
  )
);

  // 获取班级列表
const fetchClassList = async()=> {
  try {
    const response = await axios.get('http://localhost:8048/class/getclasslist',{
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
    tableData.value = response.data.map((item:Class) => ({
      id: item.id,
      name: item.className,
      teacher: item.teacher,
    }));
  } catch (error) {
    console.error('获取班级列表失败:', error);
    ElMessage.error('获取班级列表失败');
  }
}

onMounted(async () => {
  fetchClassList();
});

// 查看班级详情
const handleEnter = (index: number, row: any) => {
  console.log(row.id);
  router.push({
      path: '/classdetail',
      query: { classId: row.id.toString() } 
  })
}

// 加入班级
const isDialogVisible_join = ref(false);
const joinCode = ref<string>("");

const openDialog_join = () => {
  isDialogVisible_join.value = true;
};

const handleJoin = async () => {
  if (!joinCode.value.trim()) {
    ElMessage.warning('加入代码不能为空！');
    return;
  }

  try {
    const response = await axios.post(
      `http://localhost:8048/class/join`,
      {
        classCode: joinCode.value
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    ElMessage.success('授权成功！');
    router.push({
    path: '/classdetail',
    query: { classId: response.data } 
  })
  } catch (error:any) {
    console.error("错误:", error.response || error.message);
    ElMessage.error('加入班级失败');
  } finally {
    isDialogVisible_join.value = false; // 关闭弹窗
  }
};

// 老师创建班级
const isDialogVisible_create = ref(false);
const classname = ref<string>("");

const openDialog_create = () => {
  isDialogVisible_create.value = true;
};

const handleCreate = async () => {
  if (!classname.value.trim()) {
    ElMessage.warning('班级名称不能为空！');
    return;
  }

  try {
    const response = await axios.post(
      `http://localhost:8048/class/create?className=${classname.value}`,{},
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    ElMessage.success('创建成功！');
    router.push({
    path: '/classdetail',
    query: { classId: response.data } 
  })
  } catch (error:any) {
    console.error("错误:", error.response || error.message);
    ElMessage.error('创建新班级失败');
  } finally {
    isDialogVisible_create.value = false; // 关闭弹窗
  }
};
</script>

<style scoped>
.wrapper-a {
  width: 650px;
  padding: 30px;
  background-color: white;
  border-radius: 10px;
  color: #000;
}

.title {
  padding-bottom: 5px;
  color: #5d5d5d;
  border-bottom:1px solid #cccccc;
  display: flex;
  align-items: center;
  justify-content:space-between;
}
</style>