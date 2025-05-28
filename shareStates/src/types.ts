export interface User {
    id: number
    name: string
    micEnabled: boolean
    canCollaborate: boolean
}

export interface ClassItem {
    documentId: number
    classId: string | null
    startTime: string  // 使用字符串便于序列化
    endTime: string | null
    users: User[]
    annotations: Blob | null
    scrollLeft: number
    scrollTop: number
}

export type MessageType = 
    | { type: 'addClass', documentId: number, users: User[] }
    | { type: 'endClass', documentId: number }
    | { type: 'updateUserCollaboration', documentId: number, userId: number, canCollaborate: boolean }
    | { type: 'syncState', state: ClassItem[] }
    | { type: 'updateUserMic', documentId: number, userId: number, micEnabled: boolean }
    | { type: 'updateAnnotations', documentId: number, annotations: Blob }
    | { type: 'updateScroll', documentId: number, scrollLeft: number, scrollTop: number }