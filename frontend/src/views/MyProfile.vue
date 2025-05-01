<template>
    <StickyNavbar/>
    <ThemeSelector :initialTheme="currentTheme" />
    <div class="wrapper-out"> 
    <div class="wrapper-my">
      <div class="wrapper-left">
        <UserCard 
        :user="user"
        :activeSection="activeSection"
        @update:activeSection="setActiveSection"
        />
        <div>I want to
          <el-button
            type="primary"
            @click="dialogVisible = true"
            link
          >
            logout
          </el-button>
        </div>
        <el-dialog
          title="Confirm Logout"
           v-model="dialogVisible"
          width="30%"
        >
          <p>Are you sure you want to log out?</p>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="logout">Confirm</el-button>
          </span>
        </el-dialog>
      </div>

      <div class="wrapper-right">
        <div class="motto-container">
          <Motto :motto="userMotto"
          @update:motto="updateUserMotto" />
        </div>

        <div class="content-section">
          <div v-if="activeSection === 'profile'">
            <el-card class="profile-card">
              <!-- 头像和编辑图标 -->
              <div class="profile-avatar">
                <img src="/avatar.png" alt="Avatar" class="avatar" />
                <el-icon class="edit-icon" @click="toggleEdit"><Edit /></el-icon>
              </div>

              <!-- 个人信息输入表单 -->
              <div class="profile-info">
                <div class="info-item">
                  <span class="label">Nickname:</span>
                  <el-input v-model="user.nickname" :disabled="!isEditing" placeholder="Enter your nickname" class="input"></el-input>
                </div>
                <!-- :disabled="!isEditing" -->
                <div class="info-item">
                  <span class="label">Gender:</span>
                  <el-radio-group v-model="user.gender" size="small" :disabled="true" class="gender-group">
                    <el-radio-button label="male" class="gender-radio male">Male</el-radio-button>
                    <el-radio-button label="female" class="gender-radio female">Female</el-radio-button>
                  </el-radio-group>
                </div>
              
                <div class="info-item">
                  <span class="label">Birthday:</span>
                  <!-- 使用 el-date-picker 并添加自定义图标 -->
                    <el-date-picker
                      v-model="user.birthday"
                      type="date"
                      :disabled="true"
                      placeholder="Select birthday"
                      class="input date-picker"
                    ></el-date-picker>
                </div>

                <div class="info-item">
                  <span class="label">School:</span>
                  <el-input v-model="user.school" :disabled="true" placeholder="Enter your school" class="input"></el-input>
                </div>

                <div class="info-item">
                  <span class="label">Github:</span>
                  <el-input v-model="user.github" :disabled="true" placeholder="Enter your Github URL" class="input"></el-input>
                </div>

                <!-- 保存按钮 -->
                <el-button v-if="isEditing" type="primary" @click="saveChanges" class="save-button">Save</el-button>
              </div>
            </el-card>
          </div>
          <div v-else-if="activeSection === 'activity'">
            <!-- <div class="radar-chart-container">
              <RadarChart/>
            </div>
            <div class="calendar-container">
              <Calendar/>
            </div>   -->
            <div class="activities-container">
              <ActivityCard/>
            </div>
          </div>
          <div v-if="activeSection === 'class'">
            <div class="classes-container">
              <ClassCard/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  </template>
  
  <script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import StickyNavbar from '../components/Navbar.vue';
  import UserCard from '../components/UserCard.vue';
  import Motto from '../components/Motto.vue';
  import RadarChart from '../components/RadarChart.vue';
  import Calendar from '../components/Calendar.vue';
  import { Edit } from '@element-plus/icons-vue'
  import ActivityCard from '../components/Activities.vue';
  import ClassCard from '../components/Classes.vue';
  import { ElCard } from 'element-plus';
  import axios from 'axios';
  import { ElMessage, ElDialog, ElButton } from 'element-plus';

  const dialogVisible = ref(false) // 控制弹窗是否显示

  // 定义当前主题
  const currentTheme = ref({
    background: '#1A1A2E',
    color: '#FFFFFF',
    primaryColor: '#0F3460'
  });
  
  // 定义用户信息
  const user = ref({
    name: localStorage.getItem('username'),
    email: localStorage.getItem('useremail'),
    nickname: localStorage.getItem('username'),
    gender: 'male',
    birthday: '2000-01-01',
    school: 'Tongji University',
    github: 'https://github.com/SmithTime'
  });

  // 定义用户格言
  const userMotto = ref('Believe you can and you\'re halfway there.');

  // 更新用户格言
  const updateUserMotto = (newMotto: string) => {
      userMotto.value = newMotto;
    };

  // 定义当前激活的展示内容
  const activeSection = ref('profile');

  const isEditing = ref(false); // 控制编辑模式

  // 切换编辑模式
  const toggleEdit = () => {
    isEditing.value = !isEditing.value;
  };

  // 保存更改
  const saveChanges = async () => {
    try {
      isEditing.value = false;
      
      const response = await axios.post(
        'http://localhost:8048/account/editinfo',
        {
          // 请求体的内容
          username: user.value.nickname,
          email: localStorage.getItem('useremail')
        },
        {
          // 请求头部分
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json', // 确保内容类型是 JSON
          }
        }
      );

      if (response.status === 200) {
        const token = response.data;

        localStorage.setItem('username', user.value.nickname ?? 'No nickname'); 
        ElMessage({
          message: 'your change is reserved.',
          type: 'success',
          duration: 3000, 
        })

      } else {
        console.error('Edit failed:', response.data);
        ElMessage({
          message: 'Edit failed!',
          type: 'error',
          duration: 3000, 
        })
      }
    } catch (error:any) {
      if (error.response) {
        // 这是 Axios 处理的响应错误
        console.log('Response error:', error.response);
        ElMessage({
          message: error.response.data|| 'An error occurred during edit.',
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

  // 设置当前激活的展示内容
  const setActiveSection = (section: string) => {
    activeSection.value = section;
  };

  

  // 退出登录
  const logout = async () => {
    try {
      console.log('logout triggered ');
      // 调用后端登出 API
      const response = await axios.get('http://localhost:8048/account/logout',  {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.status === 200) {
        // 登出成功，清除本地存储的用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('useremail');
        localStorage.removeItem('role');

        ElMessage({
          message: 'Logged out successfully!',
          type: 'success',
          duration: 3000
        });

        // 跳转到登录页
        window.location.href = '/login';  
      } else {
        ElMessage({
          message: 'Logout failed, please try again.',
          type: 'error',
          duration: 3000
        });
      }

    } catch (error) {
      console.error('Logout failed:', error);
      ElMessage({
        message: 'An error occurred during logout.',
        type: 'error',
        duration: 3000
      });
    } finally {
      // 关闭弹窗
      dialogVisible.value = false;
    }
  };

  </script>
  
  <style scoped>
  .wrapper-out {
    padding: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .wrapper-my {
    display: flex;
    justify-content: center;
    align-items: start;
    gap: 5mm;
  }

  .wrapper-left {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .wrapper-left div {
    font-size: 13px;
  }

  .wrapper-right {
    display: flex;
    flex-direction: column;
  }

  .motto-container {
  }
  .content-section{
  }

.profile-card {
  width: 650px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-avatar {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.edit-icon {
  position: absolute;
  bottom: 2px;
  right: calc(50% - 60px);
  width: 30px;
  height: 30px;
  background:linear-gradient(45deg, #9BBCC3, #CBA5D1);
  border-radius: 50%;
  padding: 5px;
  cursor: pointer;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
}

.label {
  font-size: 14px;
  color: #6D6D6D;
  width: 100px;
  text-align: left;
  margin-right:10px;
}

.input {
  width: 80%;
}

/* 性别选择器的样式 */
/* .gender-group {
  
  display: flex;
  align-items: center;
}

.gender-radio {
  margin-right: 50px; 
}

.gender-radio.male.is-active .el-radio-button__inner {
  background-color: #DAEDF6;
  color: #3E7FBF;
  border:none;
}

.gender-radio.female.is-active .el-radio-button__inner {
  background-color: #FBE8F1; 
  color: #B23A7E;
  border:none;
}

.save-button {
  margin-top: 20px;
  align-self: flex-end;
}

.radar-chart-container {
}
.calendar-container {
} */

  </style>