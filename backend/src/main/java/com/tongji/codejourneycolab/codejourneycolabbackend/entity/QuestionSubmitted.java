package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitted extends Question {
    private LocalDateTime submitTime;
    private int state;
}
