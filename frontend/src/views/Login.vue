<template>
  <section class="container">
    <div class="login-container">
      <div class="circle circle-one"></div>
      <div class="form-container">
        <div style="display: flex;justify-content: space-between;">
          <h1 class="opacity">LOGIN</h1>
          <ModelViewer />
        </div>
        <form @submit.prevent="handleLogin">
          <input
            type="text"
            v-model="identity"
            placeholder="USERNAME OR EMAIL"
            class="form-input"
          />
          <input
            type="password"
            v-model="password"
            placeholder="PASSWORD"
            class="form-input"
          />
          <button class="form-button opacity">SUBMIT</button>
        </form>
        <div class="register-forget opacity">
          <router-link to="/register" @click.prevent="$emit('register')">REGISTER</router-link>
          <a href="#" @click.prevent="$emit('forgot-password')">FORGOT PASSWORD</a>
        </div>
      </div>
      <div class="circle circle-two"></div>
    </div>
    <router-link to="/">
      <el-button
        type="primary"
        link
      >
        Continue as Guest
      </el-button>
    </router-link>
  </section>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import ModelViewer from '../components/ModelViewer.vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $message: typeof ElMessage;
  }
}

const identity = ref('');
const password = ref('');

const router = useRouter(); // 获取路由实例

const handleLogin = async () => {
  try {
    // 发送登录请求
    const response = await axios.post('http://localhost:8048/account/login', {
      identity: identity.value,
      password: password.value,
    });

    if (response.status === 200) {
      // 登录成功，获取 token
      const token = response.data.token;
      localStorage.setItem('token', token); 
      const role = response.data.role;
      localStorage.setItem('role', role); 
      ElMessage({
        message: 'Login Successful!',
        type: 'success',
        duration: 3000, 
      })

      // 使用 token 进行 getInfo 请求
      const response2 = await axios.get('http://localhost:8048/account/getinfo', {
        headers: {
          'Authorization': `Bearer ${token}`  // 添加 token 到请求头
        }
      });
      let username = response2.data.username; 
      let useremail = response2.data.email; 
      localStorage.setItem('username', username); 
      localStorage.setItem('useremail', useremail); 
      console.log('GetInfo successful:', response2.data);

      // 跳转到首页
      router.push('/');
    } else {
      console.error('Login failed:', response.data);
      ElMessage({
        message: 'Login failed!',
        type: 'error',
        duration: 3000, 
      })
    }
  } catch (error:any) {
    if (error.response) {
      // 这是 Axios 处理的响应错误
      console.log('Response error:', error.response);
      ElMessage({
        message: error.response.data|| 'An error occurred during login.',
        type: 'error',
        duration: 3000, 
      })
    } else if (error.request) {
      // 请求已发送，但没有收到响应
      console.log('Request error:', error.request);
      ElMessage({
        message: 'No response from server.',
        type: 'error',
        duration: 3000, 
      })
    } else {
      // 其他错误
      console.log('Other error:', error.message);
      ElMessage({
        message: 'An unknown error occurred.',
        type: 'error',
        duration: 3000, 
      })
    }
  }
};
</script>

<style scoped>
a {
  text-decoration: none;
  color: rgb(255, 255, 255);
}

h1 {
  font-size: 2.5rem;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
  gap: 5px;
  background: linear-gradient(45deg, #9BBCC3, #CBA5D1);
}

.login-container {
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

.form-input {
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

.form-input:focus {
  box-shadow: 0 0 16px 1px rgba(0, 0, 0, 0.2);
  animation: wobble 0.3s ease-in;
}

.form-button {
  background-color: black;
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

.form-button:hover {
  box-shadow: 0 0 10px 1px rgba(0, 0, 0, 0.15);
  transform: scale(1.02);
}

.circle {
  width: 8rem;
  height: 8rem;
  background: rgba(37, 37, 37, 0.8);
  border-radius: 50%;
  position: absolute;
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

</style>