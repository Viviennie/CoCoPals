package com.tongji.codejourneycolab.codejourneycolabbackend.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileInfoDto {
    private String title;
    private String ownerName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String createTime;

}
