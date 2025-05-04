<template>
  <el-button
      v-if="identity === 'TEACHER'"
      size="large"
      type="primary"
      :icon="Plus"
      @click="openDialog_create()"
  >
      创建作业
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
              <p>状态: {{ getStatusText(section.status) }}</p>
            </div>
            <el-button
              class="try"
              type="text"
              @click="goto(section.problemId)"
              >
              尝试
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
    title="创建作业"
    v-model="isDialogVisible_create"
    width="60%"
  >
    <div>
    <span class="demonstration">题目</span>
    <el-select v-model="problemId" filterable placeholder="请选择" style="width:100%">
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
      <span class="demonstration">截止时间</span>
      <el-date-picker
          v-model="time"
          type="datetime"
          placeholder="选择日期和时间"
          style="width:100%"
      />
    </div>
    <template #footer>
      <div class="dialog-footer">
      <el-button @click="isDialogVisible_create = false">取消</el-button>
      <el-button type="primary" @click="handleCreate()">保存</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 查看作业情况弹窗 -->
  <el-dialog
    title="查看作业情况"
    v-model="isDialogVisible_view"
    width="80%"
    >
    <el-table :data="filterTableData" stripe="true" style="width: 100%">
      <el-table-column label="学号" prop="studentId" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="状态" prop="status" />
      <el-table-column align="right">
      <template #header>
          <el-input v-model="search" size="small" placeholder="输入搜索内容" />
      </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="isDialogVisible_view = false">关闭</el-button>
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

// 状态文本映射
const statusTextMap: Record<string, string> = {
  'passed': '已完成',
  'failed': '未通过',
  'not tried': '未尝试'
};

const getStatusText = (status: string): string => {
  return statusTextMap[status] || status;
};

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
    console.error('获取作业列表失败:', error)
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

// 前往刷题页面
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
    console.error('获取题目列表失败:', error);
  }
}

// 创建作业
const isDialogVisible_create = ref(false);

const openDialog_create = () => {
  isDialogVisible_create.value = true;
};

const handleCreate = async () => {
  if (!problemId.value || !time.value) { 
    ElMessage.warning('题目和截止时间不能为空！');
    return;
  }

  try {
    const response = await axios.post(
      `http://localhost:8048/class/createAssignment`,
      {
        classId: classId.value,
        problemId: problemId.value,
        dueTime: time.value
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    console.log(response.data);
    ElMessage.success('创建成功！');
    fetchAssignments();
  } catch (error:any) {
    console.error("错误:", error.response || error.message);
    ElMessage.error('创建失败');
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
    console.error('获取学生作业数据失败:', error)
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