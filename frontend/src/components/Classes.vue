<template>
    <div class="wrapper-a">
      <div class="title">
        <h2>Joined Classes</h2>
        <div class="btn">
            <el-button
                v-if="identity === 'TEACHER'"
                class="add-button"
                size="large"
                type="primary"
                :icon="Plus"
                @click="openDialog_create()"
            >
                Create
            </el-button>
            <el-button
                v-else
                class="add-button"
                size="large"
                type="primary"
                :icon="Plus"
                @click="openDialog_join()"
            >
                Join
            </el-button>
        </div>
      </div>
      <el-table :data="filterTableData" stripe="true" style="width: 100%">
          <el-table-column label="ClassID" prop="id" />
          <el-table-column label="Class Name" prop="name" />
          <el-table-column label="Teacher" prop="teacher" />
          <el-table-column align="right">
            <template #header>
              <el-input v-model="search" size="small" placeholder="Type to search" />
            </template>
            <template #default="scope">
              <el-button type="primary" text="true" size="small" @click="handleEnter(scope.$index, scope.row)">
                Enter
              </el-button>
            </template>
          </el-table-column>
      </el-table>
    </div>
    <!-- 接受邀请弹窗 -->
    <el-dialog
    title="Join Class"
    v-model="isDialogVisible_join"
    width="30%"
    >
    <el-input v-model="joinCode" placeholder="Paste your join code here:"></el-input>
    <template #footer>
        <div class="dialog-footer">
        <el-button @click="isDialogVisible_join = false">cancel</el-button>
        <el-button type="primary" @click="handleJoin()">save</el-button>
        </div>
    </template>
    </el-dialog>

    <!-- 创建新班级弹窗 -->
    <el-dialog
    title="Create Class"
    v-model="isDialogVisible_create"
    width="30%"
    >
    <el-input v-model="classname" placeholder="Paste your class name here:"></el-input>
    <template #footer>
        <div class="dialog-footer">
        <el-button @click="isDialogVisible_create = false">cancel</el-button>
        <el-button type="primary" @click="handleCreate()">save</el-button>
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
      console.error('Error fetching class list:', error);
      ElMessage.error('Error fetching class list');
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
      ElMessage.warning('JoinCode cannot be empty!');
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
      ElMessage.success('Authorited!');
      router.push({
      path: '/classdetail',
      query: { classId: response.data } 
    })
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage.error('Failed to join the class.');
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
      ElMessage.warning('class name cannot be empty!');
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
      ElMessage.success('Created!');
      router.push({
      path: '/classdetail',
      query: { classId: response.data } 
    })
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage.error('Failed to create new class.');
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