var backend = require("./sharedb_singleton").sharedb;
const config = require('config');
const axios = require('axios');

// 定义每个文档的最大空闲时间（以毫秒为单位）
const MAX_IDLE_TIME = config.get('timer.max_idle_time');

// 文档服务地址
const document_service_url = config.get('document_service_url');

// 存储每个文档对象及其最后活动时间
let docActivityMap = new Map();

// 创建并初始化文档，添加op回调更新活动时间
function createDoc(doc) {
	// *必须要订阅文档才能接收到op事件
	doc.subscribe();
	// 将文档对象放入活动字典并记录当前时间
	docActivityMap.set(doc, Date.now());
	// 为该文档添加 op 事件回调
	doc.on("op", function (op, source) {
		// 每当文档有操作，更新活动时间
		docActivityMap.set(doc, Date.now());
	});
}

// 定时检查并关闭空闲文档
function checkForIdleDocuments() {
	const currentTime = Date.now();
	console.log("Running documents: ", docActivityMap.size);

	// 遍历所有已存储的文档及其活动时间
	docActivityMap.forEach((lastActivity, doc) => {
		if (currentTime - lastActivity > MAX_IDLE_TIME) {
			// 超过最大空闲时间，关闭文档
			console.log(`Document with ID ${doc.id} has been idle for too long, closing...`);
			doc.unsubscribe();
			// *持久化文档
			persistDocument(doc);
			// 释放文档资源
			doc.del(function (err) {
				// *删除活动记录
				docActivityMap.delete(doc);
				if (err) {
					console.error('Error closing document:', err);
				} else {
					console.log(`Document with ID ${doc.id} closed and deleted successfully.`);
				}
			});
		}
	});
}

// 调用远程接口保存文档
function persistDocument(doc) {
	var docCode = doc.id;
	var content = doc.data.content;
	console.log('1111')
	axios.post(document_service_url, {
		documentCode: docCode,
		code: content
	}, {
		headers: {	// *手动发放的永久token，id为-4242
			'Authorization': `Bearer ${config.get('jwt_token')}`
		}
	})
		.then(response => {
			console.log(response.status);
			console.log('Document saved successfully.');
		})
		.catch(error => {
			console.error('Error saving document:', error);
		});
		console.log('11122221')
}

// 保存所有文档
function persistAllDocument() {
	console.log("Persisting all documents...");
	// 遍历所有文档，持久化
	docActivityMap.forEach((lastActivity, doc) => {
		persistDocument(doc);
	});
}

// 导出方法
module.exports = {
	createDoc: createDoc,
	checkForIdleDocuments: checkForIdleDocuments,
	persistAllDocument: persistAllDocument
}
