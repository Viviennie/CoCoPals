package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.LearningAnalysisResponseDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.LearningAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-analysis")
public class LearningAnalysisController {

    private final LearningAnalysisService learningAnalysisService;

    public LearningAnalysisController(LearningAnalysisService learningAnalysisService) {
        this.learningAnalysisService = learningAnalysisService;
    }

    @GetMapping("/by-user-id")
    public LearningAnalysisResponseDto getLearningAnalysisByUserId(@RequestHeader("userid") Integer id) {
        return learningAnalysisService.getLearningAnalysisByUserId(id);
    }

    @GetMapping
    public LearningAnalysisResponseDto getLearningAnalysis(@RequestHeader("Authorization") String token) {
        return learningAnalysisService.getLearningAnalysis(token);
    }

    @GetMapping("/generate")
    public LearningAnalysisResponseDto generateLearningAnalysis(@RequestHeader("Authorization") String token) {
        return learningAnalysisService.generateLearningAnalysis(token);
    }
}