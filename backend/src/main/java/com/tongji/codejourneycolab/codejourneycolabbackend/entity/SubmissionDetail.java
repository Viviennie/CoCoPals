package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionDetail extends Submission {
    private String code;
    private String firstFailureOutput;
    private int questionId;
    private int userId;

    public SubmissionDetail(int attemptNum, LocalDateTime submitTime, String language, int state, int passCount, double totalTime, String code, String firstFailureOutput){
        this.attemptNum = attemptNum;
        this.submitTime = submitTime;
        this.language = language;
        this.state = state;
        this.passCount = passCount;
        this.totalTime = totalTime;
        this.code = code;
        this.firstFailureOutput = firstFailureOutput;
    }
}
