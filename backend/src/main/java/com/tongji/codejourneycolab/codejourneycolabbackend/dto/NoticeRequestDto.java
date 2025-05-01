package com.tongji.codejourneycolab.codejourneycolabbackend.dto;

import lombok.Data;

@Data
public class NoticeRequestDto {
    private Integer classId;
    private String title;
    private String content;
}
