package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // 导入 LocalDateTime 用于映射 DATETIME

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("teaching_record") 
public class TeachingRecord {

    @TableId(type = IdType.AUTO) // 设置为自增
    private Integer id;

    @TableField("class_id") 
    private Integer classId;

    @TableField("begin_time") 
    private LocalDateTime beginTime; // 使用 LocalDateTime 对应 DATETIME

    @TableField("end_time")
    private LocalDateTime endTime; // 使用 LocalDateTime 对应 DATETIME

    @TableField("attend_num") 
    private Integer attendNum;

}