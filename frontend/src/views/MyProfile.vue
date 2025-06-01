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
      <div>我想
        <el-button
          type="primary"
          @click="dialogVisible = true"
          link
        >
          退出登录
        </el-button>
      </div>
      <el-dialog
        title="确认退出"
         v-model="dialogVisible"
        width="30%"
      >
        <p>确定要退出登录吗？</p>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="logout">确认</el-button>
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
              <img src="/avatar.png" alt="头像" class="avatar" />
              <el-icon class="edit-icon" @click="toggleEdit"><Edit /></el-icon>
            </div>

            <!-- 个人信息输入表单 -->
            <div class="profile-info">
              <div class="info-item">
                <span class="label">昵称:</span>
                <el-input v-model="user.nickname" :disabled="!isEditing" placeholder="请输入昵称" class="input"></el-input>
              </div>
              <div class="info-item">
                <span class="label">性别:</span>
                <el-radio-group v-model="user.gender" size="small" :disabled="true" class="gender-group">
                  <el-radio-button label="male" class="gender-radio male">男</el-radio-button>
                  <el-radio-button label="female" class="gender-radio female">女</el-radio-button>
                </el-radio-group>
              </div>
            
              <div class="info-item">
                <span class="label">生日:</span>
                  <el-date-picker
                    v-model="user.birthday"
                    type="date"
                    :disabled="true"
                    placeholder="选择生日"
                    class="input date-picker"
                  ></el-date-picker>
              </div>

              <div class="info-item">
                <span class="label">学校:</span>
                <el-input v-model="user.school" :disabled="true" placeholder="请输入学校" class="input"></el-input>
              </div>

              <div class="info-item">
                <span class="label">Github:</span>
                <el-input v-model="user.github" :disabled="true" placeholder="请输入Github地址" class="input"></el-input>
              </div>

              <!-- 保存按钮 -->
              <el-button v-if="isEditing" type="primary" @click="saveChanges" class="save-button">保存</el-button>
            </div>
          </el-card>
        </div>
        <div v-else-if="activeSection === 'activity'">
          <div class="activities-container">
            <ActivityCard/>
          </div>
        </div>
        <div v-if="activeSection === 'class'">
          <div class="classes-container">
            <ClassCard/>
          </div>
        </div>
        <div v-show="activeSection === 'analysis'">
          <div class="analysis-container">
            <LearningAnalysis/>
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
import LearningAnalysis from '../components/LearningAnalysis.vue';
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
  school: '同济大学',
  github: 'https://github.com/SmithTime'
});

// 定义用户格言
const userMotto = ref('行百里者半九十。');

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
        username: user.value.nickname,
        email: localStorage.getItem('useremail')
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        }
      }
    );

    if (response.status === 200) {
      const token = response.data;

      localStorage.setItem('username', user.value.nickname ?? '未设置昵称'); 
      ElMessage({
        message: '修改已保存',
        type: 'success',
        duration: 3000, 
      })

    } else {
      console.error('修改失败:', response.data);
      ElMessage({
        message: '修改失败！',
        type: 'error',
        duration: 3000, 
      })
    }
  } catch (error:any) {
    if (error.response) {
      console.log('响应错误:', error.response);
      ElMessage({
        message: error.response.data|| '修改过程中发生错误',
        type: 'error',
        duration: 3000, 
      })
    } else if (error.request) {
      console.log('请求错误:', error.request);
      ElMessage({
        message: '服务器无响应',
        type: 'error',
        duration: 3000, 
      })
    } else {
      console.log('其他错误:', error.message);
      ElMessage({
        message: '发生未知错误',
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
    console.log('触发退出登录');
    const response = await axios.get('http://localhost:8048/account/logout',  {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.status === 200) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('useremail');
      localStorage.removeItem('role');

      ElMessage({
        message: '退出登录成功！',
        type: 'success',
        duration: 3000
      });

      window.location.href = '/login';  
    } else {
      ElMessage({
        message: '退出登录失败，请重试',
        type: 'error',
        duration: 3000
      });
    }

  } catch (error) {
    console.error('退出登录失败:', error);
    ElMessage({
      message: '退出登录过程中发生错误',
      type: 'error',
      duration: 3000
    });
  } finally {
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
  border-radius: 30px;
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
  width: 20px;
  height: 20px;
  background:#afdfff;
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