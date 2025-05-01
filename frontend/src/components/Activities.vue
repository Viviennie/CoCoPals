<template>
  <div class="wrapper-a">
    <div class="title">
      <h2>Practice Record</h2>
    </div>
    <div class="table">
      <el-table :data="filterTableData" :border="parentBorder" height="340" style="width: 100%">
        <el-table-column type="expand">
          <template #default="props">
            <div m="4">
              <el-table :data="props.row.record" :border="childBorder">
                <el-table-column label="No" witdh="50%" prop="attemptNum" />
                <el-table-column label="Date" prop="submitTime" />
                <el-table-column
                  prop="state"
                  label="Result"
                >
                  <template #default="scope">
                    <el-tag
                      :type="scope.row.state === 1 ? 'success' : (scope.row.state === 2 ? 'warning' : 'danger')"
                      disable-transitions
                    >
                      {{ scope.row.state === 1 ? 'Correct' : (scope.row.state === 2 ? 'Compile error' : 'Wrong answer') }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="Language" prop="language" />
                <el-table-column align="right">
                  <template #default="scope">
                    <el-button type="primary" text="true" size="small" @click="handleMore(props.row, props.row.record[scope.$index])">
                      More
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="Status" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.state === 1 ? 'success' : 'danger'"
              disable-transitions
            >
              {{ scope.row.state === 1 ? 'Passed' : 'Failed' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Title" prop="title" />
        <el-table-column prop="difficulty" label="Difficulty" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.difficulty === 'hard' ? 'warning' : (scope.row.difficulty === 'moderate' ? 'primary' : 'success')"
              disable-transitions
            >
              {{ scope.row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column align="right">
          <template #header>
            <el-input v-model="search" size="small" placeholder="Type to search" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

  
  <script lang="ts" setup>
  import { computed, ref, onMounted } from 'vue'
  import axios from 'axios'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  const parentBorder = ref(false)
  const childBorder = ref(false)
  
  interface Submission {
    attemptNum: number
    submitTime: string
    language: string
    state: number
    passCount: number
    totalTime: number
  }
  
  interface Question {
    id: number
    title: string
    difficulty: number
    submitTime: string
    state: number
    record: Submission[]
  }
  
  const search = ref('')
  const tableData = ref<Question[]>([])
  
  const difficultyMap:any = {
    1: 'easy',
    2: 'moderate',
    3: 'hard',
  }
  
  const filterTableData = computed(() =>
    tableData.value.filter(
      (data) => !search.value || data.title.toLowerCase().includes(search.value.toLowerCase())
    )
  )
  
  const handleMore = (row: Question, submission: Submission) => {
    console.log(row.id);
    console.log(submission.attemptNum);
    router.push({
      path: '/submissiondetail',
      query: {
        problemId: row.id,
        attemptNum: submission.attemptNum
      }
    })
  }

  const fetchTableData = async () => {
    try {
      // 请求获取历史做题记录
      const response = await axios.get('http://localhost:8048/question/getAttemptedQuestionList', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`  // 添加 token 到请求头
        }
      });
      const attemptedQuestions = response.data
  
      // 请求每个问题的提交记录，并合并数据
      for (let question of attemptedQuestions) {
        const submissionResponse = await axios.get(
          `http://localhost:8048/question/getSubmissionList?questionId=${question.id}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`  // 添加 token 到请求头
          }
        });
        question.record = submissionResponse.data
      }
  
      // 将获取的数据设置到表格数据
      tableData.value = attemptedQuestions.map((item: any) => ({
        ...item,
        difficulty: difficultyMap[item.difficulty] || 'unknown', // 将难度映射为字符串
      }))
    } catch (error) {
      console.error('Error fetching data:', error)
    }
  }
  
  onMounted(() => {
    fetchTableData()
  })
  </script>
  
  
  <style scoped>
  
  .wrapper-a {
    width: 650px;
    /* height: 430px; */
    padding: 30px;
    background-color: white;
    border-radius: 10px;
    color: #000;
  }

  .title h2 {
    padding-bottom: 15px;
    color: #5d5d5d;
    border-bottom:1px solid #cccccc;
  }

  </style>