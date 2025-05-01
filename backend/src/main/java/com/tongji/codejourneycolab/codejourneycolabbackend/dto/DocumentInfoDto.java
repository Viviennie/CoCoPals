package com.tongji.codejourneycolab.codejourneycolabbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

// 请求文章列表时的返回类型，不包含文档的内容。
@Data
public class DocumentInfoDto {
    private Integer id;
    private Integer ownerId;
    private String ownerName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime lastModifiedTime;
    private String title;
}
