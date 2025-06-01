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
    private Integer questionId;
    private Integer userId;

    public SubmissionDetail(int attemptNum, LocalDateTime submitTime, String language, int state, int passCount, double totalTime, String code, String firstFailureOutput){
        this.setAttemptNum(attemptNum);
        this.setSubmitTime(submitTime);
        this.setLanguage(language);
        this.setState(state);
        this.setPassCount(passCount);
        this.setTotalTime(totalTime);
        this.code = code;
        this.firstFailureOutput = firstFailureOutput;
    }
}
