package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("class") // 请将此处替换为实际的表名
public class Clas {
    // 对应 id 字段，是主键且自增
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("classname")
    private String classname;

    @TableField("teacher_id")
    private Integer teacherId;

    @TableField("capacity")
    private Integer capacity;

    @TableField("join_code")
    private String joinCode;

    public Clas(Integer id,String classname){
        this.classname = classname;
        this.teacherId = id;
        this.capacity = null;
        this.joinCode = null;
    }
}
