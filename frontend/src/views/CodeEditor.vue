<template>
  <StickyNavbar/>
  <UserList
      :documentId="documentId"
  />
  <audio ref="remoteAudio" autoplay></audio>
  <div class="wrapper-ce">
    <div class="inner-wrapper">

      <!-- 代码运行器 -->
      <div class="component-wrapper">
        <CodeRunner
            :code="code"
            :selectedLanguage="selectedLanguage"
            height="500px"
            width="200px"
            :canCollaborate="canCollaborate"
        />
      </div>

      <div class="code-section">
        <div class="title">
          <p><strong>标题：</strong> {{ fileInfo.title }}</p>
          <p><strong>所有者：</strong> {{ fileInfo.owner }}</p>
          <p><strong>创建时间：</strong> {{ fileInfo.createdAt }}</p>
          <div class="class-control">
            <button
                v-if="canAnnotate"
                @click="toggleAnnotation"
                class="annotation-btn"
                :class="{ 'active': isAnnotating }"
            >
              {{ isAnnotating ? '结束批注' : '开始批注' }}
            </button>
            <button
                v-if="userRole === 'TEACHER'"
                @click="endClass"
                class="end-class-btn"
            >
              结束课堂
            </button>
            <button
                v-else
                @click="exitClass"
                class="exit-class-btn"
            >
              退出课堂
            </button>
          </div>
        </div>

        <!-- 语言选择器和主题选择器以及代码编辑器 -->
        <div class="component-wrapper" ref="editorWrapper" style="position: relative;">
          <SharedbCodeMirror
              v-if="documentId !== 0"
              v-model:code="code"
              v-model:selectedLanguage="selectedLanguage"
              height="500px"
              width="900px"
              :documentId=documentId
              :canCollaborate="canCollaborate"
          />
          <AnnotationLayer
              :width="920"
              :height="562"
              :isAnnotating="isAnnotating && canAnnotate"
              :showToolbar="isAnnotating && canAnnotate"
              :documentId="documentId"
              :style="{
                position: 'absolute',
                top: '0',
                left: '0',
                zIndex: '100',
                pointerEvents: isAnnotating && canAnnotate ? 'auto' : 'none'
              }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, nextTick, onMounted, ref, watch} from 'vue';
import SharedbCodeMirror from '../components/SharedbCodeMirror.vue';
import CodeRunner from '../components/CodeRunner.vue';
import StickyNavbar from '../components/Navbar.vue';
import UserList from '../components/PeopleList.vue';
import AnnotationLayer from '../components/AnnotationLayer.vue';
import {useClassStore} from '../stores/classStore';
import axios from 'axios';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';

// 获取路由中的参数
const route = useRoute();
const router = useRouter();
const classStore = useClassStore();

// 管理在 Main 组件中共享的状态
const documentId = ref<number>(0);
const code = ref<string>("");  // 保存编辑器中的代码
const selectedLanguage = ref<string>("python");  // 保存用户选择的语言

const fileInfo = ref({
  title: '',
  owner: '',
  createdAt: '',
});

// 批注相关状态
const isAnnotating = ref(false);
const canAnnotate = computed(() => fileInfo.value.owner === localStorage.getItem('username'));

const fetchFileInfo = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getfileinfo?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
    fileInfo.value = {
      title: response.data.title,
      owner: response.data.ownerName,
      createdAt: response.data.createTime,
    };
    return fileInfo.value.title;
  } catch (error) {
    ElMessage.error('获取文件信息失败');
    console.error('获取文件信息失败:', error)
    return '';
  }
}

// 获取用户角色
const userRole = ref(localStorage.getItem("role") || '');
const userId = ref(localStorage.getItem("id")||'');

const fetchUsers = async () => {
  try {
    const response = await axios.get(`http://localhost:8048/document/getUsersByDocumentId?documentId=${documentId.value}`,{
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });

    console.log(response.data)
    console.log(fileInfo.value.owner)
    return response.data.map((user: any) => ({
      id: user.id,
      name: user.username,
      canCollaborate: user.username === fileInfo.value.owner,
      micEnabled: false,
    }));
  } catch (error) {
    ElMessage.error('获取协作者列表失败');
    console.error('获取协作者列表失败:', error)
    return [];
  }
}

// 计算当前课程
const currentClass = computed(() => {
  return classStore.classes.find(c => c.documentId === documentId.value);
});

// 计算当前用户的协作权限
const canCollaborate = computed(() => {
  const user = currentClass.value?.users.find(u => u.id === Number(userId.value));
  console.log("当前用户操作权限"+user?.canCollaborate)
  return user?.canCollaborate || false;
});

// 批注功能
const toggleAnnotation = () => {
  isAnnotating.value = !isAnnotating.value;
};

