package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

@Data
public class Document {
    @TableId(type = AUTO)
    private Integer id;
    private Integer ownerId;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;
    private String title;
    private String code;
}
