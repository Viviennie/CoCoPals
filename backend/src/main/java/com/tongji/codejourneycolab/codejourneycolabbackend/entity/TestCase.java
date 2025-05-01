package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.Data;

@Data
public class TestCase {
    private String id;
    private Integer questionId;
    private String input;
    private String output;
}
