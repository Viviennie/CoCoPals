<template>
    <StickyNavbar />
    <div class="wrapper-pl">
      <div class="wrapper-pl2">
      <div class="title">
        <h1>Problem List</h1>
      </div>
      <div class="table">
        <el-table :data="filterTableData" stripe="true" style="width: 100%">
          <el-table-column
            prop="status"
            label="Status"
            :filters="[
                { text: 'passed', value: 'passed' },
                { text: 'failed', value: 'failed' },
                { text: 'no attempt', value: 'no attempt' },
            ]"
            :filter-method="filterStatus"
            filter-placement="bottom-end"
            >
              <template #default="scope">
                <el-tag
                :type="scope.row.status === 'passed' ? 'success' : (scope.row.status === 'failed' ? 'danger' : 'info')"
                disable-transitions
                >{{ scope.row.status }}</el-tag
                >
              </template>
          </el-table-column>
          <el-table-column label="Id" prop="id" />
          <el-table-column label="Title" prop="title" />
          <el-table-column
            prop="tag"
            label="Tag"
            :filters="[
                { text: 'hard', value: 'hard' },
                { text: 'moderate', value: 'moderate' },
                { text: 'easy', value: 'easy' },
            ]"
            :filter-method="filterTag"
            filter-placement="bottom-end"
            >
              <template #default="scope">
                <el-tag
                :type="scope.row.tag === 'hard' ? 'warning' : (scope.row.tag === 'moderate' ? 'primary' : 'success')"
                disable-transitions
                >{{ scope.row.tag }}</el-tag
                >
              </template>
            </el-table-column>

          <el-table-column align="right">
            <template #header>
              <el-input v-model="search" size="small" placeholder="Type to search" />
            </template>
            <template #default="scope">
              <el-button type="primary" text="true" size="small" @click="handleTry(scope.$index, scope.row)">
                Try
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
    console.log('API Response:', response.data)
    if (Array.isArray(response.data)) {
      // 处理难度的转换
      tableData.value = response.data.map((item: any) => ({
        ...item,
        tag: difficultyMap[item.difficulty] || 'unknown' // 将整数难度转换为字符串
      }))
    } else {
      console.error('API returned non-array data:', response.data)
    }
  } catch (error) {
    console.error('Error fetching data:', error)
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
  