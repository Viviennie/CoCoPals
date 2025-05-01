package com.tongji.codejourneycolab.codejourneycolabbackend.entity;

import lombok.Data;

@Data
public class QuestionByUser extends Question {
    private String status;

    public void fill(Question question, String status) {
        this.id=question.getId();
        this.title=question.getTitle();
        this.difficulty=question.getDifficulty();
        this.status=status;
    }
}
