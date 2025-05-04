<template>
  <StickyNavbar/>

  <div class="intro-container">
    <div class="intro-title">
      <div class="card" @mousemove="handleMouseMove" @mouseleave="resetCard">
        <img src="/poster.png" alt="CoCoPals宣传图" class="card-image" />
      </div>
      <div class="intro-text">
        欢迎来到CoCoPals编程乐园！<br><br>
        在这里，你可以轻松愉快地体验编程课堂。接下来我们将简单介绍网站功能，带你快速上手。
      </div>
    </div>
  </div>

  <div class="item-container">
    <div class="intro-item">
      <div class="pre">
        <video width="600" height="400" controls>
          <source src="/freecoding.mp4" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div class="pre-text">
        <div class="subtitle">
          协同编程
        </div>
        <div class="text">
          在这里，你可以邀请小伙伴一起写代码，实时看到彼此的修改，遇到问题可以互相帮助，让编程学习更有趣！
        </div>
      </div>
    </div>
  </div>

  <div class="item-container">
    <div class="intro-item">
      <div class="pre">
        <video width="600" height="400" controls>
          <source src="/judge.mp4" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div class="pre-text">
        <div class="subtitle">
          编程挑战
        </div>
        <div class="text">
          这里有丰富的编程题目等你来挑战！系统会自动记录错题，通过反复练习，你的编程能力会越来越强哦~
        </div>
      </div>
    </div>
  </div>

  <div class="item-container">
    <div class="intro-item">
      <div class="pre">
        <video width="600" height="400" controls>
          <source src="/blog.mp4" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div class="pre-text">
        <div class="subtitle">
          学习笔记
        </div>
        <div class="text">
          你可以在这里分享编程学习心得，也可以提出遇到的问题，和小伙伴们一起讨论解决方案，共同进步！
        </div>
      </div>
    </div>
  </div>

  <div class="item-container">
    <div class="intro-item">
      <div class="pre">
        <video width="600" height="400" controls>
          <source src="/me.mp4" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div class="pre-text">
        <div class="subtitle">
          我的空间
        </div>
        <div class="text">
          这里是你的专属空间！
          <br>
          可以查看编程挑战记录，定期复习能让你的编程基础更扎实。
          <br>
          老师可以在这里发布课堂公告和作业，班级成员也能随时交流讨论。
        </div>
      </div>
    </div>
  </div>

  <div class="item-container">
    <div class="intro-item">
      <div class="pre">
        <video width="600" height="400" controls>
          <source src="/about.mp4" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div class="pre-text">
        <div class="subtitle">
          关于CoCoPals
        </div>
        <div class="text">
          如果你对我们的项目感兴趣，可以在这里了解CoCoPals的设计理念和使用的协作技术哦！
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted,ref } from 'vue';
import StickyNavbar from '../components/Navbar.vue';

const handleMouseMove = (event: MouseEvent) => {
    const card = event.currentTarget as HTMLElement;
    const rect = card.getBoundingClientRect();
    const x = event.clientX - rect.left - rect.width / 2;
    const y = event.clientY - rect.top - rect.height / 2;
    const rotateX = (y / rect.height) * 15;
    const rotateY = (x / rect.width) * -15;

    card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
    card.style.boxShadow = `${x * 0.1}px ${y * 0.1}px 20px rgba(0, 0, 0, 0.1)`;
    card.style.backgroundImage = `radial-gradient(circle at ${x + rect.width / 2}px ${y + rect.height / 2}px, rgba(255, 255, 255, 0.5), transparent)`;
};

const resetCard = (event: MouseEvent) => {
    const card = event.currentTarget as HTMLElement;
    card.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg)`;
    card.style.boxShadow = `none`;
    card.style.backgroundImage = `none`;
};
</script>

<style scoped>
.intro-container {
  display: flex;
  justify-content: center;
  margin: 200px 0 200px 0;
}
.intro-title {
  display: flex;
  justify-content: space-between;
  height: 300px;
  width: 60%;
  background-color: #3f3f3f;
  border-radius: 40px;
}

.card {
  width: 500px;
  height: 400px;
  padding: 10px;
  margin-top: -50px;
  margin-left: -150px;
  border-radius: 10px;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background-image 0.2s ease;
}

.card-image {
  width: 100%;
  height: auto;
  border-radius: 10px;
  object-fit: cover;
}  

.item-container {
  display: flex;
  justify-content: center;
  margin: 100px 0 100px 0;
}

.intro-text {
  margin: 70px 50px 0 0;
  width: 50%;
  font-size: large;
  color: rgb(235, 235, 235);
}

.intro-item {
  width: 80%;
  height: 500px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between; 
  align-items: center; 
  background-color: rgb(255, 255, 255);
}

.pre-text {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: end; 
  width: 50%;
  height: 380px;
  margin: 60px;
}

.subtitle {
  font-size: 30px;
  color: rgb(33, 33, 33);
}

.text {
  font-size: large;
  color: rgb(109, 109, 109);
}

.pre {
  margin-left: 20px;
}
</style>