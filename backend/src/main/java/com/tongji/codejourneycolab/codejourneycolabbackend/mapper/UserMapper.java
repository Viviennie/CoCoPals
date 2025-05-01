package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Select("select * from user where email = #{email}")
    User selectByEmail(String email);

    @Select("select role from user where id = #{id}")
    String getRoleById(Integer id);
}
