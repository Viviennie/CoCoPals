package com.tongji.codejourneycolab.codejourneycolabbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.DocumentInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.FileInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Document;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.DocInvitationCodeException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.DocPermissionException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidDocLenException;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.DocumentMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.DocumentService;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sharedb.url}")
    private String sharedbUrl;

    @Override
    public Boolean isOwner(Integer userId, Integer documentId) {
        Integer ownerId = documentMapper.getOwnerId(documentId);
        return ownerId != null && ownerId.equals(userId);
    }

    @Override
    public Boolean isCollaborator(Integer userId, Integer documentId) {
        // * -4242 用于标识sharedb服务器
        if (userId == -4242) return true;
        return documentMapper.isCollaborator(userId, documentId);
    }

    @Override
    public Integer joinCollaborationByCode(Integer userId, String colabCode) {

        Integer documentId = getSharedId(colabCode);

        if(!documentMapper.existsDocument(documentId)) {
            throw new DocPermissionException("No permission to this document.");
        }

        if (!isOwner(userId, documentId) && !isCollaborator(userId, documentId)) { // 获取邀请链接的用户若无权限，应添加权限
            documentMapper.addCollaborator(userId, documentId);
        }

        ///  发送POST请求到协作服务器
        String content = documentMapper.selectById(documentId).getCode();
        createSharedbService(createSharingCode(documentId),content);
        return documentId;
    }

    @Override
    public String joinCollaborationById(Integer userId, Integer documentId) {

        boolean docExists = documentMapper.existsDocument(documentId);
        boolean hasAccess = isOwner(userId, documentId) || isCollaborator(userId, documentId);

        if(!docExists || !hasAccess) {
            throw new DocPermissionException("No permission to this document.");
        }

        ///  发送POST请求到协作服务器
        String content = documentMapper.selectById(documentId).getCode();
        createSharedbService(createSharingCode(documentId),content);
        return createSharingCode(documentId);
    }

    @Override
    public String getContent(Integer userId, Integer documentId) {
        if (!isOwner(userId, documentId) && !isCollaborator(userId, documentId)) {
            throw new DocPermissionException("No permission to this document.");
        }
        Document document = documentMapper.selectById(documentId);
        return document.getCode();
    }

    @Override
    public FileInfoDto getFileInfo(Integer userId, Integer documentId) {
        if (!isOwner(userId, documentId) && !isCollaborator(userId, documentId)) {
            throw new RuntimeException("无权查看文档信息");
        }

        try {
            return documentMapper.getFileInfo(documentId);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("未能获取文档信息");
        }
    }

    @Override
    public void deleteDocument(Integer ownerId, Integer documentId) {

        // 检查是否是文档所有者
        if (!isOwner(ownerId, documentId)) {
            throw new DocPermissionException("No permission to this document.");
        }

        // 删除文档
        documentMapper.deleteDocument(documentId);
    }

    @Override
    public DocumentInfoDto createDocument(Integer ownerId, String title) {
        // 创建默认文档对象
        Document newDocument = new Document();
        newDocument.setOwnerId(ownerId);
        newDocument.setTitle(title);
        newDocument.setCode("");

        // 插入新文档记录并获取生成的文档 ID
        documentMapper.createDocument(newDocument);

        // 返回文档信息
        return documentMapper.getDocumentInfo(newDocument.getId());
    }

    @Override
    public void updateContent(Integer userId, String documentCode, String newContent) {

        Integer documentId  = getSharedId(documentCode);

        if (!isOwner(userId, documentId) && !isCollaborator(userId, documentId)) {
            throw new DocPermissionException("No permission to this document.");
        }

        if(newContent.length()>500000){
            throw new InvalidDocLenException("Document too long. Failed to save.");
        }

        // 更新文档内容
        documentMapper.updateContent(documentId, newContent);
    }

    @Override
    public String getDocumentShareCode(Integer ownerId, Integer documentId) {
        // 检查是否是文档所有者
        if (!isOwner(ownerId, documentId) && !isCollaborator(ownerId, documentId)) {
            throw new DocPermissionException("You are not owner or collaborator of this document.");
        }
        return createSharingCode(documentId);
    }

    @Override
    public List<DocumentInfoDto> getDocumentInfoList(Integer userId) {
        // 调用 Mapper 查询文档信息列表
        List<DocumentInfoDto> documentList = documentMapper.getDocumentInfoListByUserId(userId);

        // 如果没有文档，返回空列表
        if (documentList == null || documentList.isEmpty()) {
            return List.of(); // 返回空列表
        }

        return documentList;
    }

    @Override
    public void createSharedbService(String colabCode, String content) {
        try {
            // 创建一个 Map 来存储 JSON 数据
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("docCode", colabCode);
            requestBody.put("content", content);

            // 使用 Jackson 的 ObjectMapper 将 Map 转换为 JSON 字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建 HttpEntity，包含请求体和头部
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            // 发送 POST 请求
            ResponseEntity<String> result = restTemplate.exchange(sharedbUrl, HttpMethod.POST, entity, String.class);

            // 可以根据需要处理响应结果
            if (result.getStatusCode().is2xxSuccessful()) {
                System.out.println("请求成功：" + result.getBody());
            } else {
                System.out.println("请求失败：" + result.getStatusCode());
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }


    /// 下为与协作码相关的函数

    private Integer getSharedId(String sharingCode)  throws DocInvitationCodeException {
        try {
            String key = "d0_n0t_PUbL1SH_TH1S_K3Y";
            // 补全密钥长度
            key = padKeyToValidLength(key);
            // Initialize Cipher for AES decryption
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secret = new SecretKeySpec(keyBytes, "AES");

            // Decode the sharingCode from Base64
            byte[] decodedBytes = Base64.getDecoder().decode(sharingCode);

            // Decrypt the bytes
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            // Convert the decrypted bytes to a string (the original documentId)
            String documentIdStr = new String(decryptedBytes);

            // Return as an Integer
            return Integer.parseInt(documentIdStr);
        } catch (Exception e) {
            throw new DocInvitationCodeException("Failed to decode: " + e.getMessage());
        }
    }

    private String createSharingCode(Integer documentId) throws DocInvitationCodeException {
        try {
            String data = documentId.toString();
            String key = "d0_n0t_PUbL1SH_TH1S_K3Y";       //key AES-16/24/32byte DES-8byte 3DES-8/16/24byte // DO NOT PUBLISH
            key = padKeyToValidLength(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secret = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] bytes = cipher.doFinal(data.getBytes());
            String sharingCode = Base64.getEncoder().encodeToString(bytes);

            System.out.println(sharingCode);
            return sharingCode;
        } catch (Exception e) {
            throw new DocInvitationCodeException("Failed to encode: " + e.getMessage());
        }
    }

    private static String padKeyToValidLength(String key) {
        // AES 密钥长度要求：16、24 或 32 字节
        int[] validLengths = {16, 24, 32};
        for (int validLength : validLengths) {
            if (key.length() <= validLength) {
                return String.format("%1$-" + validLength + "s", key).replace(' ', '0'); // 使用 '0' 填充
            }
        }
        // 如果密钥超过 32 字节，则截断
        return key.substring(0, 32);
    }


}

