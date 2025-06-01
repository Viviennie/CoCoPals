package com.tongji.codejourneycolab.codejourneycolabbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIModelService {

    @Value("${ai.model.api.url}")
    private String apiUrl;

    @Value("${ai.model.api.key}")
    private String apiKey;

    @Value("${ai.model.api.model}")
    private String modelName;

    private final RestTemplate restTemplate;

    public AIModelService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAnalysis(String prompt) {
        // 1. 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", new Object[] {
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 3. 创建HTTP实体
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 5. 处理响应
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            // 解析响应，具体结构根据硅基流动API的实际返回格式调整
            return extractContentFromResponse(responseBody);
        } else {
            throw new RuntimeException("Failed to get response from SiliconFlow AI: " +
                    response.getStatusCode() + " - " + response.getBody());
        }
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        // 根据硅基流动API的实际返回结构解析内容
        // 假设返回结构与OpenAI类似:
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
        throw new RuntimeException("No valid content in AI response");
    }
}