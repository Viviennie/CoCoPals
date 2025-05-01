package com.tongji.codejourneycolab.codejourneycolabbackend.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class HomeworkDto {
    private Integer problemId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date dueTime;
    private String title;
    private String status;
}
