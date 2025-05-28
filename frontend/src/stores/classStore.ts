import { defineStore } from 'pinia'
import { ref } from 'vue'
import { io, Socket } from 'socket.io-client'

interface User {
    id: number
    name: string
    micEnabled: boolean
    canCollaborate: boolean
}

interface ClassItem {
    documentId: number
    classId: string | null
    startTime: Date
    endTime: Date | null
    users: User[]
    annotations: ArrayBuffer
    scrollLeft: number
    scrollTop: number
}

export const useClassStore = defineStore('class', () => {
    const classes = ref<ClassItem[]>([])
    const socket = ref<Socket | null>(null)
    const isConnected = ref(false)

    // 初始化 WebSocket 连接
    const initWebSocket = () => {
        if (socket.value) return

        // 创建 Socket.IO 连接
        socket.value = io('ws://localhost:8443', {
            transports: ['websocket'], // 强制使用 WebSocket
        })

        socket.value.on('connect', () => {
            isConnected.value = true
            console.log('WebSocket connected')
        })

        socket.value.on('disconnect', () => {
            isConnected.value = false
            console.log('WebSocket disconnected')
        })

        // 监听状态同步消息
        socket.value.on('syncState', (message: { state: ClassItem[] }) => {
            classes.value = message.state.map(classItem => ({
                ...classItem,
                startTime: new Date(classItem.startTime),
                endTime: classItem.endTime ? new Date(classItem.endTime) : null
            }))
        })
    }

    // 添加新课程
    const addClass = (documentId: number, users: User[]) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }

        socket.value.emit('message', {
            type: 'addClass',
            documentId,
            users
        })
    }

    // 结束课程
    const endClass = (documentId: number) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }

        socket.value.emit('message', {
            type: 'endClass',
            documentId
        })
    }

    // 更新用户协作权限
    const updateUserCollaboration = (documentId: number, userId: number, canCollaborate: boolean) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }
        socket.value.emit('message', {
            type: 'updateUserCollaboration',
            documentId,
            userId,
            canCollaborate
        })
    }

    // 更新用户协作权限
    const updateUserMic = (documentId: number, userId: number, micEnabled: boolean) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }
        socket.value.emit('message', {
            type: 'updateUserMic',
            documentId,
            userId,
            micEnabled
        })
    }

    const updateAnnotations = (documentId: number, annotations: Blob) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }
        socket.value.emit('message', {
            type: 'updateAnnotations',
            documentId,
            annotations
        })
    }

    const updateScroll = (documentId: number, scrollLeft: number, scrollTop: number) => {
        if (!socket.value || !isConnected.value) {
            console.error('WebSocket not connected')
            return
        }
        socket.value.emit('message', {
            type: 'updateScroll',
            documentId,
            scrollLeft,
            scrollTop
        })
    }
    // 组件挂载时初始化 WebSocket
    initWebSocket()

    return {
        classes,
        isConnected,
        addClass,
        endClass,
        updateUserCollaboration,
        updateUserMic,
        updateAnnotations,
        updateScroll
    }
})