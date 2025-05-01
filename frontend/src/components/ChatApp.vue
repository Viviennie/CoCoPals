<template>
  <div class="fixed-chat-container">
    <div class="page-content">
      <div class="card" :style="{width: props.width, height: props.height} ">
        <div class="card-header">
          <h4><strong>Chat</strong></h4>
        </div>
  
        <div class="chat-container" id="chat-content">
          <!-- 消息列表 -->
          <div class="media-chat" v-for="(message, index) in messages" :key="index">
            <img class="avatar" :src="message.avatar" alt="avatar">
            <div class="media-body">
              <p class="message-text">{{ message.text }}</p>
              <span class="meta">{{ message.time }}</span>
            </div>
          </div>
  
          <!-- 回复列表 -->
          <div class="media-chat media-chat-reverse" v-for="(reply, index) in replies" :key="'reply-' + index">
            <div class="media-body">
              <p class="message-text">{{ reply.text }}</p>
              <span class="meta">{{ reply.time }}</span>
            </div>
          </div>
        </div>
  
        <div class="publisher" >
          <img class="avatar" src="https://img.icons8.com/color/36/000000/administrator-male.png" alt="avatar">
          <input class="publisher-input" type="text" placeholder="Write something" v-model="newMessage" @keyup.enter="sendMessage">
          <span class="publisher-btn"><el-icon :size="20"><Link /></el-icon></span>
          <span class="publisher-btn"><el-icon :size="20"><Phone /></el-icon></span>
          <span class="publisher-btn"><el-icon :size="20"><Promotion /></el-icon></span>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script lang="ts" setup>
  import { ref, defineProps } from 'vue';
  import { Link, Phone, Promotion } from '@element-plus/icons-vue';
  
  const props = defineProps<{ 
    height: string;
    width: string;
  }>();

  // 消息数据类型
  interface Message {
    text: string;
    time: string;
    avatar: string;
  }
  
  // 消息和回复列表
  const messages = ref<Message[]>([
    { text: 'Hi', time: '23:58', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
    { text: 'How are you ...???', time: '23:59', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
    { text: 'What are you doing tomorrow?', time: '00:00', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
  ]);
  
  const replies = ref<Message[]>([
    { text: "Hiii, I'm good.", time: '00:06', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
    { text: 'How are you doing?', time: '00:07', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
    { text: 'Long time no see! Free on Sunday.', time: '00:08', avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png' },
  ]);
  
  const newMessage = ref('');
  
  // 发送消息方法
  const sendMessage = () => {
    if (newMessage.value.trim()) {
      replies.value.push({
        text: newMessage.value,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        avatar: 'https://img.icons8.com/color/36/000000/administrator-male.png',
      });
      newMessage.value = '';
    }
  };
  </script>
  
<style scoped>
  .fixed-chat-container {
    border: 10px solid rgba(255,255,255,0.4);
    border-radius: 20px;
  }

  .card {
    border-radius: 10px;
    background-color: rgba(255,255,255,0.2);
    height: 100vh; 
    width: 350px;
  }
  .card-header {
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    height: 8%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: antiquewhite;
  }
  
  .chat-container {
    overflow-y: scroll;
    scrollbar-width: thin;
    scrollbar-color: #c1c1c1 #f1f1f1;
    height: 77%;
    padding: 11px;
  }

  .media-chat {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 10px;
  }
  .media-chat-reverse {
    flex-direction: row-reverse;
    margin-right: 10px;
  }
  .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: #f5f6f7;
  }
  .media-body {
    max-width: 70%;
    display: flex;
    flex-direction: column;
  }
  .message-text {
    padding: 8px 12px;
    margin: 4px 0;
    background-color: #ffffff;
    border-radius: 6px;
    color: #696969;
    max-width: 200px;
    word-wrap: break-word;
  }
  .media-chat-reverse .message-text {
    background-color: #3f3f3f;
    color: #c4c4c4;
  }
  .meta {
    font-size: 12px;
    color: #9a9a9a;
    margin-top: 4px;
  }
  .publisher {
    display: flex;
    align-items: center;
    padding: 0 10px;
    height: 12%;
    border-bottom-left-radius: 10px;
    border-bottom-right-radius: 10px;
    background-color: aliceblue;
  }
  .publisher-input {
    flex-grow: 1;
    border: none;
    outline: none;
    background-color: transparent;
  }
  .publisher-btn {
    font-size: 18px;
    cursor: pointer;
    margin-left: 8px;
  }
  </style>
  