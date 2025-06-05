package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.LearningAnalysisCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LearningAnalysisCacheMapper extends BaseMapper<LearningAnalysisCache> {
    @Select("SELECT * FROM learning_analysis_cache WHERE user_id = #{userId}")
    LearningAnalysisCache findByUserId(@Param("userId") Integer userId);
}