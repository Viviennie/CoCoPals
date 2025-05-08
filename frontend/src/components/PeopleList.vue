<template>
  <div>
    <!-- 覆盖层，用于半透明遮罩背景 -->
    <div class="overlay" v-if="isOpen" @click="toggleSidebar"></div>

    <!-- 侧边栏 -->
    <nav class="sidebar" :class="{ open: isOpen }">
      <!-- 菜单标题 -->
      <div class="sidebar-header">
        <h2>用户列表</h2>
      </div>

      <ul class="user-item">
        <!-- 当前用户 -->
        <li v-if="currentUser">
          <img src="https://img.icons8.com/bubbles/50/000000/user.png" alt="avatar" class="avatar" />
          <span class="user-name">{{ currentUser.name }} (我)</span>

          <!-- 当前用户的麦克风按钮（总是可修改） -->
          <div class="tooltip-container">
            <button
                class="icon-btn"
                @click.stop="toggleUserMic(currentUser.id)"
            >
              <img
                  :src="currentUser.micEnabled ?
                  'https://img.icons8.com/ios-filled/50/FFFFFF/microphone.png' :
                  'https://img.icons8.com/ios/50/FFFFFF/no-microphone.png'"
                  alt="sound control"
                  class="action-icon"
              />
            </button>
            <span class="tooltip-text">
              {{ currentUser.micEnabled ? '关闭麦克风' : '打开麦克风' }}
            </span>
          </div>

          <!-- 当前用户的协作按钮 -->
          <div class="tooltip-container">
            <button
                class="icon-btn"
                :disabled="userRole!=='TEACHER'"
            >
              <img
                  :src="currentUser.canCollaborate ?
                  'https://img.icons8.com/ios-filled/50/FFFFFF/collaboration.png' :
                  'https://img.icons8.com/ios/50/FFFFFF/collaboration.png'"
                  alt="collaboration control"
                  class="action-icon disabled-icon"
              />
              <span class="tooltip-text">
                {{ userRole === 'TEACHER' ? "当前可协作" :
                  (currentUser.canCollaborate ? "无权限操作(当前可协作)" : "无权限操作(当前不可协作)") }}
              </span>
            </button>
          </div>
        </li>

        <!-- 分割线 -->
        <div class="divider" v-if="currentUser && otherUsers.length > 0"></div>

        <!-- 其他用户列表 -->
        <li
            v-for="user in otherUsers"
            :key="user.id"
        >
          <img src="https://img.icons8.com/bubbles/50/000000/user.png" alt="avatar" class="avatar" />
          <span class="user-name">{{ user.name }}</span>

          <!-- 麦克风控制按钮 -->
          <div class="tooltip-container">
            <button
                class="icon-btn"
                @click.stop="userRole === 'TEACHER' ? toggleUserMic(user.id) : null"
                :disabled="userRole !== 'TEACHER'"
            >
              <img
                  :src="user.micEnabled ?
                  'https://img.icons8.com/ios-filled/50/FFFFFF/microphone.png' :
                  'https://img.icons8.com/ios/50/FFFFFF/no-microphone.png'"
                  alt="sound control"
                  class="action-icon"
              />
            </button>
            <span class="tooltip-text">
              {{ userRole === 'TEACHER' ? (user.micEnabled ? '关闭麦克风' : '打开麦克风') : "无权限操作"}}
            </span>
          </div>

          <!-- 协作权限按钮 -->
          <div class="tooltip-container">
            <button
                class="icon-btn"
                @click.stop="userRole === 'TEACHER' ? toggleUserCollaboration(user.id) : null"
                :disabled="userRole !== 'TEACHER'"
            >
              <img
                  :src="user.canCollaborate ?
                  'https://img.icons8.com/ios-filled/50/FFFFFF/collaboration.png' :
                  'https://img.icons8.com/ios/50/FFFFFF/collaboration.png'"
                  alt="collaboration control"
                  class="action-icon"
                  :class="{ 'disabled-icon': userRole !== 'TEACHER' }"
              />
              <span class="tooltip-text">
                {{ userRole === 'TEACHER' ? (user.canCollaborate ? '取消授权协作' : '授权协作') : "无权限操作"}}
              </span>
            </button>
          </div>
        </li>
      </ul>
    </nav>

    <button class="hamburger" :class="isOpen ? 'is-open' : 'is-closed'" @click="toggleSidebar">
      <span class="hamb-top"></span>
      <span class="hamb-middle"></span>
      <span class="hamb-bottom"></span>
    </button>
  </div>
