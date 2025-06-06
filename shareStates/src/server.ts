import { Server } from "socket.io";
import { createServer } from "http";
import { ClassItem, MessageType } from "./types";

const PORT = 8443;

// 创建 HTTP 服务器
const server = createServer((req, res) => {
    // 处理 CORS 预检请求
    if (req.method === "OPTIONS") {
        res.writeHead(204, {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type",
        });
        res.end();
        return;
    }

    res.writeHead(404);
    res.end();
});

// 创建 Socket.IO 服务器
const io = new Server(server, {
    cors: {
        origin: "*", // 允许所有来源
        methods: ["GET", "POST"],
    },
});

console.log(`Socket.IO server running on ws://localhost:${PORT}`);

// 共享状态
let classes: ClassItem[] = [];

// 广播状态给所有客户端
function broadcastState() {
    io.emit("syncState", { state: classes });
}

io.on("connection", (socket) => {
    console.log("New client connected");

    // 发送当前状态给新连接的客户端
    socket.emit("syncState", { state: classes });

    socket.on("message", (message: MessageType) => {
        try {
            switch (message.type) {
                case "addClass": {
                    console.log("添加课程");
                    const existingClass = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (existingClass) {
                        console.log(`课程已存在，更新用户列表，documentId: ${message.documentId}`);

                        // 更新现有课程的 users
                        existingClass.users = message.users.map((user) => ({
                            ...user,
                            canCollaborate: user.canCollaborate,
                        }));
                    } else {
                        // 如果课程不存在，创建新课程
                        const newClass: ClassItem = {
                            documentId: message.documentId,
                            classId: null,
                            startTime: new Date().toISOString(),
                            endTime: null,
                            users: message.users.map((user) => ({
                                ...user,
                                canCollaborate: user.canCollaborate,
                            })),
                            annotations: null,
                            scrollLeft: 0,
                            scrollTop: 0
                        };
                        classes.push(newClass);
                    }
                    break;
                }

                case "endClass": {
                    console.log("结束课程");
                    const classItem = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (classItem) {
                        classItem.endTime = new Date().toISOString();
                    }
                    break;
                }

                case "updateUserCollaboration": {
                    console.log("更新用户共享权限");
                    const classItem = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (classItem) {
                        const user = classItem.users.find(
                            (u) => u.id === message.userId
                        );
                        if (user) {
                            user.canCollaborate = message.canCollaborate;
                        }
                    }
                    break;
                }

                case "updateUserMic": {
                    console.log("更新用户麦克风权限");
                    const classItem = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (classItem) {
                        const user = classItem.users.find(
                            (u) => u.id === message.userId
                        );
                        if (user) {
                            user.micEnabled = message.micEnabled;
                        }
                    }
                    break;
                }

                case "updateAnnotations": {
                    console.log("更新批注");
                    const classItem = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (classItem) {
                        classItem.annotations = message.annotations;
                    }
                    break;
                }

                case "updateScroll": {
                    console.log("更新翻页情况");
                    const classItem = classes.find(
                        (c) => c.documentId === message.documentId
                    );
                    if (classItem) {
                        classItem.scrollLeft = message.scrollLeft;
                        classItem.scrollTop = message.scrollTop;
                    }
                    break;
                }
            }

            broadcastState();
        } catch (error) {
            console.error("Error processing message:", error);
        }
    });

    socket.on("disconnect", () => {
        console.log("Client disconnected");
    });
});

// 启动服务器
server.listen(PORT, () => {
    console.log(`Server is listening on http://localhost:${PORT}`);
});