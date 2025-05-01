package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionDetail extends Question {
    private String description;
    private List<TestCase> tests;
}