</template>

<script lang="ts" setup>
import {ref, computed, onMounted} from 'vue';
import {useClassStore} from "../stores/classStore";

const classStore = useClassStore();
const isOpen = ref(false);

const props = defineProps<{
  documentId: Number
}>();

const userRole = ref(localStorage.getItem("role") || '');
const userId = Number(localStorage.getItem("id"));

const currentClass = computed(() => {
  return classStore.classes.find(c => c.documentId === props.documentId);
});
// 计算属性：获取当前用户
const currentUser = computed(() => {
  return currentClass.value?.users.find(user => user.id === userId);
});

// 计算属性：获取其他用户（排除当前用户）
const otherUsers = computed(() => {
  return currentClass.value?.users.filter(user => user.id !== userId);
});

// 切换侧边栏的打开/关闭状态
const toggleSidebar = () => {
  isOpen.value = !isOpen.value;
};

// 切换用户麦克风状态
const toggleUserMic = (userId: number) => {
  const user = currentClass.value?.users.find(u => u.id === userId);
  if (user) {
    user.micEnabled = !user.micEnabled;
  }
};

// 切换用户协作权限（仅老师可用）
const toggleUserCollaboration = (userId: number) => {
  const user = currentClass.value?.users.find(u => u.id === userId);
  if (user) {
    user.canCollaborate = !user.canCollaborate;
  }
};
</script>

<style scoped>
/* 侧边栏样式 */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 300px;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  color: #fff;
  padding-top: 20px;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  z-index: 1000;
}

.sidebar.open {
  transform: translateX(0);
}

/* 菜单标题样式 */
.sidebar-header {
  text-align: center;
  padding: 10px;
  font-size: 1rem;
  font-weight: bold;
  color: #ffffff;
}

/* 用户项样式 */
.user-item {
  list-style: none;
  align-items: center;
  gap: 10px;
  padding: 0;
}

.user-item li {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  gap: 10px;
  position: relative;
}

.user-item li:hover {
  background-color: rgba(0,0,0,0.1);
}

/* 分割线样式 */
.divider {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.2);
  margin: 10px 20px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.user-name {
  flex-grow: 1;
}

/* 图标按钮样式 */
.icon-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-btn:disabled {
  cursor: not-allowed;
}

.action-icon {
  width: 24px;
  height: 24px;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.action-icon:hover {
  opacity: 1;
}

.disabled-icon {
  opacity: 0.4;
}

/* 覆盖层样式 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 900;
}

/* 汉堡菜单样式 */
.hamburger {
  background: transparent;
  border: none;
  display: block;
  height: 32px;
  margin-left: 15px;
  position: fixed;
  top: 15px;
  left: 10px;
  width: 32px;
  z-index: 1100;
  cursor: pointer;
  transition: left 0.3s ease;
}

.hamburger:hover .hamb-top {
  transform: translate(0, -50%);
}

.hamburger:hover .hamb-bottom {
  transform: translate(0, 50%);
}

/* 点击后移动汉堡按钮到侧边栏右侧 */
.hamburger.is-open {
  left: 320px;
}
.hamburger.is-open span {
  background-color: #ffffff;
}

.hamburger.is-closed span {
  background-color: #808080;
}
.hamburger span {
  display: block;
  height: 4px;
  width: 100%;
  border-radius: 1px;
  position: absolute;
  transition: all 0.35s ease-in-out;
}

.hamburger.is-closed .hamb-top {
  top: 5px;
}

.hamburger.is-closed .hamb-middle {
  top: 50%;
  transform: translateY(-50%);
}

.hamburger.is-closed .hamb-bottom {
  bottom: 5px;
}

.hamburger.is-open .hamb-top {
  top: 50%;
  transform: rotate(45deg);
}

.hamburger.is-open .hamb-middle {
  display: none;
}

.hamburger.is-open .hamb-bottom {
  top: 50%;
  transform: rotate(-45deg);
}

/* 工具提示样式 */
.tooltip-container {
  position: relative;
  display: inline-block;
}

.tooltip-text {
  visibility: hidden;
  width: max-content;
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
  text-align: center;
  border-radius: 4px;
  padding: 4px 8px;
  position: absolute;
  z-index: 1;
  bottom: 125%;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
  white-space: nowrap;
}

.tooltip-container:hover .tooltip-text {
  visibility: visible;
  opacity: 1;
}
</style>