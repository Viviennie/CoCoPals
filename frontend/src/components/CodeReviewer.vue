<template>
  <div>
    <!-- 运行测试按钮 -->
    <div class="btn">
      <el-button @click="runTests" type="plain">提交</el-button>
    </div>
    
    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" title="运行结果" width="600">
      <div v-if="submissionDetail">
        <strong>这是您的第{{ submissionDetail.attemptNum }}次尝试：</strong>
        <ul>
          <li>提交于 {{ submissionDetail.submitTime }}，耗时 {{ submissionDetail.totalTime }} 秒</li>
          <li><strong>编程语言：</strong> {{ submissionDetail.language }}</li>
          <li><strong>运行状态：</strong> {{ getStateMessage(submissionDetail.state) }}</li>
          <li><strong>通过用例数：</strong> {{ submissionDetail.passCount }}</li>
          <li v-if="submissionDetail.firstFailureOutput">
            <strong>首个失败用例输出：</strong> {{ submissionDetail.firstFailureOutput }}
          </li>
        </ul>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" type="primary">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps } from 'vue';
import axios from 'axios';

// 接收从父组件传递的 props
const props = defineProps<{
  code: string;
  selectedLanguage: string;
  problemId: number;  // 接收题号
}>();

// 本地状态
const dialogVisible = ref(false);  // 控制弹窗的显示
const submissionDetail = ref<any>(null);  // 存储提交的详细信息

// 状态映射
const stateMessages:any = {
  1: '正确',
  2: '编译错误',
  3: '答案错误'
};

const getStateMessage = (state: number) => {
  return stateMessages[state] || '未知状态';
};

// 运行用户代码并与测试用例进行比较
const runTests = async () => {
  submissionDetail.value = null;  // 重置提交详情

  try {
    console.log(props.problemId);
    
    // 将用户代码和测试用例发送到后端
    const response = await axios.post(`http://localhost:8048/question/run?questionId=${props.problemId}&code=${encodeURIComponent(props.code)}`, {}, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    // 后端返回的结果
    const result = response.data;
    console.log('成功', result);
    
    // 将后端返回的提交详情更新到前端
    submissionDetail.value = result;

    // 显示弹窗
    dialogVisible.value = true;

  } catch (error: any) {
    submissionDetail.value = { 
      error: `错误：${error.message}` 
    };
    dialogVisible.value = true;
  }
};
</script>

<style scoped>
.btn {
  display: flex;
  justify-content: flex-end;
  margin: 10px;
}

.dialog-footer {
  text-align: right;
}
</style>