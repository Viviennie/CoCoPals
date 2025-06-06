<template>
  <div class="container">
    <div class="header">
      <el-button
          @click="runCode"
          type="primary"
          :disabled="!canCollaborate"
          class="run-button"
      >
        运行
      </el-button>
    </div>
    <el-tabs class="demo-tabs" :style="{ width: props.width, height: props.height}">
      <!-- 参数输入 -->
      <el-tab-pane label="输入">
        <el-input
          id="parameters"
          type="textarea"
          v-model="parameters"
          placeholder="输入参数..."
          style="margin-top: 10px; width: 100%; height: 100%;"
          :disabled="!props.canCollaborate"
        />
      </el-tab-pane>

      <!-- 显示结果 -->
      <el-tab-pane label="输出">
        <div class="result-container">
          <pre style="color: #616161;">{{ result }}</pre>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps } from 'vue';
import axios from "axios";

// 接收从父组件传递的 props
const props = defineProps<{
  code: string;
  selectedLanguage: string;
  height:string;
  width:string;
  canCollaborate: boolean;
}>();

// 本地状态
const parameters = ref<string>("");
const result = ref<string>("");

// Pyodide 实例
let pyodide: any = null;

// 加载 Pyodide
onMounted(async () => {
  const pyodideScript = document.createElement("script");
  pyodideScript.src = "https://cdn.jsdelivr.net/pyodide/v0.23.0/full/pyodide.js";
  pyodideScript.onload = async () => {
    pyodide = await (window as any).loadPyodide();
    console.log("Pyodide 已加载");
  };
  document.body.appendChild(pyodideScript);
});

// 运行代码
const runCode = async () => {
  try {
    console.log("用户输入的代码:",props.code);
    console.log("用户输入的参数:", parameters.value); // 打印用户输入的参数
    if(props.selectedLanguage === "python") {
      // 使用FormData或JSON格式发送请求体数据，避免URL长度限制
      const response = await axios.post(`http://localhost:8048/question/runWithInput`, {
        code: props.code,
        input: parameters.value
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      console.log("成功", response);
      result.value = response.data;
    } else {
      result.value = `当前暂不支持 ${props.selectedLanguage} 语言`;
    }
  } catch (err) {
    result.value = `运行出错: ${err}`;
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  color: #000;
}

.demo-tabs {
  background-color: white;
  padding: 32px;
  color: #6b778c;
  font-size: 12px;
  font-weight: 600;
  border-radius: 5px;
}

.header {
  display: flex;
  justify-content: flex-start; /* 按钮靠左对齐 */
  width: 100%;
  margin-bottom: 10px;
}

.run-button {
  margin-left: 0; /* 确保按钮紧贴左侧 */
}
.result-container {
  height: 120px;
  overflow: scroll;
}
</style>