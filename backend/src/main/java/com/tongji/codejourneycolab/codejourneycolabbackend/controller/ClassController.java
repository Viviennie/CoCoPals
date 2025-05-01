package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.*;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.ClassNotice;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.impl.ClassService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    // 获取班级列表
    @GetMapping("/getclasslist")
    public ResponseEntity<List<ClassInfoDto>> getClasses(@RequestAttribute Integer id) {
        try {
            List<ClassInfoDto> classList = classService.getClassList(id);
            return ResponseEntity.ok(classList);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 加入班级
    @PostMapping("/join")
    public ResponseEntity<Boolean> joinClass(@RequestAttribute Integer id,@RequestBody JoinClassCodeDto joinCode) {
        try {
            Boolean result = classService.joinClass(joinCode.getClassCode(),id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 创建班级
    @PostMapping("/create")
    public ResponseEntity<Boolean> createClass(@RequestAttribute Integer id,@RequestParam String className) {
        try {
            Boolean result = classService.createClass(id,className);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 获取班级成员列表
    @GetMapping("/getstulist")
    public ResponseEntity<List<UserInfoDto>> getStudentsInClass(@RequestParam Integer classId) {
        try {
            List<UserInfoDto> studentList = classService.getStuListByClassId(classId);
            return ResponseEntity.ok(studentList);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 生成加入班级的代码
    @PostMapping("/generatecode")
    public ResponseEntity<String> generateJoinCode(@RequestParam Integer classId) {
        try {
            String joinCode = classService.generateJoinCode(classId);
            return ResponseEntity.ok(joinCode);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 获取通知列表
    @GetMapping("/getNotice")
    public ResponseEntity<List<ClassNotice>> getNotice(@RequestParam Integer classId) {
        try {
            List<ClassNotice> notifications = classService.getNotice(classId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 创建通知
    @PostMapping("/createNotice")
    public ResponseEntity<Boolean> createNotice(@RequestAttribute Integer id,
                                                @RequestBody NoticeRequestDto request) {
        try {
            Boolean result = classService.createNotice(request.getClassId(), request.getTitle(),request.getContent());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 获取作业列表
    @GetMapping("/getHomeworkList")
    public ResponseEntity<List<HomeworkDto>> getHomeworkList(@RequestAttribute Integer id,
                                                             @RequestParam Integer classId) {
        try {
            List<HomeworkDto> homeworkList = classService.getHomeworkList(id,classId);
            return ResponseEntity.ok(homeworkList);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 创建作业
    @PostMapping("/createAssignment")
    public ResponseEntity<Boolean> createAssignment(@RequestAttribute Integer id,
                                                    @RequestBody AssignmentRequestDto request) {
        try {
            Boolean result = classService.createHomework(request.getClassId(),request.getProblemId(),request.getDueTime());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getStuHomework")
    public ResponseEntity<List<StuStatusDto>> getStuHomework(@RequestParam Integer classId,
                                                      @RequestParam Integer problemId) {
        try {
            List<StuStatusDto> stuStatus = classService.getStuHomework(classId, problemId);
            return ResponseEntity.ok(stuStatus);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }
}
