<template>
  <section class="container">
    <div class="register-container">
      <div class="circle circle-one"></div>
      <div class="form-container">
        <div style="display: flex; justify-content: space-between;">
          <h1 class="opacity" style="font-size: 30px;">注册账号</h1>
          <img src="/logo.png" alt="logo" class="logo" />
        </div>
        <form @submit.prevent="handleRegister">
          <input
            type="text"
            v-model="username"
            placeholder="用户名"
            class="register-container form-input"
          />
          <input
            type="password"
            v-model="password"
            placeholder="密码"
            class="register-container form-input"
          />
          <input
            type="password"
            v-model="confirmPassword"
            placeholder="确认密码"
            class="register-container form-input"
          />
          <input
            type="email"
            v-model="email"
            placeholder="电子邮箱"
            class="register-container form-input"
          />
          <button class="register-container form-button opacity">提交</button>
        </form>
        <div class="register-forget opacity">
          <router-link to="/login" @click.prevent="$emit('login')">已有账号立即登录</router-link>
          <a href="#" @click.prevent="$emit('forgot-password')">忘记密码</a>
        </div>
      </div>
      <div class="circle circle-two"></div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import ModelViewer from '../components/ModelViewer.vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus'

const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const email = ref('');

const router = useRouter(); // 获取路由实例

const handleRegister = async () => {
  // 确认密码是否一致
  if (password.value !== confirmPassword.value) {
    ElMessage({
      message: '两次输入的密码不一致！',
      type: 'error',
      duration: 3000, // 3秒后消失
    })
    return;
  }

  try {
    const response = await axios.post('http://localhost:8048/account/register', {
      username: username.value,
      password: password.value,
      email: email.value,
    });

    if (response.status === 200) {
      ElMessage.success('注册成功');
      console.log('注册成功:', response.data);
      // 注册成功后跳转到首页或登录页
      router.push('/login');
    } else {
      console.error('注册失败:', response.data);
      ElMessage.error('注册失败！');
    }
  } catch (error:any) {
    if (error.response) {
      // 这是 Axios 处理的响应错误
      console.log('响应错误:', error.response);
      ElMessage.error(error.response.data|| '注册过程中发生错误');
    } else if (error.request) {
      // 请求已发送，但没有收到响应
      console.log('请求错误:', error.request);
      ElMessage.error('服务器无响应');
    } else {
      // 其他错误
      console.log('其他错误:', error.message);
      ElMessage.error('发生未知错误');
    }
  }
};
</script>
  
<style scoped>
/* 样式部分保持不变 */
a {
  text-decoration: none;
  color: rgb(255, 255, 255);
}

h1 {
  font-size: 2.5rem;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(45deg, #aeecf9, rgb(255, 252, 198));
}

.register-container {
  position: relative;
  width: 22.2rem;
}

.form-container {
  border: 1px solid hsla(0, 0%, 65%, 0.158);
  box-shadow: 0 0 36px 1px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  backdrop-filter: blur(20px);
  z-index: 99;
  padding: 2rem;
}

.register-container .form-input {
  display: block;
  padding: 14.5px;
  width: 90%;
  margin: 2rem 0;
  color: black;
  outline: none;
  background-color: #9191911f;
  border: none;
  border-radius: 5px;
  font-weight: 500;
  letter-spacing: 0.8px;
  font-size: 15px;
  backdrop-filter: blur(15px);
}

.register-container .form-input:focus {
  box-shadow: 0 0 16px 1px rgba(0, 0, 0, 0.2);
  animation: wobble 0.3s ease-in;
}

.register-container .form-button {
  background-color: rgb(0, 0, 0);
  color: gainsboro;
  display: block;
  padding: 13px;
  border-radius: 5px;
  outline: none;
  font-size: 18px;
  letter-spacing: 1.5px;
  font-weight: bold;
  width: 100%;
  cursor: pointer;
  margin-bottom: 2rem;
  transition: all 0.1s ease-in-out;
  border: none;
}

.register-container .form-button:hover {
  box-shadow: 0 0 10px 1px rgba(0, 0, 0, 0.15);
  transform: scale(1.02);
}

.circle {
  width: 8rem;
  height: 8rem;
  background: aliceblue;
  border-radius: 50%;
  position: absolute;
  background: rgba(37, 37, 37, 0.8);
}

.illustration {
  position: absolute;
  top: -14%;
  right: -2px;
  width: 90%;
}

.circle-one {
  top: 0;
  left: 0;
  transform: translate(-45%, -45%);
}

.circle-two {
  bottom: 0;
  right: 0;
  transform: translate(45%, 45%);
}

.register-forget {
  margin: 1rem 0;
  display: flex;
  justify-content: space-between;
}

.opacity {
  opacity: 0.6;
}

@keyframes wobble {
  0% {
    transform: scale(1.025);
  }
  25% {
    transform: scale(1);
  }
  75% {
    transform: scale(1.025);
  }
  100% {
    transform: scale(1);
  }
}
.logo {
    width: 160px;
    height: auto;
}
</style>