<template>
    <div class="fixed-chat-container">
      <div class="page-content">
        <div class="card" >
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
        classId: number;
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
        background-color: #f5f5f5;
    }
  
    .card {

    }

    .card-header {
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #f5f5f5;
      border-bottom: 1px solid #e3e3e3;
    }
    
    .chat-container {
      overflow-y: scroll;
      scrollbar-width: thin;
      scrollbar-color: #c1c1c1 #f1f1f1;
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
      display: flex;
      flex-direction: column;
    }
    .message-text {
      padding: 8px 12px;
      margin: 4px 0;
      background-color: #ffffff;
      border-radius: 6px;
      word-wrap: break-word;
    }
    .media-chat-reverse .message-text {
      background-color: #409EFF;
      color: #ffffff;
    }
    .meta {
      font-size: 12px;
      color: #9a9a9a;
      margin-top: 4px;
    }
    .publisher {
      display: flex;
      align-items: center;
      padding:  10px;
      border-bottom-left-radius: 10px;
      border-bottom-right-radius: 10px;
      background-color: #e6e6e6;
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
    