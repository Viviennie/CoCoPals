<template>
  <div class="flex" :style="flexStyle">
    <div class="selector">
      <div class="inner-selector">
        <!-- 语言选择器 -->
        <div>
          <label for="language"><el-icon :style="iconStyle"><Place /></el-icon></label>
          <el-select v-model="localSelectedLanguage" placeholder="language" size="small" style="width: 100px" @change="updateCM">
            <el-option
              v-for="item in languageOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>

        <!-- 主题选择器 -->
        <div>
          <label for="theme"><el-icon :style="iconStyle" ><Picture /></el-icon></label>
          <el-select v-model="selectedTheme" placeholder="theme" size="small" style="width: 100px" @change="updateCM">
            <el-option
              v-for="item in themeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>

      <div>
        <el-icon :style="iconStyle"><More /></el-icon>
      </div>
    </div>
    <!-- CodeMirror 编辑器 -->
    <div class="cm">
      <codemirror
        :key="editorKey"
        v-model="localCode"
        placeholder="Code goes here..."
        :style="{ width: props.width, height: props.height }"
        :autofocus="true"
        :indent-with-tab="true"
        :tab-size="2"
        :extensions="extensions"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue';
import { Codemirror } from "vue-codemirror";
import { oneDark } from "@codemirror/theme-one-dark";
import { python } from "@codemirror/lang-python";
import { EditorView } from "@codemirror/view";
import { EditorState } from "@codemirror/state";
// import ReconnectingWebSocket from 'reconnecting-websocket';
// import { Connection } from 'sharedb/lib/client';
// Props 接收从父组件传递过来的数据
const props = defineProps<{ 
  code: string;
  selectedLanguage: string;
  height: string;
  width: string;
}>();

// Emit 用于向父组件发送更新事件
const emit = defineEmits<{
  (e: 'update:code', value: string): void;
  (e: 'update:selectedLanguage', value: string): void;
}>();

// 使用 ref 作为局部变量，以防止直接修改 props
const localCode = ref(props.code);  // 代码
const localSelectedLanguage = ref(props.selectedLanguage);  // 选择的语言
const selectedTheme = ref<string>("oneDark");  // 默认主题
const editorKey = ref(0);  // 用于强制重新渲染编辑器
const editorInstance = ref(null); // 保存编辑器实例

// 语言和主题的选择项
const languageOptions = [
  { value: 'python', label: 'Python' },
  // 你可以在这里加入更多的语言
];

const themeOptions = [
  { value: 'oneDark', label: 'One Dark' },
  { value: 'customLight', label: 'Custom Light' },
];

// 根据主题选择动态改变背景色
const flexStyle = computed(() => {
  return selectedTheme.value === 'oneDark'
    ? { backgroundColor: 'rgba(40, 44, 52, 1)' }  // 半透明黑色
    : selectedTheme.value === 'customLight'
    ? { backgroundColor: 'rgba(255, 255, 255, 1)' }  // 半透明白色
    : {};
});

const iconStyle = computed(() => {
  return selectedTheme.value === 'oneDark'
    ? { color: 'rgba(255,255,255, 1)' }  // 半透明黑色
    : selectedTheme.value === 'customLight'
    ? { color: 'rgba(40, 44, 52, 1)' }  // 半透明白色
    : {};
});

// CodeMirror 扩展配置
let extensions = [python(), oneDark];

const languageExtensions: Record<string, any> = {
  python: python(),
  // 可以添加其他语言扩展
};

const themeExtensions: Record<string, any> = {
  oneDark: oneDark,
  customLight: EditorView.theme({
    "&": {
      color: "#000000",
      backgroundColor: "#ffffff"
    },
    ".cm-content": {
      caretColor: "#000000",
    },
    "&.cm-focused .cm-cursor": {
      borderLeftColor: "#000000",
    },
    ".cm-activeLine": {
      backgroundColor: "#f0f0f0",
    },
    ".cm-gutters": {
      backgroundColor: "#ffffff",
      color: "#999999",
      borderRight: "1px solid #ddd"
    }
  }),
};


// 更新 CodeMirror 的扩展
const updateCM = () => {
  extensions = [
    languageExtensions[localSelectedLanguage.value],
    themeExtensions[selectedTheme.value],
  ];
  editorKey.value++;  // 强制重新渲染
};

// 监听局部变量的变化，并通过 emit 事件将更新传递回父组件
watch(localSelectedLanguage, () => {
  emit('update:selectedLanguage', localSelectedLanguage.value);
  updateCM(); // 当语言发生变化时，更新编辑器
});

watch(localCode, () => {
  emit('update:code', localCode.value);
});

// 在组件挂载后初始化编辑器和 ShareDB
onMounted(() => {
  // initializeShareDB();
});
</script>

<style scoped>
.flex {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 5px;
  flex-direction: column;
  justify-content: center; 
}

.selector {
  display: flex;
  justify-content: space-between;
}

.inner-selector {
  display: flex;
  gap: 1rem;
}

</style>
