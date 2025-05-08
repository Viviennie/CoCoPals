import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
    id: number
    name: string
    micEnabled: boolean
    canCollaborate: boolean
}

interface ClassItem {
    documentId: Number
    classId: string | null
    startTime: Date
    endTime: Date | null
    users: User[]
}

export const useClassStore = defineStore('class', () => {
    const classes = ref<ClassItem[]>([])

    // 添加新课程
    const addClass = (documentId: number, users: User[]) => {
        // 检查是否已存在相同documentId的课程
        const existingClass = classes.value.find(c => c.documentId === documentId);
        if (existingClass) {
            console.log(`课程已存在，documentId: ${documentId}`);
            return;
        }
        const newClass: ClassItem = {
            documentId: documentId,
            classId: null, // 暂时设为null
            startTime: new Date(),
            endTime: null,
            users: users.map(user => ({
                ...user,
                canCollaborate: user.canCollaborate // 保持原有值
            }))
        }
        classes.value.push(newClass)
    }

    // 结束课程
    const endClass = (documentId: number) => {
        const classItem = classes.value.find(c => c.documentId === documentId)
        if (classItem) {
            classItem.endTime = new Date()
        }
    }

    // 更新用户协作权限
    const updateUserCollaboration = (documentId: number, userId: number, canCollaborate: boolean) => {
        const classItem = classes.value.find(c => c.documentId === documentId)
        if (classItem) {
            const user = classItem.users.find(u => u.id === userId)
            if (user) {
                user.canCollaborate = canCollaborate
            }
        }
    }

    return {
        classes,
        addClass,
        endClass,
        updateUserCollaboration
    }
})