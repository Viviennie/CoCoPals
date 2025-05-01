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
          <label for="theme"><el-icon :style="iconStyle"><Picture /></el-icon></label>
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
        <el-button-group class="ml-4">
          <el-button type="primary" :icon="Upload" />
          <el-button type="primary" :icon="Download" />
          <el-button type="primary" :icon="Share" @click="inviteOthers()" />
        </el-button-group>
      </div>
    </div>
    <!-- CodeMirror 编辑器 -->
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
  <!-- 弹窗展示邀请码 -->
  <el-dialog
    title="Your Invitation Code"
    v-model="dialogVisible"
    width="400px"
  >
    <div style="display: flex;justify-content: space-around;align-items: center;">
      <p>{{ inviteCode }}</p>
      <el-button 
        type="primary"
        circle
        @click="copyInviteCode" 
        :disabled="!inviteCode"
      >
      <el-icon><CopyDocument /></el-icon>
      </el-button>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false" type="primary">Close</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { CopyDocument, Share, Upload, Download } from '@element-plus/icons-vue';
import { ref, watch, onMounted, computed } from 'vue';
import { Codemirror } from "vue-codemirror";
import { oneDark } from "@codemirror/theme-one-dark";
import { python } from "@codemirror/lang-python";
import { EditorView } from "@codemirror/view";
import ReconnectingWebSocket from 'reconnecting-websocket';
import * as ShareDB from 'sharedb';
import { Connection, } from 'sharedb/lib/client';
import { ElMessage } from 'element-plus';
import { useRoute } from 'vue-router';
import axios from 'axios';

