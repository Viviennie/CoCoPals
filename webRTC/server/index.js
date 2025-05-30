const WebSocket = require('ws');
const wss = new WebSocket.Server({ host: '0.0.0.0', port: 3000 });

const rooms = {}; // roomId -> [ws, ws, ...]

wss.on('connection', (ws) => {
    console.log('New client connected');

    ws.on('message', (msg) => {
        let data;
        try {
            data = JSON.parse(msg);
        } catch (e) {
            console.error('Failed to parse message:', msg, e);
            return;
        }

        const { type, roomId, payload } = data;

        try {
            switch (type) {
                case 'join':
                    ws.roomId = roomId;
                    rooms[roomId] = rooms[roomId] || [];
                    rooms[roomId].push(ws);
                    console.log(`Client joined room: ${roomId}, total clients: ${rooms[roomId].length}`);
                    broadcast(ws, roomId, { type: 'new-peer' });
                    break;

                case 'signal':
                    if (!roomId) {
                        console.warn('Signal message missing roomId');
                        return;
                    }
                    broadcast(ws, roomId, { type: 'signal', payload });
                    break;

                case 'leave':
                    if (rooms[roomId]) {
                        rooms[roomId] = rooms[roomId].filter(client => client !== ws)
                        broadcast(ws, roomId, { type: 'peer-left' })
                    }
                    break

                default:
                    console.warn('Unknown message type:', type);
                    break;
            }
        } catch (e) {
            console.error('Error handling message:', e);
        }
    });

    ws.on('close', () => {
        const room = rooms[ws.roomId];
        if (room) {
            rooms[ws.roomId] = room.filter((client) => client !== ws);
            console.log(`Client left room: ${ws.roomId}, remaining clients: ${rooms[ws.roomId].length}`);
            broadcast(ws, ws.roomId, { type: 'peer-left' });
            // 如果房间没人了，清理房间
            if (rooms[ws.roomId].length === 0) {
                delete rooms[ws.roomId];
                console.log(`Room ${ws.roomId} deleted (empty)`);
            }
        }
    });

    ws.on('error', (error) => {
        console.error('WebSocket error:', error);
    });
});

function broadcast(sender, roomId, message) {
    const room = rooms[roomId];
    if (!room) {
        console.warn(`Broadcast failed, room ${roomId} does not exist`);
        return;
    }
    room.forEach((client) => {
        if (client !== sender && client.readyState === WebSocket.OPEN) {
            try {
                client.send(JSON.stringify(message));
            } catch (e) {
                console.error('Failed to send message to client:', e);
            }
        }
    });
}

console.log('WebSocket signaling server running on ws://localhost:3000');
