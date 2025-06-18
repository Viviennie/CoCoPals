<template>
  <StickyNavbar/>
  <UserList
      :documentId="documentId"
  />
  <div v-for="peerId in Object.keys(peers)" :key="peerId">
    <audio :ref="el => setAudioRef(peerId, el)" autoplay></audio>
  </div>
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
import {computed, nextTick, onMounted, ref, watch ,reactive ,onBeforeUnmount} from 'vue';
import SharedbCodeMirror from '../components/SharedbCodeMirror.vue';
import CodeRunner from '../components/CodeRunner.vue';
import StickyNavbar from '../components/Navbar.vue';
import UserList from '../components/PeopleList.vue';
import AnnotationLayer from '../components/AnnotationLayer.vue';
import {useClassStore} from '../stores/classStore';
import axios from 'axios';
import {useRoute, useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import type { ComponentPublicInstance } from 'vue'

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
// peers: { clientId: RTCPeerConnection }
const peers = reactive<{ [clientId: string]: RTCPeerConnection }>({})
// 远端音频元素 refs
const audioElements = reactive<{ [clientId: string]: HTMLAudioElement | null }>({})
const joined = ref(false)
let selfClientId = ''

function setAudioRef(clientId: string, el: Element | ComponentPublicInstance | null) {
  if (el instanceof HTMLAudioElement) {
    audioElements[clientId] = el
  }
}

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

function createPeer(clientId: string, initiator: boolean) {
  if (peers[clientId]) return peers[clientId] // 已存在

  const peer = new RTCPeerConnection()

  if (localStream.value) {
    localStream.value.getTracks().forEach(track => peer.addTrack(track, localStream.value!))
  }

  peer.onicecandidate = e => {
    if (e.candidate) {
      // 这里确保传递的 candidate 对象是标准格式
      send({
        type: 'signal',
        payload: {
          candidate: {
            candidate: e.candidate.candidate,
            sdpMid: e.candidate.sdpMid,
            sdpMLineIndex: e.candidate.sdpMLineIndex
          }
        },
        to: clientId
      })
    }
  }

  peer.ontrack = e => {
    const audio = audioElements[clientId]
    if (audio) {
      audio.srcObject = e.streams[0]
      audio.play()
    }
  }

  peer.onconnectionstatechange = () => {
    if (peer.connectionState === 'disconnected' || peer.connectionState === 'failed') {
      peer.close()
      delete peers[clientId]
      delete audioElements[clientId]
    }
  }

  if (initiator) {
    peer.createOffer().then(offer => {
      return peer.setLocalDescription(new RTCSessionDescription(offer))
          .then(() => {
            send({
              type: 'signal',
              payload: offer,
              to: clientId
            })
          })
    }).catch(err => console.error('createOffer error:', err))
  }


  peers[clientId] = peer
  return peer
}

// 缓存 ICE candidates，直到 remoteDescription 设置完成
const pendingCandidates: { [clientId: string]: RTCIceCandidateInit[] } = {}

async function handleSignal(fromClientId: string, data: any) {
  let peer = peers[fromClientId]
  if (!peer) {
    peer = createPeer(fromClientId, false)
  }

  try {
    if (data.type === 'offer') {
      console.log(`[信令][offer] peer signalingState: ${peer.signalingState}`)

      // 只有在 stable 状态下才能处理 offer，避免重复或竞态
      if (peer.signalingState === 'stable') {
        await peer.setRemoteDescription(new RTCSessionDescription(data))
        const answer = await peer.createAnswer()
        await peer.setLocalDescription(new RTCSessionDescription(answer))
        send({
          type: 'signal',
          payload: answer,
          to: fromClientId
        })

        // 处理缓存的 candidates
        if (pendingCandidates[fromClientId]) {
          for (const candidate of pendingCandidates[fromClientId]) {
            await peer.addIceCandidate(new RTCIceCandidate(candidate)).catch(err => {
              console.warn('addIceCandidate failed (cached):', err, candidate)
            })
          }
          delete pendingCandidates[fromClientId]
        }
      } else {
        console.warn(`收到offer时状态不对，忽略 offer，当前状态: ${peer.signalingState}`)
      }
    } else if (data.type === 'answer') {
      console.log(`[信令][answer] peer signalingState: ${peer.signalingState}`)

      // 只有在 have-local-offer 状态才能设置 answer
      if (peer.signalingState === 'have-local-offer') {
        await peer.setRemoteDescription(new RTCSessionDescription(data))

        if (pendingCandidates[fromClientId]) {
          for (const candidate of pendingCandidates[fromClientId]) {
            await peer.addIceCandidate(new RTCIceCandidate(candidate)).catch(err => {
              console.warn('addIceCandidate failed (cached):', err, candidate)
            })
          }
          delete pendingCandidates[fromClientId]
        }
      } else {
        console.warn(`收到answer时状态不对，忽略 answer，当前状态: ${peer.signalingState}`)
      }
    } else if (data.candidate) {
      const candidate = data.candidate
      if (peer.remoteDescription && peer.remoteDescription.type) {
        await peer.addIceCandidate(new RTCIceCandidate(candidate)).catch(err => {
          console.warn('addIceCandidate failed:', err, candidate)
        })
      } else {
        if (!pendingCandidates[fromClientId]) {
          pendingCandidates[fromClientId] = []
        }
        pendingCandidates[fromClientId].push(candidate)
      }
    }
  } catch (err) {
    console.error('handleSignal error:', err)
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
    console.log('WebSocket connected')
  }

  socket.value.onmessage = async (event) => {
    const data = JSON.parse(event.data)

    if (data.type === 'signal') {
      const payload = data.payload;
      if (payload.type === 'offer') {
        console.log('[信令][offer] 收到offer:', payload);
      } else if (payload.type === 'answer') {
        console.log('[信令][answer] 收到answer:', payload);
      } else if (payload.candidate) {
        console.log('[信令][candidate] 收到candidate:', payload);
      } else {
        console.log('[信令] 收到未知payload:', payload);
      }
    }
    switch (data.type) {
      case 'init':
        selfClientId = data.clientId
        send({ type: 'join', roomId: roomId.value })
        joined.value = true
        break

      case 'peers':
        // 房间已有其他人，主动发起 offer
        for (const peerId of data.peers) {
          createPeer(peerId, true)
        }
        break

      case 'new-peer':
        if (data.clientId !== selfClientId) {
          // 新人加入，先不主动发起offer，等信令来
          createPeer(data.clientId, false)
        }
        break

      case 'signal':
        if (data.clientId && data.clientId !== selfClientId) {
          await handleSignal(data.clientId, data.payload)
        }
        break

      case 'peer-left':
        if (peers[data.clientId]) {
          peers[data.clientId].close()
          delete peers[data.clientId]
          delete audioElements[data.clientId]
        }
        break
    }
  }
}

function leaveRoom() {
  if (socket.value && socket.value.readyState === WebSocket.OPEN) {
    send({ type: 'leave', roomId: roomId.value })
    socket.value.close()
  }

  Object.values(peers).forEach(peer => peer.close())

  // peers 和 audioElements 是 reactive 对象，不能直接赋空对象，要逐个删除
  for (const key in peers) {
    delete peers[key]
  }
  for (const key in audioElements) {
    delete audioElements[key]
  }

  localStream.value?.getTracks().forEach(track => track.stop())
  localStream.value = null

  joined.value = false
}

//关闭麦克风
function muteMicrophone() {
  if (!localStream.value) return
  localStream.value.getAudioTracks().forEach(track => {
    track.enabled = false  // 直接禁用轨道（效果是停止发送音频）
  })
}

//打开麦克风
function unmuteMicrophone() {
  if (!localStream.value) return
  localStream.value.getAudioTracks().forEach(track => {
    track.enabled = true
  })
}

// 监听 micEnabled 的变化
watch(micEnabled, (newVal) => {
  if (newVal) {
    unmuteMicrophone();
  } else {
    muteMicrophone();
  }
});

onMounted(async () => {
  documentId.value = Number(route.query.documentId);
  await fetchFileInfo();
  const users = await fetchUsers();
  await joinRoom();
  muteMicrophone()
  // 添加到课程列表
  classStore.addClass(documentId.value, users);
});

onBeforeUnmount(() => {
  leaveRoom()
})

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