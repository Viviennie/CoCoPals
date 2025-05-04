<template>
  <StickyNavbar/>

  <!-- 顶部横幅 -->
  <section class="banner">
    <img src="/home.png" alt="CoCoPals编程乐园" class="banner-image" />
    <div class="banner-text">
      <div>
      <h1 style="font-size: 45px;color:darkblue;">开启编程奇妙之旅</h1>
      <p style="font-size: 30px;color:darkblue;">协作编程，让学习更有趣</p>
    </div>
      <router-link to="/about" class="learn-more-btn">了解更多 →</router-link> 
    </div>
  </section>

  <div class="features-intro">
    <div class="bar"></div>
    <div>
      <p>推荐功能快速体验</p>
      <p>点击立即尝试</p>
    </div>
  </div>

  <div class="buttons">
    <FeatureButton
      :icon="DataBoard"
      text="互动课堂"
      route="/editlist"
    />
    <FeatureButton
      :icon="Connection"
      text="编程挑战"
      route="/problemlist"
    />
    <FeatureButton
      :icon="Opportunity"
      text="学习笔记"
      route="/blog"
    />
    <FeatureButton
      :icon="Collection"
      text="班级活动"
      route="/profile"
    />
  </div>
  <div class="buttons">
    <FeatureButton
      :icon="Flag"
      text="团队竞赛"
      route="/problemlist"
    /> 
    <FeatureButton
      :icon="Reading"
      text="代码互评"
      route="/"
    />
  </div>

  <!-- 底部联系方式和项目简介 -->
  <footer class="footer">
    <div class="footer-content">
      <div class="contact-info">
        <div class="contact-item">
          <div class="icon-circle">
            <el-icon><Location /></el-icon>
          </div>
          <p>上海市嘉定区<br />曹安公路4800号</p>
        </div>
        <div class="contact-item">
          <div class="icon-circle">
            <el-icon><Phone /></el-icon>
          </div>
          <p>10086</p>
        </div>
        <div class="contact-item">
          <div class="icon-circle">
            <el-icon><Message /></el-icon>
          </div>
          <p>123456@qq.com</p>
        </div>
      </div>
      <div class="project-info">
        <h3>关于CoCoPals</h3>
        <p>我们致力于打造全新的编程教育生态，为师生带来生动有趣的教学体验，让编程学习变得更加轻松愉快。</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import StickyNavbar from '../components/Navbar.vue';
import FeatureButton from '../components/FeatureButton.vue';
import { Location, Phone, Message, Flag, Reading, Connection, Collection, DataBoard, Opportunity } from '@element-plus/icons-vue';

</script>

<style scoped>

/* 顶部横幅样式 */
/* 顶部横幅样式 */
.banner {
  position: relative;
  text-align: center;
}

.banner-image {
  width: 100%;
  height: auto;
  display: block; /* 确保图片没有底部间隙 */
}

.banner-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%; /* 添加宽度限制 */
  max-width: 1200px; /* 根据实际需要调整 */
  padding: 0 20px; /* 添加内边距 */
  box-sizing: border-box;
  text-align: center; /* 文字居中 */
}

.banner-text > div {
  /*display: flex;*/
  flex-direction: column;
  align-items: center; /* 子元素居中 */
  /*gap: 20px;  调整标题和副标题间距 */
  margin-bottom: 100px; /* 增加这个属性 */
}

.learn-more-btn {
  border-radius: 5px; 
  font-size: large;
  padding: 18px 45px;
  text-decoration: none; /* 去掉下划线 */
  border: none;
  cursor: pointer;
  color: white;
  background:linear-gradient(45deg, #9BBCC3, #CBA5D1);
  box-shadow: 0 6px 10px #2a2a2a;

  transition: transform 0.3s ease;
}


.learn-more-btn:hover {
  transform: translateY(-10%);
}

.bar {
  background: linear-gradient(45deg, #9BBCC3, #CBA5D1);
  width: 5px;
  height: 60px;
}

.buttons {
  display: flex;
  gap:70px;
  margin-left: 250px;
  margin-top: 50px;
  margin-bottom: 50px;
}

.features-intro {
  display: flex;
  justify-content: start;
  align-items: center;
  margin: 30px 250px 10px; /* 修改为：上50px 左右250px 下10px */
  gap: 20px;
  font-size: large;
}

.features-intro div p {
  margin-top: 5px;
  margin-bottom: 5px; /* 移除默认的外边距 */
}

/* 底部样式 */
.footer {
  padding: 50px 150px;
  background-color: #2b2b2b;
  color: #e8e8e8;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 60%;
}

.project-info {
  display: flex;
  flex-direction: column;
  justify-content: start;
  gap: 10px;
  width: 40%;
}

.project-info p {
  color: #a0a0a0;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-circle {
  display: flex;
  width: 40px;
  height: 40px;
  border-radius: 50%; /* 圆形背景 */
  align-items: center;
  justify-content: center;
  margin-right: 10px; /* 图标和文字间距 */
  background-color: #565656; /* 圆形背景色 */
}

</style>