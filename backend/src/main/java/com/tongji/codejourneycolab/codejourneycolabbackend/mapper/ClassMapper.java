package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.ClassInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.HomeworkDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.StuStatusDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Clas;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.ClassNotice;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Homework;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Clas> {

    @Select("SELECT c.id, c.classname, u.username as teacher " +
            "FROM class as c " +
            "INNER JOIN user as u " +
            "ON c.teacher_id = u.id " +
            "WHERE u.id = #{userId}")
    List<ClassInfoDto> getClassListByTeacher(@Param("userId") Integer userId);

    @Select("SELECT c.id, c.classname, u.username as teacher " +
            "FROM user_class uc " +
            "JOIN class c ON uc.class_id = c.id " +
            "JOIN user u ON c.teacher_id = u.id " +
            "WHERE uc.user_id = #{userId}")
    List<ClassInfoDto> getClassListByStudent(@Param("userId") Integer userId);

    @Insert("INSERT INTO user_class(user_id,class_id) VALUES(#{userId},#{classId})")
    void joinClass(@Param("userId") Integer userId, @Param("classId") Integer classId);

    @Select("SELECT u.username, u.email " +
            "FROM user_class uc join user u on uc.user_id = u.id " +
            "WHERE uc.class_id = #{classId}")
    List<UserInfoDto> getStuListByClassId(Integer classId);

    @Select("SELECT cn.*,user.username as teacher_name FROM class_notice as cn left join class as c on cn.class_id = c.id left join user on c.teacher_id = user.id WHERE class_id = #{classId} "+
            "ORDER BY create_time DESC")
    List<ClassNotice> getNotice(Integer classId);

    @Insert("INSERT INTO class_notice(class_id,title,content,create_time) VALUES(#{classId},#{title},#{content},#{createTime})")
    int insertNotice(ClassNotice newNotice);

    List<HomeworkDto> getHomeworkList(
            @Param("studentId") Integer studentId,
            @Param("classId") Integer classId
    );

    @Insert("INSERT INTO homework(class_id,question_id,due_time) VALUES(#{classId},#{questionId},#{dueTime})")
    int insertHomework(Homework newHomework);

    List<StuStatusDto> getStuHomework( @Param("classId")Integer classId, @Param("problemId") Integer problemId);
}
