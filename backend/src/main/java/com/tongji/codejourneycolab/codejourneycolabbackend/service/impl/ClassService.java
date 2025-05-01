package com.tongji.codejourneycolab.codejourneycolabbackend.service.impl;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.ClassInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.HomeworkDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.StuStatusDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Clas;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.ClassNotice;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Homework;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.ClassMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.UserMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ClassService implements IClassService {
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ClassInfoDto> getClassList(Integer userId){
        // 先判断用户是否为老师
        if(Objects.equals(userMapper.getRoleById(userId), "TEACHER")){
            return classMapper.getClassListByTeacher(userId);
        }else {
            return classMapper.getClassListByStudent(userId);
        }
    }

    @Override
    public String generateJoinCode(Integer classId) {
        String joinCode = UUID.randomUUID().toString().substring(0, 8); // 生成 8 位随机码
        try {
            redisTemplate.opsForValue().set(joinCode, classId, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("Failed to set join code in Redis for classId: {}", classId, e);
            throw new RuntimeException("Failed to generate join code", e);
        }
        return joinCode;
    }

    @Override
    public Integer getClassIdByJoinCode(String joinCode) {
        return redisTemplate.opsForValue().get(joinCode);
    }

    @Override
    public Boolean joinClass(String joinCode,Integer userId) {
        Integer classId =getClassIdByJoinCode(joinCode);
        if (classId == null) {
            return false;
        }
        classMapper.joinClass(userId, classId);
        return true;
    }

    @Override
    public Boolean createClass(Integer userId, String className) {
        try {
            // 创建班级对象
            Clas newClass = new Clas(userId,className);
            System.out.println(newClass);
            // 插入数据库
            int result = classMapper.insert(newClass);

            // 检查插入是否成功
            if (result > 0) {
                return true;
            } else {
                throw new RuntimeException("create failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserInfoDto> getStuListByClassId(Integer classId){
        try{
            if(classId == null){
                return null;
            }
            return classMapper.getStuListByClassId(classId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClassNotice> getNotice(Integer classId){
        try{
            if(classId == null){
                return null;
            }
            return classMapper.getNotice(classId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean createNotice(Integer classId, String title, String content) {
        try {
            if (classId == null || title == null || content == null) {
                return false;
            }
            ClassNotice newNotice = new ClassNotice(classId, title, content);
            int result = classMapper.insertNotice(newNotice);
            if (result > 0) {
                return true;
            } else {
                throw new RuntimeException("create failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HomeworkDto> getHomeworkList(Integer stuId, Integer classId){
        try{
            if(classId == null){
                return null;
            }
            return classMapper.getHomeworkList(stuId,classId);

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean createHomework(Integer classId, Integer problemId, Date dueTime) {
        try{
            if(classId == null || problemId == null || dueTime == null){
                return false;
            }
            Homework newHomework = new Homework(classId, problemId, dueTime);
            int result = classMapper.insertHomework(newHomework);
            if(result > 0){
                return true;
            }else{
                throw new RuntimeException("create failed");
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StuStatusDto> getStuHomework(Integer classId, Integer problemId) {
        try {
            if (classId == null || problemId == null) {
                return null;
            }
            return classMapper.getStuHomework(classId, problemId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
