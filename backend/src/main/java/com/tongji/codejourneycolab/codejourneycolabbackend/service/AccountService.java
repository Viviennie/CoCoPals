package com.tongji.codejourneycolab.codejourneycolabbackend.service;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.LoginResponseDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidCredentialsException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidInformationException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.IdentityAlreadyExistsException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AccountService {
    LoginResponseDto login(String username, String password) throws IdentityAlreadyExistsException;

    void register(String username, String password, String email) throws IdentityAlreadyExistsException, InvalidInformationException;

    UserInfoDto getUserInfoById(Integer id) throws InvalidCredentialsException;

    void editUserInfoById(Integer id, UserInfoDto userInfoDto) throws InvalidCredentialsException, InvalidInformationException;

    void logout(String token);

    void teacherQualifyById(Integer id, MultipartFile[] files);

}
