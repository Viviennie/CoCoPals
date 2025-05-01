package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    // ! 仅作测试！
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("da jia hao a ,wo shi codejourneycolab");
    }
    // ! 仅作测试！这个接口需要携带token才能访问，并且不需要传入id，id在token里面
    // * 携带了token的请求会被JwtTokenFilter拦截，JwtTokenFilter会解析token并将id放入RequestAttribute
    // * 通过@RequestAttribute注解获取id (也可以不获取忽略它)
    @GetMapping("/helloauthorized")
    public ResponseEntity<String> helloAuthorized(@RequestAttribute Integer id) {
        return ResponseEntity.ok("尊贵的用户 " + id + " 您好，欢迎来到codejourneycolab");
    }

    // ! 仅作测试！这个接口需要携带token才能访问，可以传一个param
    @GetMapping("/hellowithparam")
    public ResponseEntity<String> helloWithParam(@RequestAttribute Integer id,@RequestParam String greeting) {
        return ResponseEntity.ok("尊贵的用户 " + id + " 您好，欢迎来到codejourneycolab,您的问候是：" + greeting);
    }
}
