package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.tongji.codejourneycolab.codejourneycolabbackend.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    //查询题目列表
    //@Select("select id, title, difficulty from question")
    List<Question> getQuestionList();

    //查询单个题目详情
    //@Select("select * from question where id = #{questionId}")
    QuestionDetail getQuestionById(@Param("questionId") int questionId);

    //获取单一题目的用例
    //@Select("select id, input, output from test_case where question_id=#{questionId} and id<=3 order by id")
    List<TestCase> getTestCaseListByQuestionId(@Param("questionId") int questionId);


    //根据用户id获取提交过的题目
    //@Select("select distinct q.id, q.title, q.description, s.submit_time, s.state FROM submission s JOIN question q ON s.question_id = q.id WHERE s.user_id = #{userId} ORDER BY s.submit_time DESC;")
    List<QuestionSubmitted> getAttemptedQuestionList(@Param("userId") int userId);

    //获取题目状态
    int getQuestionState(@Param("userId") int userId, @Param("questionId") int questionId);

    //获取单个题目的提交记录
    List<Submission> getSubmissionList(@Param("userId") int userId, @Param("questionId") int questionId);

    //获取提交记录详细信息
    SubmissionDetail getSubmission(@Param("userId") int userId, @Param("questionId") int questionId, @Param("attemptNum") int attemptNum);

    //获取用户该题已提交次数
    int getSubmissionCount(@Param("userId") int userId, @Param("questionId") int questionId);

    //添加提交记录
    void addSubmission(SubmissionDetail detail);
}
