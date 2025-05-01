package com.tongji.codejourneycolab.codejourneycolabbackend.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String identity;
    private String password;
}
