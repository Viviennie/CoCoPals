package com.tongji.codejourneycolab.codejourneycolabbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.LoginResponseDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.User;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidCredentialsException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidInformationException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.IdentityAlreadyExistsException;
import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.UserMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.security.InvalidTokenManager;
import com.tongji.codejourneycolab.codejourneycolabbackend.security.JwtTokenProvider;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvalidTokenManager invalidTokenManager;

    @Override
    public LoginResponseDto login(String identity, String password) throws InvalidCredentialsException {
        if (identity == null || password == null) {
            throw new InvalidCredentialsException("User not found or invalid password");
        }

        User user;
        if (identity.contains("@")) { //邮箱登录
            user = userMapper.selectByEmail(identity);
        } else {    //用户名登录
            user = userMapper.selectByUsername(identity);
        }
        if (user == null ) {
            throw new InvalidCredentialsException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return new LoginResponseDto(jwtTokenProvider.generateToken(user.getId()), user.getRole());
    }

    @Override
    public void register(String username, String password, String email) throws IdentityAlreadyExistsException, InvalidInformationException {
        if (userMapper.exists(new QueryWrapper<User>().eq("username", username))) {
            throw new IdentityAlreadyExistsException("Username already exists");
        }
        if (userMapper.exists(new QueryWrapper<User>().eq("email", email))) {
            throw new IdentityAlreadyExistsException("Email already exists");
        }

        // 检查注册合法性
//        if (username == null || username.length() < 4 || username.length() > 20 || !username.matches("\\w+")) {
//            throw new InvalidInformationException("Username must be between 4 and 20 characters and contain only alphanumeric characters and underscores");
//        }
//        if (password == null || password.length() < 8) {
//            throw new InvalidInformationException("Password must be at least 8 characters");
//        }
        if (email == null || !email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new InvalidInformationException("Must be a valid email address");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        // *注册的默认身份是普通用户
        user.setRole("USER");
        userMapper.insert(user);
    }

    @Override
    public UserInfoDto getUserInfoById(Integer id) throws InvalidCredentialsException {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new InvalidCredentialsException("User not found");
        }
        return new UserInfoDto(user.getUsername(), user.getEmail());
    }

    @Override
    public void editUserInfoById(Integer id, UserInfoDto userInfoDto) throws InvalidCredentialsException, InvalidInformationException {
//        if (userInfoDto.getUsername() == null || userInfoDto.getUsername().length() < 4 || userInfoDto.getUsername().length() > 20 || !userInfoDto.getUsername().matches("\\w+")) {
//            throw new InvalidInformationException("Username must be between 4 and 20 characters and contain only alphanumeric characters and underscores");
//        }
        if (userInfoDto.getEmail() == null || !userInfoDto.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new InvalidInformationException("Must be a valid email address");
        }
        if (id == null || !userMapper.exists(new QueryWrapper<User>().eq("id", id))) {
            throw new InvalidCredentialsException("User not found");
        }
        if (userMapper.exists(new QueryWrapper<User>().eq("username", userInfoDto.getUsername()).ne("id", id))) {
            throw new InvalidInformationException("Username already exists");
        }
        if (userMapper.exists(new QueryWrapper<User>().eq("email", userInfoDto.getEmail()).ne("id", id))) {
            throw new InvalidInformationException("Email already exists");
        }
        User user = userMapper.selectById(id);
        user.setUsername(userInfoDto.getUsername());
        user.setEmail(userInfoDto.getEmail());
        userMapper.updateById(user);
    }

    @Override
    public void logout(String token) {
        invalidTokenManager.addInvalidToken(token);
    }

    @Override
    public void teacherQualifyById(Integer id, MultipartFile[] files) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new InvalidCredentialsException("User not found");
        }
        if(user.getRole().equals("TEACHER")){
            throw new InvalidInformationException("User already qualified as a teacher");
        }
        // TODO: 上传证书和职业资格证书并保存到数据库
        user.setRole("TEACHER");
        userMapper.updateById(user);
    }
}