<template>
  <div class="container">
    <div class="btn">
      <el-button @click="runCode" type="primary">Run</el-button>
    </div>
    <el-tabs class="demo-tabs" :style="{ width: props.width, height: props.height}">
      <!-- 参数输入 -->
      <el-tab-pane label="input">
        <el-input
          id="parameters"
          type="textarea"
          v-model="parameters"
          placeholder="parameters..."
          style="margin-top: 10px; width: 100%; height: 100%;"
        />
      </el-tab-pane>

      <!-- 显示结果 -->
      <el-tab-pane label="output">
        <div class="result-container">
          <pre style="color: 616161;">{{ result }}</pre>
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

// 前端运行方式：
// 可以在input框里用空格分割参数输入，然后在代码里用arg[0]这种去依次访问。
// 不能用input()会导致浏览器弹窗，当然可以在prompt里输入，就不用在代码写arg
// sys方式我用不了，读取不到（但是后端是sys）

// 运行代码
const runCode = async () => {
  try {
    console.log("用户输入的代码:",props.code);
    console.log("用户输入的参数:", parameters.value); // 打印用户输入的参数
    if(props.selectedLanguage === "python") {
      const response = await axios.post(`http://localhost:8048/question/runWithInput`, null, {
        params: {
          code: props.code,
          input: parameters.value
        }
      });
      console.log("Success", response);
      result.value = response.data;
    } else {
      result.value = `The language ${props.selectedLanguage} is not supported by now`;
    }
  } catch (err) {
    result.value = `There was an error running: ${err}`;
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

.btn {
  display: flex;
  margin: 10px;
}

.result-container {
  height: 120px;
  overflow: scroll;
}
</style>
