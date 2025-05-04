<template>
  <StickyNavbar />
 
 <!-- 左侧操作栏 -->
 <div class="action-bar">
     <el-button 
       circle 
       class="action-button"
       :class="{ 'is-liked': isLiked }"
       @click="toggleLike"
     >
       <el-icon><Star /></el-icon>
       <span class="action-count">{{ post.likeCount }}</span>
     </el-button>
     <el-button circle class="action-button">
       <el-icon><Share /></el-icon>
     </el-button>
 </div>
 
 <div class="container">
    <el-card class="blog-detail">
      <!-- 中间文章内容 -->
      <div class="content-container">
      <div class="article-header">
          <h1 class="article-title">{{ post.title }}</h1>
          <div class="article-meta">
          <span class="author">作者：{{ post.author }}</span>
          <span class="date">{{ formatDate(post.publishTime) }}</span>
          <span class="views"><el-icon><View /></el-icon> {{ post.viewCount }}</span>
          </div>
          <div class="article-tags">
          <el-tag 
              v-for="tag in post.tags" 
              :key="tag" 
              size="small"
              class="article-tag"
          >
              {{ tag }}
          </el-tag>
          </div>
      </div>
      <div class="article-content" v-html="renderedContent"></div>
      </div>
  
      <!-- 下侧信息栏 -->
      <div class="author-card">
      <div>
          <img :src="authorInfo.avatar" class="author-avatar" />
      </div>
      <div>
          <div class="author-info">
          <div class="author-name">{{ authorInfo.username }}</div>
          <div class="stat-item">
              文章：{{ authorInfo.postCount }}
          </div>
          </div>
          <el-button type="text">更多 ></el-button>
      </div>
      </div>
    </el-card>

    <div style="color: gray;">共1条回答...</div>

    <el-card class="remark-detail">
      <!-- 评论内容 -->
      <div class="content-container">
          <div class="remarker-info">
              <img :src="remark.avatar" class="author-avatar" />
              <div>
              <span class="author-name">{{ remark.author }}</span>
              <span class="date">{{ formatDate(remark.publishTime) }}</span>
              </div>
          </div>
          <div class="article-content" v-html="renderedRemark"></div>
      </div>
    </el-card>

    <el-button class="answer" type="plain">写下我的想法</el-button>
  </div>
</template>
 
 <script setup lang="ts">
 import { ref, onMounted, computed } from 'vue';
 import StickyNavbar from '../components/Navbar.vue';
 import { useRoute, useRouter } from 'vue-router';
 import MarkdownIt from 'markdown-it';
 
 const md = new MarkdownIt();
 const route = useRoute();
 const router = useRouter();
 
 // 模拟文章数据
 const post = ref({
   id: 1,
   title: '大三学生如何平衡学业与实习',
   content: `
    大三学生如何平衡学业与实习？分享你的实习规划经验，包括简历准备、面试技巧、时间管理等方面...
    ![Composition API示意图](/study.png)
   `,
   author: '史密斯',
   publishTime: '2025-01-15T10:00:00Z',
   viewCount: 2341,
   likeCount: 180,
   commentCount: 1,
   tags: ['Vue3', '组合式API', '前端开发']
 });
 
 // 作者信息
 const authorInfo = ref({
   username: 'Rosaria',
   avatar: '/avatar1.png',
   postCount: 3,
 });
 
 // 渲染Markdown内容
 const renderedContent = computed(() => {
   return md.render(post.value.content);
 });
 
 // 生成目录
 const toc = computed(() => {
   return '<div>1. Composition API 简介</div><div>2. 核心概念</div>';
 });
 
 // 格式化日期
 const formatDate = (dateString: string) => {
   return new Date(dateString).toLocaleDateString('en-US', {
     year: 'numeric',
     month: 'long',
     day: 'numeric'
   });
 };
 
 const isLiked = ref(false);
 
 // 模拟评论数据
 const renderedRemark = computed(() => {
   return md.render(remark.value.content);
 });
 const remark = ref({
   content: `
### 2.1 setup 函数  🎯

setup 函数是 Composition API 的入口。它在组件创建之前执行，一旦 props 被解析就会调用该函数。

\`\`\`js
export default {
  setup(props, context) {
    // 组件逻辑写在这里
  }
}
\`\`\`
   `,
   author: 'Alice',
   avatar: '/avatar.png',
   publishTime: '2025-01-16T10:00:00Z',
 });
 
 // 点赞功能
 const toggleLike = () => {
   if (isLiked.value) {
     post.value.likeCount--;
   } else {
     post.value.likeCount++;
   }
   isLiked.value = !isLiked.value;
 };
 
 onMounted(() => {
   // 这里可以根据路由参数获取文章详情
   const postId = route.params.id;
   // fetchPostDetail(postId);
 });
 </script>
 
 <style scoped>
 .action-bar {
   left: 10px;
   position: fixed;
   top: 30vh;
   display: flex;
   flex-direction: column;
   gap: 20px;
   align-items: center;
 }
 
 
 .action-button {
   border: none;
   width: 80px;
   height: 80px;
   position: relative;
 }
 .action-button:hover{
   background-color: #d2e8ff;
 }
 .action-button .el-icon{
   width: 100%;
   height: auto;
   color: #494949;
 }
 .action-count {
   position: absolute;
   left: 100%;
   top: 50%;
   transform: translateY(-50%);
   font-size: 1.5em;
   color: rgb(202, 202, 202);
   margin-left: 8px;
 }
 
 .action-button.is-liked {
   background-color: rgb(255, 207, 50);
 }
 
 .action-button.is-liked .el-icon {
   color: #ffffff;
 }
 
 .container {
   margin: 100px 200px;
   display: flex;
   flex-direction: column;    
   gap: 20px;
 }
 
 .content-container {
   padding: 30px;
 }
 
 .article-header {
   margin-bottom: 30px;
 }
 
 .article-title {
   font-size: 2.0em;
   margin-bottom: 20px;
 }
 
 .article-meta {
   display: flex;
   gap: 20px;
   color: grey;
   margin-bottom: 15px;
 }
 .views .el-icon{
   color:#494949;
 }
 .article-tags {
   display: flex;
   gap: 10px;
   margin-top: 15px;
 }
 
 .article-content {
   line-height: 1.8;
   font-size: 16px;
 }
 
 /* 添加图片样式 */
 .article-content :deep(img) {
   max-width: 100%;
   height: auto;
   display: block;
   margin: 20px auto;
 }
 
 .author-card {
   display: flex;
   justify-content: end;
   align-items: center;
   padding-top: 20px;
   gap: 10px;
   border-top: 1px dashed #d0d0d0;
 }
 
 .author-avatar {
   width: 50px;
   height: 50px;
   border-radius: 10%;
 }
 
 .author-info {
   display: flex;
   align-items: end;
   gap: 5px;
 }
 
 .author-name {
   font-size: 18px;
 }
 
 .stat-item {
   font-size: 14px;
   color: #666;
 }
 
 /* 评论样式 */ 
 .remarker-info {
    display: flex;
    gap: 10px;
 }

 .remarker-info div {
    display: flex;
    flex-direction: column;
 }

 .remarker-info .date {
    font-size: small;
    color: #5d5d5d;
 }
 </style>