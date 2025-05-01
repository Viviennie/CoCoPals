<template>
  <StickyNavbar/>

  <!-- 顶部横幅 -->
  <section class="banner">
    <img src="/home.png" alt="Banner Image" class="banner-image" />
    <div class="banner-text">
      <div>
      <h1 style="font-size: 45px;color:darkblue;">Make Coding a Shared Wonderful Journey</h1>
      <p style="font-size: 30px;color:darkblue;">Collaborative Programming for Better Teaching and Collaborative Experiences</p>
    </div>
      <router-link to="/about" class="learn-more-btn">Learn More  →</router-link> 
    </div>
  </section>

  <div class="features-intro">
    <div class="bar"></div>
    <div>
      <p>A Shortcut of Recommended Features</p>
      <p>for Users to Try</p>
    </div>
  </div>

  <div class="buttons">
    <FeatureButton
      :icon="DataBoard"
      text="Interactive Teaching"
      route="/editlist"
    />
    <FeatureButton
      :icon="Connection"
      text="Online Judge"
      route="/problemlist"
    />
    <FeatureButton
      :icon="Opportunity"
      text="Blog Share"
      route="/blog"
    />
    <FeatureButton
      :icon="Collection"
      text="Class Activity"
      route="/profile"
    />
  </div>
  <div class="buttons">
    <FeatureButton
      :icon="Flag"
      text="Team Competitions"
      route="/problemlist"
    /> 
    <FeatureButton
      :icon="Reading"
      text="Peer Code Review"
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
          <p>4800 Cao'an Street<br />Jiading, Shanghai</p>
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
        <h3>About the Project</h3>
        <p>Embracing the New Era of Education,we are redefining the ecosystem of programming education,breathing vibrant life into diverse teaching and learning experiences.</p>
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
.banner {
  position: relative;
  text-align: center;
}

.banner-image {
  width: 100%;
  height: auto;
}

.banner-text {
  color: #fff;
  position: absolute;
  top: 50%;
  left: 41%;
  transform: translate(-33%, -50%);
  display: flex;
  flex-direction: column;
  gap: 50px;
  align-items: center;
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
  margin: 10px 250px;
  gap:20px;
  font-size: large;
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