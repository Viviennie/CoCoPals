package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Submission {
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private Integer attemptNum;
    private LocalDateTime submitTime;
    private String language;
    private Integer state;
    private Integer passCount;
    private Double totalTime;
}
