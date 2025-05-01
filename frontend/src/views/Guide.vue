<template>
    <StickyNavbar/>
  
    <div class="intro-container">
      <div class="intro-title">
        <div class="card" @mousemove="handleMouseMove" @mouseleave="resetCard">
          <img src="/poster.png" alt="Card Image" class="card-image" />
        </div>
        <div class="intro-text">
          Welcome to CodeJourney Colab! <br><br>
          Here, you can effortlessly and efficiently experience programming classes. Next, we'll briefly introduce the website and guide you on how to get started quickly. 
        </div>
      </div>
    </div>

    <div class="item-container">
      <div class="intro-item">
        <div class="pre">
          <video width="600" height="400" controls>
            <source src="/freecoding.mp4" type="video/mp4">
            your browser don't support
          </video>
        </div>
        <div class="pre-text">
          <div class="subtitle">
            Free Coding
          </div>
          <div class="text">
            Here, you can invite students or partners to write code together, which can speed up the cooperation process and help you solve bug - related problems more quickly.
          </div>
        </div>
      </div>
    </div>

    <div class="item-container">
      <div class="intro-item">
        <div class="pre">
          <video width="600" height="400" controls>
            <source src="/judge.mp4" type="video/mp4">
            your browser don't support
          </video>
        </div>
        <div class="pre-text">
          <div class="subtitle">
            Online Judge
          </div>
          <div class="text">
            Here, you can complete your assignments or freely explore a vast sea of algorithm problems. Our system will automatically record the questions you answer incorrectly. Through practice, you can improve your programming skills and thinking abilities.
          </div>
        </div>
      </div>
    </div>

    <div class="item-container">
      <div class="intro-item">
        <div class="pre">
          <video width="600" height="400" controls>
            <source src="/blog.mp4" type="video/mp4">
            your browser don't support
          </video>
        </div>
        <div class="pre-text">
          <div class="subtitle">
            Blog Share
          </div>
          <div class="text">
            You can view and post class notes or any exciting programming learning experiences here. You can also post the problems you've encountered so that everyone can discuss and solve them together.
          </div>
        </div>
      </div>
    </div>

    <div class="item-container">
      <div class="intro-item">
        <div class="pre">
          <video width="600" height="400" controls>
            <source src="/me.mp4" type="video/mp4">
            your browser don't support
          </video>
        </div>
        <div class="pre-text">
          <div class="subtitle">
            Me
          </div>
          <div class="text">
            Here, you can enrich your personal homepage.
            <br>
            You can check your records of solving algorithm problems. Regularly reviewing these records will be more helpful for consolidating your programming knowledge.
            <br>
            There is also an important class function. Teachers can release announcements and assign homework with just one click. Communication within the class is supported.  
          </div>
        </div>
      </div>
    </div>

    <div class="item-container">
      <div class="intro-item">
        <div class="pre">
          <video width="600" height="400" controls>
            <source src="/about.mp4" type="video/mp4">
            your browser don't support
          </video>
        </div>
        <div class="pre-text">
          <div class="subtitle">
            About Us
          </div>
          <div class="text">
            If you are interested in our project, you can check out our project ideas here, as well as the collaborative algorithms we used. 
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