// Props 接收从父组件传递过来的数据
const props = defineProps<{ 
  code: string;
  selectedLanguage: string;
  height: string;
  width: string;
  documentId: number;
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
const documentId = ref(props.documentId);
const inviteCode = ref<string>(''); // 用于存储邀请码
const dialogVisible = ref(false); // 控制弹窗的显示

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

const route = useRoute();
watch(() => route.query.documentId, (newDocumentId) => {
  documentId.value = Number(newDocumentId);
  fetchDocumentContent();
});

const fetchDocumentContent = async () => {
  if (documentId.value !== null) {
    try {
      console.log("documentId",documentId.value);
      const response = await axios.post(
        `http://localhost:8048/document/connectServiceById?documentId=${documentId.value}`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      console.log("success load code!");
    } catch (error) {
      console.error('Error fetching content:', error);
    }
  }
};

const inviteOthers= async()=> {
  if (documentId.value !== null) {
    try {
      const response = await axios.get(
        `http://localhost:8048/document/generatecode?documentId=${documentId.value}`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      inviteCode.value = response.data;
      console.log("success generate code!");
      dialogVisible.value = true; 
    } catch (error:any) {
      if(error.response){
        ElMessage({
          message: error.response.data,
          type: 'error',
          duration: 3000, 
        })
      }
      console.error('Error fetching inviteCode:', error);
      inviteCode.value = '';
    }
  }
}

// 复制邀请码到剪贴板
const copyInviteCode = () => {
  if (inviteCode.value) {
    // 使用 Clipboard API 替代 execCommand
    navigator.clipboard.writeText(inviteCode.value)
      .then(() => {
        ElMessage({
          message: 'copied!',
          type: 'success',
          duration: 3000, 
        })
      })
      .catch((err) => {
        console.error('failed:', err);
      });
  }
};


onMounted(async () => {
  try {
    await fetchDocumentContent();
    await initializeShareDB();
    // initializePresence();
  } catch (error) {
    console.error('Error during initialization:', error);
  }
});

const initializeShareDB = async()=> {
  
    const socket = new ReconnectingWebSocket(`ws://localhost:4242`,[],{
      maxEnqueuedMessages:0
    });
    // 监听 WebSocket 连接错误
    socket.addEventListener('error', (error: any) => {
      console.error('WebSocket connection error:', error);
      ElMessage({
        message: 'Unable to connect to the server.',
        type: 'error',
        duration: 3000,
      });
    });
    const connection = new Connection(socket);
    if(inviteCode.value==''){
      try {
        const response = await axios.get(
          `http://localhost:8048/document/generatecode?documentId=${documentId.value}`,
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        );
        inviteCode.value = response.data;
      } catch (error:any) {
        ElMessage({
          message: 'something went wrong.',
          type: 'error',
          duration: 3000, 
        })
        console.error('Error fetching inviteCode:', error);
      }
    }
    const docName=inviteCode.value;
    console.log("docName:",docName);
    const doc:ShareDB.Doc = connection.get('shared-doc', docName);

    // 订阅文档的初始内容
    doc.subscribe((error:unknown) => {
      if (error) {
        console.error('文档订阅出错:', error);
        return;
      }

      // 如果文档未创建，初始化内容为空字符串
      if (!doc.type) {
        doc.create({ content: '' }, 'json0');
      } else {
        console.log("!~!!!!",doc.data.content);
        localCode.value = doc.data.content;
      }
    });

    // 当文档内容变化时，将变化应用到 CodeMirror 实例中
    doc.on('op', (op:any) => {
      const content = doc.data.content;
      localCode.value = content;
    });

    // 监听 CodeMirror 编辑器的本地变化并更新 ShareDB 文档
    watch(localCode, (newContent) => {
      try{
        if (doc.data.content !== newContent) {
          doc.submitOp([{ p: ['content'], od: doc.data.content, oi: newContent }]);
        }
      }catch(error:any) {
        console.error(error);
        ElMessage({
          message: 'connect timeout. Please refresh the page.',
          type: 'warning',
          duration: 3000, 
        })
      }
    });
  
};

// // 光标
// let presence: ShareDB.Presence;
// let editor: any; 

// const initializePresence = () => {
//   // 创建一个ReconnectingWebSocket连接
//   const socket = new ReconnectingWebSocket('ws://localhost:4242');
//   const connection = new Connection(socket);

//   const docName = inviteCode.value;
//   const doc: ShareDB.Doc = connection.get('shared-doc', docName);

//   // 创建 presence 实例
//   const presence = new ShareDB.Presence(connection, docName);

//   // 监听 presence 更新并显示其他人的光标
//   presence.on('cursor', (cursorData:any) => {
//     // 处理光标数据，并更新显示其他客户端的光标
//     updateRemoteCursors(cursorData);
//   });

//   // 当连接或断开时，更新 presence
//   socket.onopen = () => {
//     presence.subscribe();
//   };
//   socket.onclose = () => {
//     presence.unsubscribe();
//   };
// };

// const updateRemoteCursors = (cursorData: any) => {
//   // 更新显示其他客户端的光标
//   cursorData.forEach((data: any) => {
//     // 使用某种方法来在页面上显示光标
//     // 这里是一个简单的伪代码，假设有一个 renderCursor 函数来渲染光标
//     renderCursor(data.cursor, data.color);
//   });
// };

// const renderCursor = (cursor: any, color: string) => {
//   const cursorElement = document.createElement('div');
//   cursorElement.style.position = 'absolute';
//   cursorElement.style.left = `${cursor.left}px`;
//   cursorElement.style.top = `${cursor.top}px`;
//   cursorElement.style.width = '2px';
//   cursorElement.style.height = '16px';
//   cursorElement.style.backgroundColor = color;
  
//   const editorContainer = document.querySelector('#editor-container')!;
//   editorContainer.appendChild(cursorElement);

// };

// const handleCursorChange = (editor: any) => {
//   // 获取当前光标位置
//   const cursor = editor.getCursor();

//   // 使用 presence 更新光标位置
//   presence.submitPresence({
//     cursor: cursor,
//     color: 'blue',  // 可以自定义颜色来区分不同用户
//   });
// };

// editor.on('cursorActivity', () => {
//   handleCursorChange(editor);
// });
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
  justify-content: space-between ;
}

.inner-selector {
  display: flex;
  gap: 1rem;
}
</style>
