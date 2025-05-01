package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.LoginRequestDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.RegisterRequestDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.UserInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidCredentialsException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.InvalidInformationException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.IdentityAlreadyExistsException;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            return ResponseEntity.ok(accountService.login(loginRequestDto.getIdentity(), loginRequestDto.getPassword()));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto) {
        try {
            accountService.register(registerRequestDto.getUsername(), registerRequestDto.getPassword(), registerRequestDto.getEmail());
            return ResponseEntity.ok("Register success");
        } catch (IdentityAlreadyExistsException | InvalidInformationException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/getinfo")
    public ResponseEntity<Object> getInfo(@RequestAttribute Integer id) {
        try {
            return ResponseEntity.ok(accountService.getUserInfoById(id));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/editinfo")
    public ResponseEntity<String> editInfo(@RequestAttribute Integer id, @RequestBody UserInfoDto userInfoDto) {
        try {
            accountService.editUserInfoById(id, userInfoDto);
            return ResponseEntity.ok("Edit success");
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (InvalidInformationException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            accountService.logout(token);
            return ResponseEntity.ok("Logout success");
        }
        return ResponseEntity.internalServerError().body("Unexpected internal server error");
    }

    @PostMapping(value ="/tqc",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> teacherQualify(
            @RequestAttribute Integer id,
            @RequestPart("files") MultipartFile[] files) {
        try {
            // 处理文件上传逻辑
            accountService.teacherQualifyById(id, files);
            return ResponseEntity.ok("Teacher qualify success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("上传失败: " + e.getMessage());
        }
    }
}
