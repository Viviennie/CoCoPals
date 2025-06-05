package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningAnalysisCache {
    @TableId
    private Integer id;
    private Integer userId;
    private String analysisText;
    private String chartData; // 存储JSON字符串
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}