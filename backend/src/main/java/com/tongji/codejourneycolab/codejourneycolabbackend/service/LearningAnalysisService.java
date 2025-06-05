package com.tongji.codejourneycolab.codejourneycolabbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.LearningAnalysisResponseDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.*;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.LearningAnalysisCacheMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.QuestionMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.SubmissionMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import com.tongji.codejourneycolab.codejourneycolabbackend.security.JwtTokenProvider;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class LearningAnalysisService {

    private final UserMapper userMapper;
    private final SubmissionMapper submissionMapper;
    private final QuestionMapper questionMapper;
    private final LearningAnalysisCacheMapper cacheMapper;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper
    private final AIModelService aiModelService;
    private final JwtTokenProvider jwtTokenProvider;

    public LearningAnalysisService(UserMapper userMapper,
                                   SubmissionMapper submissionMapper,
                                   QuestionMapper questionMapper,
                                   LearningAnalysisCacheMapper cacheMapper,
                                   ObjectMapper objectMapper,
                                   AIModelService aiModelService,
                                   JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.submissionMapper = submissionMapper;
        this.questionMapper = questionMapper;
        this.cacheMapper = cacheMapper;
        this.objectMapper = objectMapper;
        this.aiModelService = aiModelService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // get打头的是用户进入页面的获取报告，不是用户点击刷新强制重新生成报告
    public LearningAnalysisResponseDto getLearningAnalysis(String token) {
        // 1. 根据token获取用户ID
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Integer userId = jwtTokenProvider.tryGetIdFromToken(token);
            // 调用原有逻辑
            return getLearningAnalysisByUserId(userId);
        }
        throw new RuntimeException("Invalid token");
    }
    public LearningAnalysisResponseDto getLearningAnalysisByUserId(Integer userId) {
        // 先检查缓存
        LearningAnalysisCache cache = cacheMapper.findByUserId(userId);

        // 如果有缓存且在有效期内(7天内)，直接返回
        if (cache != null && cache.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(7))) {
            try {
                Map<String, Object> chartData = objectMapper.readValue(cache.getChartData(), new TypeReference<Map<String, Object>>() {});
                LearningAnalysisResponseDto response = new LearningAnalysisResponseDto();
                response.setAnalysisText(cache.getAnalysisText());
                response.setChartData(chartData);
                return response;
            } catch (JsonProcessingException e) {
                // 继续生成新的分析
            }
        }

        // 1. 获取用户的所有提交记录
        List<Submission> submissions = submissionMapper.findByUserId(userId);

        // 2. 获取相关题目信息
        Map<Integer, Question> questionMap = getQuestionMap(submissions);

        // 3. 计算统计指标
        Map<String, Object> statistics = calculateStatistics(submissions, questionMap);

        // 4. 生成大模型提示词
        String prompt = generatePrompt(userId, statistics);

        // 5. 调用大模型获取分析结果
        String analysisText = aiModelService.getAnalysis(prompt);

        // 6. 构建响应
        LearningAnalysisResponseDto response = new LearningAnalysisResponseDto();
        response.setAnalysisText(analysisText);
        response.setChartData(statistics);

        updateAnalysisCache(userId, analysisText, statistics);

        return response;
    }

    public LearningAnalysisResponseDto generateLearningAnalysis(String token) {
        // 1. 根据token获取用户ID
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Integer userId = jwtTokenProvider.tryGetIdFromToken(token);
            // 调用原有逻辑
            return generateLearningAnalysisByUserId(userId);
        }
        throw new RuntimeException("Invalid token");
    }
    public LearningAnalysisResponseDto generateLearningAnalysisByUserId(Integer userId) {
        // 1. 获取用户的所有提交记录
        List<Submission> submissions = submissionMapper.findByUserId(userId);

        // 2. 获取相关题目信息
        Map<Integer, Question> questionMap = getQuestionMap(submissions);

        // 3. 计算统计指标
        Map<String, Object> statistics = calculateStatistics(submissions, questionMap);

        // 4. 生成大模型提示词
        String prompt = generatePrompt(userId, statistics);

        // 5. 调用大模型获取分析结果
        String analysisText = aiModelService.getAnalysis(prompt);

        // 6. 构建响应
        LearningAnalysisResponseDto response = new LearningAnalysisResponseDto();
        response.setAnalysisText(analysisText);
        response.setChartData(statistics);

        updateAnalysisCache(userId, analysisText, statistics);

        return response;
    }

    private void updateAnalysisCache(Integer userId, String analysisText, Map<String, Object> chartData) {
        try {
            String chartDataJson = objectMapper.writeValueAsString(chartData);

            LearningAnalysisCache cache = new LearningAnalysisCache();
            cache.setUserId(userId);
            cache.setAnalysisText(analysisText);
            cache.setChartData(chartDataJson);

            // 使用MyBatis-Plus的saveOrUpdate方法
            LearningAnalysisCache existing = cacheMapper.findByUserId(userId);
            if (existing != null) {
                cache.setId(existing.getId());
                cacheMapper.updateById(cache);
            } else {
                cacheMapper.insert(cache);
            }
        } catch (JsonProcessingException e) {
        }
    }

    private Integer getUserIdFromToken(String token) {
        // 移除Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // User user = userMapper.selectById(userIdFromToken(token));
        // if (user == null) throw new RuntimeException("User not found");
        // return user.getId();
        return 0;
    }

    private Map<Integer, Question> getQuestionMap(List<Submission> submissions) {
        Set<Integer> questionIds = submissions.stream()
                .map(Submission::getQuestionId)
                .collect(Collectors.toSet());

        // 使用QuestionMapper批量获取题目信息
        return questionIds.stream()
                .map(questionMapper::getQuestionById)
                .collect(Collectors.toMap(Question::getId, q -> q));
    }

    private Map<String, Object> calculateStatistics(List<Submission> submissions, Map<Integer, Question> questionMap) {
        Map<String, Object> stats = new HashMap<>();

        // 1. 总体统计
        stats.put("totalSubmissions", submissions.size());
        stats.put("successRate", calculateSuccessRate(submissions));

        // 2. 错误类型分析
        stats.put("errorAnalysis", calculateErrorAnalysis(submissions, questionMap));

        // 3. 按题目难度统计
        stats.put("difficultyStats", calculateDifficultyStats(submissions, questionMap));

        // 4. 按题目tag统计（知识点统计）
        stats.put("knowledgeStats", calculateKnowledgeStats(submissions, questionMap));

        // 5. 时间趋势分析
        stats.put("timeTrend", calculateTimeTrend(submissions));

        return stats;
    }

    private double calculateSuccessRate(List<Submission> submissions) {
        long successCount = submissions.stream()
                .filter(s -> s.getState() == 1) // 假设1表示成功
                .count();
        return submissions.isEmpty() ? 0 : (double) successCount / submissions.size();
    }

    private Map<Integer, Map<String, Object>> calculateErrorAnalysis(List<Submission> submissions, Map<Integer, Question> questionMap) {
        // 按错误类型分组
        Map<Integer, List<Submission>> byErrorType = submissions.stream()
                .filter(s -> s.getState() != 1) // 只统计错误提交
                .collect(Collectors.groupingBy(Submission::getState));

        Map<Integer, Map<String, Object>> result = new HashMap<>();

        byErrorType.forEach((errorType, subList) -> {
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("count", subList.size());
            errorStats.put("percentage", (double) subList.size() / submissions.size());

            // 关联知识点分析
            Map<String, Long> errorByKnowledge = subList.stream()
                    .collect(Collectors.groupingBy(
                            s -> questionMap.get(s.getQuestionId()).getTag(),
                            Collectors.counting()
                    ));
            errorStats.put("knowledgeDistribution", errorByKnowledge);

            result.put(errorType, errorStats);
        });

        return result;
    }

    private Map<Integer, Map<String, Object>> calculateDifficultyStats(List<Submission> submissions, Map<Integer, Question> questionMap) {
        // 按难度分组统计
        Map<Integer, List<Submission>> byDifficulty = submissions.stream()
                .collect(Collectors.groupingBy(s -> questionMap.get(s.getQuestionId()).getDifficulty()));

        Map<Integer, Map<String, Object>> result = new HashMap<>();

        byDifficulty.forEach((difficulty, subList) -> {
            Map<String, Object> diffStats = new HashMap<>();
            diffStats.put("count", subList.size());
            diffStats.put("successRate", calculateSuccessRate(subList));
            diffStats.put("avgAttempts", calculateAverageAttempts(subList));
            result.put(difficulty, diffStats);
        });

        return result;
    }

    private Map<String, Map<String, Object>> calculateKnowledgeStats(List<Submission> submissions, Map<Integer, Question> questionMap) {
        Map<String, List<Submission>> byTag = submissions.stream()
                .collect(Collectors.groupingBy(s -> questionMap.get(s.getQuestionId()).getTag()));

        Map<String, Map<String, Object>> result = new HashMap<>();

        byTag.forEach((tag, subList) -> {
            Map<String, Object> tagStats = new HashMap<>();

            // 基础统计
            tagStats.put("totalAttempts", subList.size());
            tagStats.put("successRate", calculateSuccessRate(subList));
            tagStats.put("avgAttempts", calculateAverageAttempts(subList));

            // 错误类型分布
            Map<Integer, Long> errorDistribution = subList.stream()
                    .filter(s -> s.getState() != 1)
                    .collect(Collectors.groupingBy(
                            Submission::getState,
                            Collectors.counting()
                    ));
            tagStats.put("errorDistribution", errorDistribution);

            // 新增：题目标题分析
            Map<String, Long> titleDistribution = subList.stream()
                    .collect(Collectors.groupingBy(
                            s -> questionMap.get(s.getQuestionId()).getTitle(),
                            Collectors.counting()
                    ));
            tagStats.put("titleDistribution", titleDistribution);

            // 新增：高频错误题目
            List<String> problematicTitles = subList.stream()
                    .filter(s -> s.getState() != 1)
                    .collect(Collectors.groupingBy(
                            s -> questionMap.get(s.getQuestionId()).getTitle(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3) // 取错误最多的3个题目
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            tagStats.put("problematicTitles", problematicTitles);

            result.put(tag, tagStats);
        });

        return result;
    }

    private Map<String, Map<String, Object>> calculateTagStats(List<Submission> submissions, Map<Integer, Question> questionMap) {
        // 按tag分组统计
        Map<String, List<Submission>> byTag = submissions.stream()
                .collect(Collectors.groupingBy(s -> questionMap.get(s.getQuestionId()).getTag()));

        Map<String, Map<String, Object>> result = new HashMap<>();

        byTag.forEach((tag, subList) -> {
            Map<String, Object> tagStats = new HashMap<>();
            tagStats.put("count", subList.size());
            tagStats.put("successRate", calculateSuccessRate(subList));
            tagStats.put("avgAttempts", calculateAverageAttempts(subList));
            result.put(tag, tagStats);
        });

        return result;
    }

    private Map<String, Object> calculateTimeTrend(List<Submission> submissions) {
        // 按时间分组统计提交情况和成功率
        // 简化为按周分组
        Map<String, Object> timeTrend = new HashMap<>();
        // 实际实现可能需要更复杂的时间分组逻辑
        return timeTrend;
    }

    private List<String> identifyCommonErrors(List<Submission> submissions) {
        // 分析常见错误模式
        List<String> commonErrors = new ArrayList<>();
        // 实际实现可能需要分析firstFailureOutput字段
        return commonErrors;
    }

    private double calculateAverageAttempts(List<Submission> submissions) {
        // 计算平均尝试次数
        return submissions.stream()
                .mapToInt(Submission::getAttemptNum)
                .average()
                .orElse(0);
    }

    private String generatePrompt(Integer userId, Map<String, Object> statistics) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位经验丰富的编程教育专家，请根据以下学生学习数据生成详细的分析报告和建议。（不必重复你的人设，也不必回答我\"好的，下面是...\"这种话，不要提示我\"如需进一步分析或提供具体题目代码参考，请告知！\"这种话，只要单纯给我生成报告即可，不要表格）\n");
        prompt.append("格式要求如下：\n");
        prompt.append("1. 总体学习情况概述（300字以内）\n");
        prompt.append("2. 各知识点掌握情况分析（按知识点分类）\n");
        prompt.append("3. 常见错误模式分析（根据错误类型和具体题目）\n");
        prompt.append("4. 针对性改进建议（具体到知识点和题目）\n");
        prompt.append("5. 推荐下一步学习计划（建议若干个具体题目类型或类似题目，只要推荐重点练习的知识点，不要推荐leetcode等具体题目）\n\n");

        // prompt.append("学生ID: ").append(userId).append("\n\n");
        prompt.append("=== 学习统计数据 ===\n");

        // 1. 总体统计
        prompt.append("1. 总体统计:\n");
        prompt.append("- 总提交次数: ").append(statistics.get("totalSubmissions")).append("\n");
        prompt.append("- 总体成功率: ").append(String.format("%.2f%%", (double)statistics.get("successRate") * 100)).append("\n\n");

        // 2. 错误分析
        prompt.append("2. 错误分析:\n");
        Map<Integer, Map<String, Object>> errorAnalysis = (Map<Integer, Map<String, Object>>) statistics.get("errorAnalysis");
        errorAnalysis.forEach((errorType, stats) -> {
            String errorName = getErrorName(errorType);
            prompt.append("- ").append(errorName).append(": ")
                    .append(stats.get("count")).append("次 (")
                    .append(String.format("%.1f%%", (double)stats.get("percentage") * 100)).append(")\n");

            Map<String, Long> knowledgeDist = (Map<String, Long>) stats.get("knowledgeDistribution");
            if (!knowledgeDist.isEmpty()) {
                prompt.append("  主要出现在: ");
                knowledgeDist.forEach((knowledge, count) -> {
                    prompt.append(knowledge).append("(").append(count).append("次) ");
                });
                prompt.append("\n");
            }
        });
        prompt.append("\n");

        // 3. 知识点统计（包含题目标题信息）
        prompt.append("3. 知识点掌握情况:\n");
        Map<String, Map<String, Object>> knowledgeStats = (Map<String, Map<String, Object>>) statistics.get("knowledgeStats");
        knowledgeStats.forEach((knowledge, stats) -> {
            prompt.append("- ").append(knowledge).append(": ")
                    .append("提交").append(stats.get("totalAttempts")).append("次, ")
                    .append("成功率").append(String.format("%.1f%%", (double)stats.get("successRate") * 100)).append(", ")
                    .append("平均尝试").append(String.format("%.1f", stats.get("avgAttempts"))).append("次\n");

            // 错误类型分布
            Map<Integer, Long> errorDist = (Map<Integer, Long>) stats.get("errorDistribution");
            if (!errorDist.isEmpty()) {
                prompt.append("  主要错误: ");
                errorDist.forEach((errorType, count) -> {
                    prompt.append(getErrorName(errorType)).append("(").append(count).append("次) ");
                });
                prompt.append("\n");
            }

            // 新增：高频错误题目
            List<String> problematicTitles = (List<String>) stats.get("problematicTitles");
            if (!problematicTitles.isEmpty()) {
                prompt.append("  高频错误题目: ");
                problematicTitles.forEach(title -> prompt.append("\"").append(title).append("\" "));
                prompt.append("\n");
            }
        });
        prompt.append("\n");

        return prompt.toString();
    }

    private String getErrorName(int errorType) {
        switch (errorType) {
            case 2: return "编译错误";
            case 3: return "测试用例失败";
            default: return "未知错误(" + errorType + ")";
        }
    }
}