// 结束课堂 (老师)
const endClass = async () => {
  router.back();
}

const exitClass = () => {
  router.back();
}

//语音部分webrtc
const roomId = computed(() => String(documentId.value));
const socket = ref<WebSocket | null>(null)
const localStream = ref<MediaStream | null>(null)
const peers = ref<{ [key: string]: RTCPeerConnection }>({})
const joined = ref(false)

const remoteAudio = ref<HTMLAudioElement | null>(null)

// 计算当前用户的语音权限
const micEnabled = computed(() => {
  const user = currentClass.value?.users.find(u => u.id === Number(userId.value));
  console.log("当前用户语音权限"+user?.micEnabled)
  return user?.micEnabled || false;
});

function send(message: any) {
  if (socket.value && socket.value.readyState === WebSocket.OPEN) {
    socket.value.send(JSON.stringify({ ...message, roomId: roomId.value }))
  }
}

function sendSignal(data: any) {
  send({ type: 'signal', payload: data })
}

function createPeer(initiator: boolean): RTCPeerConnection {
  const peer = new RTCPeerConnection()
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => peer.addTrack(track, localStream.value!))
  }

  peer.onicecandidate = (e) => {
    if (e.candidate) sendSignal(e.candidate)
  }

  peer.ontrack = (e) => {
    if (remoteAudio.value) {
      remoteAudio.value.srcObject = e.streams[0]
      remoteAudio.value.play()
    }
  }

  if (initiator) {
    peer.createOffer().then(offer => {
      peer.setLocalDescription(offer)
      sendSignal(offer)
    })
  }

  peer.onconnectionstatechange = () => {
    if (peer.connectionState === 'disconnected' || peer.connectionState === 'failed') {
      peer.close()
    }
  }

  peers.value['peer'] = peer
  return peer
}

function leaveRoom() {
  if (socket.value && socket.value.readyState === WebSocket.OPEN) {
    socket.value.send(JSON.stringify({ type: 'leave', roomId: roomId.value }))
  }

  // 关闭所有 peer 连接
  Object.values(peers.value).forEach(peer => peer.close())
  peers.value = {}

  // 停止本地流
  localStream.value?.getTracks().forEach(track => track.stop())
  localStream.value = null

  // 关闭 socket
  socket.value?.close()
  socket.value = null

  // 重置状态
  joined.value = false
  console.log('已退出房间')
}

async function handleSignal(data: any) {
  let peer = peers.value['peer']
  if (!peer) peer = createPeer(false)

  if (data.type === 'offer') {
    await peer.setRemoteDescription(data)
    const answer = await peer.createAnswer()
    await peer.setLocalDescription(answer)
    sendSignal(answer)
  } else if (data.type === 'answer') {
    await peer.setRemoteDescription(data)
  } else if (data.candidate) {
    await peer.addIceCandidate(data)
  }
}

async function joinRoom() {
  if (!roomId.value) return

  try {
    localStream.value = await navigator.mediaDevices.getUserMedia({ audio: true })
  } catch (err) {
    console.error('获取麦克风失败：', err)
    return
  }

  socket.value = new WebSocket('ws://localhost:3000')

  socket.value.onopen = () => {
    send({ type: 'join', roomId: roomId.value })
    joined.value = true
  }

  socket.value.onmessage = async (event) => {
    const data = JSON.parse(event.data)
    switch (data.type) {
      case 'new-peer':
        createPeer(true)
        break
      case 'signal':
        handleSignal(data.payload)
        break
    }
  }
}

// 监听 micEnabled 的变化
watch(micEnabled, (newVal) => {
  if (newVal) {
    joinRoom();
  } else {
    leaveRoom();
  }
});

onMounted(async () => {
  documentId.value = Number(route.query.documentId);
  await fetchFileInfo();
  const users = await fetchUsers();

  // 添加到课程列表
  classStore.addClass(documentId.value, users);
});
</script>

<style scoped>
.wrapper-ce {
  margin-top: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.inner-wrapper {
  display: flex;
  justify-content: center;
  align-items: end;
  border-radius: 10px;
  padding: 10px;
  gap: 10px;
}

.code-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative; /* 添加相对定位 */
}

.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 10px;
}

.class-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.annotation-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.annotation-btn:hover {
  background-color: #40a9ff;
}

.annotation-btn.active {
  background-color: #096dd9;
}

.end-class-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.end-class-btn:hover {
  background-color: #ff7875;
}

.exit-class-btn {
  background-color: transparent;
  color: #ff4d4f;
  border: 1px solid #ff4d4f;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.exit-class-btn:hover {
  background-color: #fff2f0;
}
</style>