<template>
    <el-button
        v-if="identity === 'TEACHER'"
        size="large"
        type="primary"
        :icon="Plus"
        @click="openDialog_create()"
    >
        Create
    </el-button>
    <div class="timeline-container">
      <el-timeline style="max-width: 600px;">
        <el-timeline-item
          v-for="(section, index) in sections"
          :key="index"
          :timestamp="section.dueTime"
          placement="top"
        >
          <el-card :class="getStatusClass(section.status)">
            <div class="card-content">
              <div class="content-left">
                <h4>{{ section.problemId }}. {{ section.title }}</h4>
                <p>Status: {{ section.status }}</p>
              </div>
              <el-button
                class="try"
                type="text"
                @click="goto(section.problemId)"
                >
                Try
              </el-button>
              <el-button
                  v-if="identity === 'TEACHER'"
                  size="small"
                  type="primary"
                  :icon="View"
                  @click="openDialog_view(section.problemId)"
              >
              </el-button>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>

    <!-- 创建作业弹窗 -->
    <el-dialog
      title="Create Assignment"
      v-model="isDialogVisible_create"
      width="60%"
    >
      <div>
      <span class="demonstration">Problem</span>
      <el-select v-model="problemId" filterable placeholder="Select" style="width:100%">
        <el-option
            v-for="item in problems"
            :key="item.problemId"
            :label="item.label"
            :value="item.problemId"
        >
            <span style="float: left">{{ item.problemId }}</span>
            <span
                style="
                float: right;
                color: var(--el-text-color-secondary);
                font-size: 13px;
                "
            >
                {{ item.label }}
            </span>
        </el-option>
      </el-select>
      </div>
      <div style="margin: 20px 0" />
      <div>
        <span class="demonstration">Due</span>
        <el-date-picker
            v-model="time"
            type="datetime"
            placeholder="Select date and time"
            style="width:100%"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
        <el-button @click="isDialogVisible_create = false">cancel</el-button>
        <el-button type="primary" @click="handleCreate()">save</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看作业情况弹窗 -->
    <el-dialog
      title="View Homework"
      v-model="isDialogVisible_view"
      width="80%"
      >
      <el-table :data="filterTableData" stripe="true" style="width: 100%">
        <el-table-column label="StudentID" prop="studentId" />
        <el-table-column label="Name" prop="name" />
        <el-table-column label="Status" prop="status" />
        <el-table-column align="right">
        <template #header>
            <el-input v-model="search" size="small" placeholder="Type to search" />
        </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isDialogVisible_view = false">cancel</el-button>
        </div>
      </template>
    </el-dialog>
  </template>
  
  <script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios';
  import { Plus, View } from '@element-plus/icons-vue';
  import { ElMessage } from 'element-plus';

  const identity = localStorage.getItem('role');
// const identity = ref('TEACHER');
// Props 接收从父组件传递过来的数据
const props = defineProps<{ 
  classId: number;
}>();

const classId = ref(props.classId);
const sections = ref<{ problemId: string; title: string; dueTime: string; status: string }[]>([])

  // 从后端获取作业列表数据
const fetchAssignments = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/class/getHomeworkList?classId=${classId.value}`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }) 
    const assignments = response.data;

    sections.value = assignments.map((assignment: any) => ({
      problemId: assignment.problemId,
      title: assignment.title,
      dueTime: assignment.dueTime,
      status: assignment.status,
    }))
    .sort((a:any, b:any) => new Date(b.dueTime).getTime() - new Date(a.dueTime).getTime());
  } catch (error) {
    console.error('Error fetching assignments:', error)
  }
}

  // 根据状态返回对应的类名,改变卡片颜色
  const getStatusClass = (status: string): string => {
    switch (status) {
        case 'passed':
        return 'completed';
        case 'failed':
        return 'in-progress';
        case 'not tried':
        return 'not-started';
        default:
        return '';
    }
  };

  // 前往刷题快捷键
  const router = useRouter();
  const goto =(id:any)=>{
    router.push({
        path: '/normalOJ',
        query: { currentProblemId: id.toString() } 
    })
  }

  const problemId = ref<number | null>(null)
  const problems = ref<{ problemId: number; label: string }[]>([])
  const time = ref('')

  const fetchProblemList = async()=> {
    try {
      const response = await axios.get('http://localhost:8048/question/getListByUser',{
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        problems.value = response.data.map((item:any) => ({
        problemId: item.id,
        label: item.title
      }));
    } catch (error) {
      console.error('Error fetching list:', error);
    }
  }

  // 创建作业
  const isDialogVisible_create = ref(false);

  const openDialog_create = () => {
    isDialogVisible_create.value = true;
  };

  const handleCreate = async () => {
    if (!problemId.value || !time.value) { 
      ElMessage.warning('Problem and Due Time cannot be empty!');
      return;
    }

    try {
      const response = await axios.post(
        `http://localhost:8048/class/createAssignment`,
        {
          classId: classId.value,
          problemId: problemId.value, // 前端选择的问题ID
          dueTime: time.value // 前端选择的时间
        },
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      console.log(response.data);
      ElMessage.success('Created!');
      fetchAssignments();
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage.error('Failed to create.');
    } finally {
      isDialogVisible_create.value = false; 
    }
  };

  // 查看作业
  const isDialogVisible_view = ref(false);
  interface StuHomework {
    studentId: string; 
    name: string; 
    status: string;
  }
  const tableData = ref<StuHomework[]>([]);
  const search = ref('')
  const filterTableData = computed(() =>
    tableData.value.filter(
        (data:any) =>
        !search.value ||
        data.ownerName.toLowerCase().includes(search.value.toLowerCase()) ||
        data.title.toLowerCase().includes(search.value.toLowerCase())
    )
  );

  const currentHomework = ref(0);
  const openDialog_view = (id:any) => {
    isDialogVisible_view.value = true;
    currentHomework.value=id.toString();
    fetchStuHomework();
  };

  const fetchStuHomework = async () => {
    try {
      const response = await axios.get(`http://localhost:8048/class/getStuHomework?classId=${classId.value}&problemId=${currentHomework.value}`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      ) 
      tableData.value = response.data.map((item:StuHomework) => ({
        studentId: item.studentId,
        name: item.name,
        status: item.status,
      }));
    } catch (error) {
      console.error('Error fetching assignments:', error)
    }
  }

  onMounted(() => {
    fetchAssignments();
    fetchProblemList();
  })
  </script>
  
  <style scoped>
  .timeline-container {
    margin: auto;
    padding: 20px;
  }

  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .content-left {
    flex: 1;
  }

  /* 不同状态的卡片样式 */
 .completed {
    background-color: #e6ffed; 
    border-left: 5px solid #28a745; 
  }

  .in-progress {
    background-color: #fff3cd; 
    border-left: 5px solid #ffc107;
  }

  .not-started {
    background-color: #f8d7da; 
    border-left: 5px solid #dc3545; 
  }

  .demonstration {
    display: block;
    color: var(--el-text-color-secondary);
    font-size: 14px;
    margin-bottom: 20px;
  }
  </style>