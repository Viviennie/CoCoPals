<template>
  <div>
    <!-- 悬浮按钮 -->
    <button @click="toggleChat" class="chat-button" title="AI小助手">
      💬
    </button>

    <!-- 浮动对话框 -->
    <div v-if="chatVisible" class="chat-window">
      <div class="chat-header">
        AI 助手
        <button class="chat-close" @click="toggleChat">✖</button>
      </div>

      <div class="chat-body">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['chat-message', msg.sender === 'user' ? 'chat-user' : 'chat-ai']"
        >
          <div class="chat-bubble">{{ msg.text }}</div>
        </div>

        <!-- 显示加载动画 -->
        <div v-if="loading" class="loading-spinner"></div>
      </div>

      <form @submit.prevent="sendMessage" class="chat-input-bar">
        <input
          v-model="input"
          type="text"
          class="chat-input"
          placeholder="输入内容..."
          :disabled="chatLoading"
        />
        
        <!-- 语音识别按钮 -->
        <ElButton
          class="button"
          @click="toggleRecognition">
          <ElIcon v-if="!isRecognizing" :style="{ fontSize: '24px' }">
            <Microphone />
          </ElIcon>
          <ElIcon v-if="isRecognizing" :style="{ fontSize: '24px' }">
            <School />
          </ElIcon>
        </ElButton>
        
        <button type="submit" class="chat-send" :disabled="loading">
          发送
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElButton, ElMessage, ElIcon } from 'element-plus'
import { Microphone, School } from '@element-plus/icons-vue'

const props = defineProps({
  question: String,
  code: String,
  describe: String
})

const chatVisible = ref(false)
const input = ref('')
const messages = ref([])
const loading = ref(false)  // 新增：加载状态
const chatLoading = ref(false)
const isRecognizing = ref(false)
let recognition = null

const toggleChat = () => {
  chatVisible.value = !chatVisible.value
}

const sendMessage = async () => {
  const text = input.value.trim()
  if (!text) return

  messages.value.push({ sender: 'user', text })
  input.value = ''

  // 设置加载状态
  loading.value = true

  const chatHistory = [
    {
      role: 'system',
      content: '你是一个热心的 AI 助手，会根据用户提供的 OJ 题目和代码，给出针对性的改进建议。' +
                '请注意，你的建议应当清晰、简洁，帮助用户理解代码的优化点或者可能的错误。' +
                '避免使用 Markdown 或其他格式化语法，回复仅以纯文本形式输出。用户会提供以下信息：1. 题目:{' +
                props.question +
                '}2. 描述：{' +
                props.describe +
                '}3. 代码：{' +
                props.code +
                '}你需要结合这三个信息给出建议。'
    },
    ...messages.value.map(msg => ({
      role: msg.sender === 'user' ? 'user' : 'assistant',
      content: msg.text
    }))
  ]

  try {
    const res = await fetch('https://api.deepseek.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer sk-33f889faebd74151876508a5b2af09a7',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        model: 'deepseek-chat',
        messages: chatHistory
      })
    })

    const data = await res.json()
    const reply = data.choices?.[0]?.message?.content || '（AI 无回复）'
    messages.value.push({ sender: 'ai', text: reply })
  } catch (err) {
    console.error('发送失败', err)
    messages.value.push({ sender: 'ai', text: '发生错误，请稍后重试。' })
  } finally {
    // 请求结束，关闭加载状态
    loading.value = false
  }
}

// 语音识别功能
const toggleRecognition = () => {
  if (isRecognizing.value) {
    recognition.stop()
    isRecognizing.value = false
    chatLoading.value = false
  } else {
    if (!recognition) {
      if (!('webkitSpeechRecognition' in window)) {
        ElMessage.error('您的浏览器不支持语音识别。请使用支持 Web Speech API 的浏览器。')
        return
      }
      
      recognition = new webkitSpeechRecognition()
      recognition.lang = 'zh-CN'
      recognition.continuous = true
      recognition.interimResults = true
      recognition.maxAlternatives = 1
      chatLoading.value=true
      recognition.onstart = ()=>{
         chatLoading.value=false
      }

      recognition.onresult = (event) => {
        const lastResult = event.results[event.results.length - 1]
        input.value = lastResult[0].transcript
      }

      recognition.onend = () => {
        isRecognizing.value = false
        chatLoading.value = false
      }

      recognition.onerror = (event) => {
        ElMessage.error(`语音识别错误: ${event.error}`)
        isRecognizing.value = false
        chatLoading.value = false
      }
    }
    
    input.value = '' // 清空之前的识别结果
    isRecognizing.value = true
    chatLoading.value = true
    recognition.start()
  }
}
</script>

<style scoped>
.chat-button {
  position: fixed;
  bottom: 40px;
  left: 40px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #258feb;
  color: white;
  font-size: 24px;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 9999;
}

.chat-window {
  position: fixed;
  bottom: 100px;
  left: 40px;
  width: 600px;
  height: 600px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid #ccc;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  z-index: 9998;
  overflow: hidden;
}

.chat-header {
  background-image: url('/AIAssistantBackground.png');
  background-size: cover;
  background-position: center;
  background-color: white;
  color: white;
  padding: 10px 12px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-close {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-message {
  display: flex;
  max-width: 80%;
}

.chat-user {
  align-self: flex-end;
  justify-content: flex-end;
}

.chat-ai {
  align-self: flex-start;
}

.chat-ai .chat-bubble {
  background-color: #cfefa5;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
  max-width: 500px;
  word-wrap: break-word;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-user .chat-bubble {
  background-color: #dbeafe;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
  max-width: 500px;
  word-wrap: break-word;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-input-bar {
  display: flex;
  border-top: 1px solid #ddd;
  padding: 8px;
}

.chat-input {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  margin-right: 8px;
  
}

.chat-send {
  background-image: url('/1.png');
  background-size: cover;
  background-position: center;
  background-color: white;
  margin-left: 8px;
  background-color: #258feb;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

/* 加载动画样式 */
.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #258feb;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 2s linear infinite;
  align-self: center;
}

.button{
    background-image: url('/1.png');
  background-size: cover;
  background-position: center;
  background-color: white;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
