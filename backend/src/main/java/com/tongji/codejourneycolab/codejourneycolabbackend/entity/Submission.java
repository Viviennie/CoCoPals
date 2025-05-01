package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Submission {
    protected int attemptNum;
    protected LocalDateTime submitTime;
    protected String language;
    protected int state;
    protected int passCount;
    protected double totalTime;
}
