package com.tongji.codejourneycolab.codejourneycolabbackend.dto;

import lombok.Data;

@Data
public class LearningAnalysisResponseDto {
    private String analysisText;
    private Object chartData; // 可以包含图表需要的数据结构
}
