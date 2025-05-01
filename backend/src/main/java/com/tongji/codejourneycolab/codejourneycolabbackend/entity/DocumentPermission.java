package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("document_permission") 
public class DocumentPermission {

    @TableField("document_id") 
    private Integer documentId;

    @TableField("user_id") 
    private Integer userId;

    @TableField("permission") 
    private String permission; // 权限级别 (例如: "view", "edit", "admin")

}