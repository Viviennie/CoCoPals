<template>
  <StickyNavbar />
  <div class="wrapper-pl">
    <div class="wrapper-pl2">
    <div class="title">
      <h1>题目列表</h1>
    </div>
    <div class="table">
      <el-table :data="filterTableData" stripe="true" style="width: 100%">
        <el-table-column
          prop="status"
          label="状态"
          :filters="[
              { text: '已通过', value: 'passed' },
              { text: '未通过', value: 'failed' },
              { text: '未尝试', value: 'no attempt' },
          ]"
          :filter-method="filterStatus"
          filter-placement="bottom-end"
          >
            <template #default="scope">
              <el-tag
              :type="scope.row.status === 'passed' ? 'success' : (scope.row.status === 'failed' ? 'danger' : 'info')"
              disable-transitions
              >{{ 
                scope.row.status === 'passed' ? '已通过' : 
                (scope.row.status === 'failed' ? '未通过' : '未尝试') 
              }}</el-tag
              >
            </template>
        </el-table-column>
        <el-table-column label="ID" prop="id" />
        <el-table-column label="标题" prop="title" />
        <el-table-column
          prop="tag"
          label="Tag"
          :filters="[
              { text: '困难', value: 'hard' },
              { text: '中等', value: 'moderate' },
              { text: '简单', value: 'easy' },
          ]"
          :filter-method="filterTag"
          filter-placement="bottom-end"
          >
            <template #default="scope">
              <el-tag
              :type="scope.row.tag === 'hard' ? 'warning' : (scope.row.tag === 'moderate' ? 'primary' : 'success')"
              disable-transitions
              >{{ 
                scope.row.tag === 'hard' ? '困难' : 
                (scope.row.tag === 'moderate' ? '中等' : '简单') 
              }}</el-tag
              >
            </template>
          </el-table-column>

        <el-table-column align="right">
          <template #header>
            <el-input v-model="search" size="small" placeholder="输入搜索内容" />
          </template>
          <template #default="scope">
            <el-button type="primary" text="true" size="small" @click="handleTry(scope.$index, scope.row)">
              开始尝试
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import StickyNavbar from '../components/Navbar.vue'
import axios from 'axios'
const router = useRouter()

interface Problem {
  status: string
  id: number
  title: string
  tag: string
}

const tableData = ref<Problem[]>([]) 
const search = ref('')

const filterTableData = computed(() =>
  tableData.value.filter(
    (data) =>
      !search.value ||
      data.title.toLowerCase().includes(search.value.toLowerCase()) 
  )
)

const filterStatus = (value: string, row: Problem) => {
  return row.status === value
}

const filterTag = (value: string, row: Problem) => {
  return row.tag === value
}

const handleTry = (index: number, row: Problem) => {
  router.push({
    path: '/normalOJ',
    query: { currentProblemId: row.id } // 使用 query 传参
  });
}

const difficultyMap:any = {
  1: 'easy',
  2: 'moderate',
  3: 'hard',
}

// 请求数据并更新 tableData
const fetchTableData = async () => {
try {
  const response = await axios.get('http://localhost:8048/question/getListByUser', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`  // 添加 token 到请求头
      }
    });
  console.log('API 返回数据:', response.data)
  if (Array.isArray(response.data)) {
    // 处理难度的转换
    tableData.value = response.data.map((item: any) => ({
      ...item,
      tag: difficultyMap[item.difficulty] || 'unknown' // 将整数难度转换为字符串
    }))
  } else {
    console.error('API 返回了非数组数据:', response.data)
  }
} catch (error) {
  console.error('获取数据失败:', error)
}
}

// 在组件挂载后获取数据
onMounted(() => {
  fetchTableData()
})
</script>

<style scoped>
.wrapper-pl {
  display: flex;
  flex-direction: column;
}

.wrapper-pl2 {
  margin: 12vh 12vw 12vh 12vw;
}

.title {
  display: flex;
  align-items: center;
  justify-content:space-between;
}

.table {
  padding: 10px;
  background-color: white;
  border-radius: 10px;
}
</style>