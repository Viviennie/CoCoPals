package com.tongji.codejourneycolab.codejourneycolabbackend.dto;

/**
 * 代码执行请求DTO
 * 用于接收前端发送的代码和输入参数
 */
public class CodeExecutionRequest {
    private String code;
    private String input;

    public CodeExecutionRequest() {
    }

    public CodeExecutionRequest(String code, String input) {
        this.code = code;
        this.input = input;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "CodeExecutionRequest{" +
                "code='" + code + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
