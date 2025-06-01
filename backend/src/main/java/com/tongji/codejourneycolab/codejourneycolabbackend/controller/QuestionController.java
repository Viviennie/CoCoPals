package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.CodeExecutionRequest;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.*;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.QuestionMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.codeExecution.PythonExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    PythonExecutionService pythonExecutionService;

    //获取题目列表
    @GetMapping("/getList")
    public List<Question> getQuestionList() {
        List<Question> questionList = questionMapper.getQuestionList();
        return questionList;
    }

    @GetMapping("/getListByUser")
    public List<QuestionByUser> getQuestionListByUser(@RequestAttribute Integer id) {
        List<Question> questionList = questionMapper.getQuestionList();
        List<QuestionByUser> questionByUserList = new ArrayList<>();

        for (Question question : questionList) {
            int status = questionMapper.getQuestionState(id, question.getId());
            QuestionByUser questionByUser = new QuestionByUser();
            switch (status) {
                case 1:
                    questionByUser.fill(question,"passed");
                    break;
                case 2:
                    questionByUser.fill(question,"failed");
                    break;
                default:
                    questionByUser.fill(question,"no attempt");
            }
            questionByUserList.add(questionByUser);
        }
        return questionByUserList;
    }

    //获取题目详情
    @GetMapping("/get")
    public Question getQuestion(@RequestParam int questionId) {
        QuestionDetail question = questionMapper.getQuestionById(questionId);
        question.setTests(questionMapper.getTestCaseListByQuestionId(questionId));
//        System.out.println(question);
        return question;
    }

    //获取提交过的题目
    @GetMapping("/getAttemptedQuestionList")
    public List<QuestionSubmitted> getAttemptedQuestionList(@RequestAttribute Integer id) {
        List<QuestionSubmitted> attemptedList = questionMapper.getAttemptedQuestionList(id);
        for (QuestionSubmitted attemptedQuestion : attemptedList) {
            attemptedQuestion.setState(questionMapper.getQuestionState(id, attemptedQuestion.getId()));
        }
//        System.out.print("attemptedList:" + attemptedList);
        return attemptedList;
    }

    //查看某一道题的所有提交
    @GetMapping("/getSubmissionList")
    public List<Submission> getSubmissionList(@RequestAttribute Integer id, @RequestParam int questionId) {
        List<Submission> submissionList = questionMapper.getSubmissionList(id, questionId);
//        System.out.println("submissionList:" + submissionList);
        return submissionList;
    }

    //查看一次提交
    @GetMapping("/getSubmission")
    public SubmissionDetail getSubmission(@RequestAttribute Integer id, @RequestParam int questionId, @RequestParam int attemptNum) {
        SubmissionDetail submissionDetail = questionMapper.getSubmission(id, questionId, attemptNum);
//        System.out.println(submissionDetail);
        return submissionDetail;
    }

    //发送代码，编译，运行，比较，返回结果
    //参数放在Body中进行请求
    @PostMapping("/run")
    public SubmissionDetail runQuestion(@RequestAttribute Integer id, @RequestParam int questionId, @RequestParam String code) {
        List<TestCase> testCase = questionMapper.getTestCaseListByQuestionId(questionId);
//        System.out.println(testCase);
//        System.out.println(code);
        SubmissionDetail result = pythonExecutionService.executePythonCode(code, testCase);
//        System.out.println(result);
        result.setAttemptNum(questionMapper.getSubmissionCount(id, questionId) + 1);
        result.setUserId(id);
        result.setQuestionId(questionId);
        questionMapper.addSubmission(result);
        return result;
    }

    @PostMapping("/runWithInput")
    public String runQuestion(@RequestBody CodeExecutionRequest request) {
        System.out.println("=== runWithInput 请求开始 ===");
        System.out.println("接收到的代码: " + request.getCode());
        System.out.println("接收到的输入: " + request.getInput());

        try {
            String result = pythonExecutionService.executeWithInput(request.getCode(), request.getInput());
            System.out.println("执行结果: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("执行出错: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
