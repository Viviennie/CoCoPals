package com.tongji.codejourneycolab.codejourneycolabbackend.service;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.ClassInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.HomeworkDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.StuStatusDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.ClassNotice;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Homework;

import java.util.Date;
import java.util.List;

public interface IClassService {
    List<ClassInfoDto> getClassList(Integer userId);

    String generateJoinCode(Integer classId);

    Integer getClassIdByJoinCode(String joinCode);

    Boolean joinClass(String joinCode,Integer userId);

    Boolean createClass(Integer userId, String className);

    List<UserInfoDto> getStuListByClassId(Integer classId);

    List<ClassNotice> getNotice(Integer classId);

    Boolean createNotice(Integer classId, String title, String content);

    List<HomeworkDto> getHomeworkList(Integer stuId, Integer classId);

    Boolean createHomework(Integer classId, Integer problemId, Date dueTime);

    List<StuStatusDto> getStuHomework(Integer classId, Integer problemId);
}
