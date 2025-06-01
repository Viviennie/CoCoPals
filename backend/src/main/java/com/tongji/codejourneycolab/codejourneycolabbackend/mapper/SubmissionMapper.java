package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SubmissionMapper {
    @Select("SELECT * FROM submission WHERE user_id = #{userId}")
    List<Submission> findByUserId(Integer userId);
}
