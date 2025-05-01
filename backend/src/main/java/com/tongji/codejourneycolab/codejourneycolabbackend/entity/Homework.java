package com.tongji.codejourneycolab.codejourneycolabbackend.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("homework")
public class Homework{
    // 对应 class_id 字段
    @TableField("class_id")
    private Integer classId;

    // 对应 question id 字段
    @TableField("question_id")
    private Integer questionId;

    // 对应 title 字段
    @TableField("due_time")
    private Date dueTime;

    @TableField(exist = false)
    private String title;

    public Homework(Integer classId, Integer questionId, Date dueTime) {
        this.classId = classId;
        this.questionId = questionId;
        this.dueTime = dueTime;
    }
}
