<template>
  <div class="wrapper">
    <el-card class="wrapper-2">
      <div v-if="submissionDetail">
        <strong style="font-size:25px;">这是您的第{{ submissionDetail.attemptNum }}次尝试</strong>
        <p style="font-size: 10px;color: #7c7c7c;">提交时间：{{ submissionDetail.submitTime }}，耗时{{ submissionDetail.totalTime }}秒</p>
        <ul>
          <li><strong>编程语言：</strong> <el-text type="primary">{{ submissionDetail.language }}</el-text></li>
          <li><strong>状态：</strong> {{ getStateMessage(submissionDetail.state) }}</li>
          <li><strong>通过用例：</strong> {{ submissionDetail.passCount }}</li>
          <li v-if="submissionDetail.firstFailureOutput">
            <strong>首个失败用例输出：</strong> <pre>{{ submissionDetail.firstFailureOutput }}</pre>
          </li>
          <li><strong>代码：</strong> <pre class="code">{{ submissionDetail.code }}</pre></li>
        </ul>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="try" type="primary" @click="goto()">
            重新尝试
          </el-button>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

// 获取路由中的参数
const route = useRoute();
const problemId = ref<number | null>(null);
const attemptNum = ref<number | null>(null);

const submissionDetail = ref<any>(null);

// 状态映射
const stateMessages: Record<number, string> = {
  1: '正确',
  2: '编译错误',
  3: '答案错误'
};

const getStateMessage = (state: number) => {
  return stateMessages[state] || '未知状态';
};

// 请求并加载提交的详细信息
const fetchSubmissionDetail = async () => {
  if (problemId.value !== null && attemptNum.value !== null) {
    try {
      const response = await axios.get(
        `http://localhost:8048/question/getSubmission?questionId=${problemId.value}&attemptNum=${attemptNum.value}`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      submissionDetail.value = response.data;
    } catch (error) {
      console.error('获取提交详情失败:', error);
      submissionDetail.value = { error: '获取提交详情失败' };
    }
  }
};

// 前往刷题快捷键
const router = useRouter();
const goto =()=>{
  router.push({
      path: '/normalOJ',
      query: { currentProblemId: problemId.value } 
  })
}

// 在路由变化时更新问题ID和尝试编号
watch(() => route.query.problemId, (newProblemId) => {
  problemId.value = Number(newProblemId);
  fetchSubmissionDetail();
});

watch(() => route.query.attemptNum, (newAttemptNum) => {
  attemptNum.value = Number(newAttemptNum);
  fetchSubmissionDetail();
});

onMounted(() => {
  // 初始化从路由获取的参数
  problemId.value = Number(route.query.problemId);
  attemptNum.value = Number(route.query.attemptNum);
  
  if (problemId.value !== null && attemptNum.value !== null) {
    fetchSubmissionDetail();
  }
});
</script>

<style scoped>
.wrapper {
padding: 100px;
display: flex;
justify-content: center;
align-items: center;
}

.wrapper-2 {
padding: 20px;
color: #3d3d3d;
}

.code {
background-color: #3d3d3d;
border-radius: 5px;
padding: 5px;
color: #dadada;
}

.dialog-footer {
display: flex;
justify-content: flex-end; 
padding: 10px; 
}
</style>