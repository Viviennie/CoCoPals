package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import static com.baomidou.mybatisplus.annotation.IdType.AUTO;

@Data
public class User {
    @TableId(type = AUTO)
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
}
