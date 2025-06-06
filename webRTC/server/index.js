const WebSocket = require('ws');
const { v4: uuidv4 } = require('uuid');

const wss = new WebSocket.Server({ host: '0.0.0.0', port: 3000 });

const rooms = {}; // roomId -> [{ ws, clientId }, ...]

wss.on('connection', (ws) => {
    ws.clientId = uuidv4(); // 给每个连接生成唯一 ID
    console.log(`New client connected: ${ws.clientId}`);

    ws.on('message', (msg) => {
        let data;
        try {
            data = JSON.parse(msg);
        } catch (e) {
            console.error('Failed to parse message:', msg, e);
            return;
        }

        const { type, roomId, payload } = data;

        switch (type) {
            case 'join':
                ws.roomId = roomId;
                if (!rooms[roomId]) rooms[roomId] = [];
                rooms[roomId].push({ ws, clientId: ws.clientId });
                console.log(`Client ${ws.clientId} joined room: ${roomId}, total clients: ${rooms[roomId].length}`);

                // 告诉新加入的客户端当前房间其他所有人的 clientId
                const otherClients = rooms[roomId].filter(c => c.clientId !== ws.clientId).map(c => c.clientId);
                ws.send(JSON.stringify({ type: 'peers', peers: otherClients }));

                // 通知房间内其他客户端新客户端加入，带上 clientId
                broadcast(ws, roomId, { type: 'new-peer', clientId: ws.clientId });
                break;

            case 'signal':
                if (!roomId) {
                    console.warn('Signal message missing roomId');
                    return;
                }
                // 广播信令消息，同时带上发送者 clientId
                broadcast(ws, roomId, { type: 'signal', clientId: ws.clientId, payload });
                break;

            case 'leave':
                if (rooms[roomId]) {
                    rooms[roomId] = rooms[roomId].filter(client => client.ws !== ws);
                    broadcast(ws, roomId, { type: 'peer-left', clientId: ws.clientId });
                }
                break;

            default:
                console.warn('Unknown message type:', type);
                break;
        }
    });

    ws.on('close', () => {
        const room = rooms[ws.roomId];
        if (room) {
            rooms[ws.roomId] = room.filter((client) => client.ws !== ws);
            console.log(`Client ${ws.clientId} left room: ${ws.roomId}, remaining clients: ${rooms[ws.roomId].length}`);
            broadcast(ws, ws.roomId, { type: 'peer-left', clientId: ws.clientId });
            if (rooms[ws.roomId].length === 0) {
                delete rooms[ws.roomId];
                console.log(`Room ${ws.roomId} deleted (empty)`);
            }
        }
    });

    ws.on('error', (error) => {
        console.error('WebSocket error:', error);
    });

    // 连接时主动告诉客户端自己的 clientId，方便前端保存
    ws.send(JSON.stringify({ type: 'init', clientId: ws.clientId }));
});

function broadcast(sender, roomId, message) {
    const room = rooms[roomId];
    if (!room) {
        console.warn(`Broadcast failed, room ${roomId} does not exist`);
        return;
    }
    room.forEach(({ ws }) => {
        if (ws !== sender && ws.readyState === WebSocket.OPEN) {
            try {
                ws.send(JSON.stringify(message));
            } catch (e) {
                console.error('Failed to send message to client:', e);
            }
        }
    });
}

console.log('WebSocket signaling server running on ws://localhost:3000');
