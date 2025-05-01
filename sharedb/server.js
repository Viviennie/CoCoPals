var http = require('http');
var express = require('express');
var WebSocket = require('ws');
var WebSocketJSONStream = require('@teamwork/websocket-json-stream');
var timer_functions = require('./timer_tasks');  // 导入独立的文档管理模块
var cors = require('cors');
const config = require('config');

const IDLE_CHECK_INTERVAL = config.get('timer.idle_check_interval');
const AUTO_SAVE_INTERVAL = config.get('timer.auto_save_interval');
const port = config.get('server.port');

var backend = require("./sharedb_singleton").sharedb;
var connection = backend.connect();

var app = express();
app.use(express.json());

// ! just for testing
app.use(express.static('static'));

app.use(cors());

// POST /share/opendocument: 启动文档服务
app.post('/share/opendocument', (req, res) => {
    const { userId, docCode, content } = req.body;

    if (!userId || !docCode || content === null) {
        return res.status(400).send({ message: 'userId, docCode and content are required' });
    }
	console.log("received")
	console.log('userId:',userId);
	console.log('docCode:',docCode);
	console.log('content:',content,'\n');
    var doc = connection.get("shared-doc", docCode);

    doc.fetch(function (err) {
        if (err) {
            console.error('Error fetching document:', err);
            return res.status(500).send({ message: 'Error fetching document', error: err });
        }

        if (doc.type === null) {
            // 新建文档
            doc.create({ content: content, users: { [userId]: true } }, function (err) {
                if (err) {
                    console.error('Error creating document:', err);
                    return res.status(500).send({ message: 'Error creating document', error: err });
                }

                timer_functions.createDoc(doc);
                res.send({ message: "Document created successfully", docCode });
            });
        } else {
            // 如果文档已经存在，先删除该文档，然后再次尝试创建
            console.log('Document already exists. Deleting and recreating...');
            doc.del(function (deleteErr) {
                if (deleteErr) {
                    console.error('Error deleting document:', deleteErr);
                    return res.status(500).send({ message: 'Error deleting document', error: deleteErr });
                }

                // 再次尝试创建文档
                doc.create({ content: content, users: { [userId]: true } }, function (createErr) {
                    if (createErr) {
                        console.error('Error creating document after deletion:', createErr);
                        return res.status(500).send({ message: 'Error creating document after deletion', error: createErr });
                    }

                    console.log('doc re - created:', doc.data);
                    timer_functions.createDoc(doc);
                    res.send({ message: "Document re - created successfully", docCode });
                });
            });
        }
    });
});

app.post('/share/getUsersByDocCode', (req, res) => {
    const { docCode } = req.body;

    if (!docCode) {
        return res.status(400).send({ message: 'docCode is required' });
    }

    var doc = connection.get("shared-doc", docCode);

    doc.fetch(function (err) {
        if (err) {
            console.error('Error fetching document:', err);
            return res.status(500).send({ message: 'Error fetching document', error: err });
        }

        if (doc.type === null) {
            return res.status(404).send({ message: 'Document not found' });
        }

        const users = Object.keys(doc.data.users);
        res.send({ docCode, users });
    });
});

var server = http.createServer(app);

var wss = new WebSocket.Server({ server: server });
wss.on('connection', function (ws) {
	var stream = new WebSocketJSONStream(ws);
	backend.listen(stream);
});

// 定时检查所有文档
setInterval(() => {
	timer_functions.checkForIdleDocuments();  // 调用管理文档的检查方法
}, IDLE_CHECK_INTERVAL);

// 定时保存所有文档
setInterval(() => {
	timer_functions.persistAllDocument();
}, AUTO_SAVE_INTERVAL);

server.listen(port, () => {
	console.log(`Server started on port ${port}`);
});
