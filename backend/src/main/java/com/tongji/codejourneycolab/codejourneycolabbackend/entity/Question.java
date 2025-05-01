package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    protected int id;
    protected String title;
    protected int difficulty;
    private String description;
    private String tag;
}

