
package com.tongji.codejourneycolab.codejourneycolabbackend.service.codeExecution;

import com.tongji.codejourneycolab.codejourneycolabbackend.configuration.CodeExecutionConfig;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.SubmissionDetail;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.*;

@Service
public class PythonExecutionService {

    @Autowired
    private CodeExecutionConfig codeExecutionConfig;

    // 获取Python解释器路径
    private String getPythonPath() {
        if (codeExecutionConfig == null) {
            System.err.println("ERROR: codeExecutionConfig is null!");
            return "python"; // 使用默认的python命令
        }
        if (codeExecutionConfig.getPython() == null) {
            System.err.println("ERROR: codeExecutionConfig.getPython() is null!");
            return "python"; // 使用默认的python命令
        }
        String path = codeExecutionConfig.getPython().getPath();
        System.out.println("Python路径配置: " + path);
        return "\"" + path + "\"";
    }

    // 生成临时文件路径
    private String generateTempFilePath() {
        if (codeExecutionConfig == null || codeExecutionConfig.getPython() == null) {
            System.err.println("ERROR: 配置为空，使用默认临时目录");
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "code_" + UUID.randomUUID().toString() + ".py";
            return Paths.get(tempDir, fileName).toString();
        }
        String tempDir = codeExecutionConfig.getPython().getTempDir();
        String fileName = "code_" + UUID.randomUUID().toString() + ".py";
        return Paths.get(tempDir, fileName).toString();
    }

    public SubmissionDetail executePythonCode(String code, List<TestCase> testCases) {
        System.out.println("=== 开始执行代码测试 ===");
        System.out.println("代码: " + code);
        System.out.println("测试用例数量: " + testCases.size());

        int attemptNum = 1;  // 任意整数
        LocalDateTime submitTime = LocalDateTime.now();
        String language = "Python";
        int state = 1;  // 初始设为“通过”
        int passCount = 0;
        double maxTime = 0; // 记录最大执行时间
        String firstFailureOutput = null;

        // 生成临时文件路径
        String codeFilePath = generateTempFilePath();

        // 确保临时目录存在
        try {
            Path tempDir = Paths.get(codeExecutionConfig.getPython().getTempDir());
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
        } catch (IOException e) {
            System.out.println("Failed to create temp directory: " + e.getMessage());
            state = 2;  // 编译错误
        }



        // 遍历所有测试用例并执行
        for (TestCase testCase : testCases) {
            String input = testCase.getInput();
            String expectedOutput = testCase.getOutput();

            System.out.println("执行测试用例 - 输入: " + input + ", 期望输出: " + expectedOutput);

            try {
                // 生成包含输入处理的完整代码
                String inputProcessingCode = processInput(input);
                String fullCode = inputProcessingCode + code;

                // 写入代码到临时文件
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(codeFilePath))) {
                    writer.write(fullCode);
                }

                // 执行Python程序（不需要命令行参数，因为输入已经内置到代码中）
                String[] args = {getPythonPath(), codeFilePath};

                // 记录执行开始时间
                long startTime = System.nanoTime();

                // 执行Python程序
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
                Process process = processBuilder.start();

                // 获取Python输出字符串
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder outputBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    outputBuilder.append(line).append("\n");
                }
                in.close();

                // 等待进程结束
                process.waitFor();

                // 获取程序输出
                String actualOutput = outputBuilder.toString().trim();
                System.out.println("实际输出: " + actualOutput);

                // 使用改进的输出比较逻辑
                boolean outputMatches = compareOutputs(actualOutput, expectedOutput);

                if (!outputMatches) {
                    state = 3;  // 结果错误
                    firstFailureOutput = "期望: " + expectedOutput + "\n实际: " + actualOutput;
                    System.out.println("输出不匹配，测试失败");
                    break;
                } else {
                    passCount++;
                    System.out.println("测试用例通过");
                }

                // 记录执行结束时间并计算运行时间
                long endTime = System.nanoTime();
                double elapsedTime = (endTime - startTime) / 1000000000.0; // 转换为秒

                maxTime = Math.max(maxTime, elapsedTime);  // 更新最大执行时间

            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
                state = 2;  // 编译错误
            }
        }

        // 清理临时文件
        cleanupTempFile(codeFilePath);

        return new SubmissionDetail(
                attemptNum, submitTime, language, state, passCount, maxTime, code, firstFailureOutput);
    }

    public String executeWithInput(String code, String input) {
        System.out.println("=== 执行代码（带输入）===");
        System.out.println("原始代码: " + code);
        System.out.println("原始输入: " + input);

        // 处理代码中的缩进问题（URL编码导致的++++变成空格）
        code = fixCodeIndentation(code);
        System.out.println("修复后代码: " + code);

        int attemptNum = 1;  // 任意整数
        LocalDateTime submitTime = LocalDateTime.now();
        String language = "Python";
        int state = 1;  // 初始设为“通过”
        int passCount = 0;
        double maxTime = 0; // 记录最大执行时间
        String firstFailureOutput = null;

        // 生成临时文件路径
        System.out.println("开始生成临时文件路径...");
        String codeFilePath = generateTempFilePath();
        System.out.println("临时文件路径: " + codeFilePath);

        // 确保临时目录存在
        try {
            String tempDirPath = codeExecutionConfig.getPython().getTempDir();
            System.out.println("临时目录配置: " + tempDirPath);
            Path tempDir = Paths.get(tempDirPath);
            if (!Files.exists(tempDir)) {
                System.out.println("临时目录不存在，正在创建...");
                Files.createDirectories(tempDir);
                System.out.println("临时目录创建成功");
            } else {
                System.out.println("临时目录已存在");
            }
        } catch (IOException e) {
            System.err.println("创建临时目录失败: " + e.getMessage());
            e.printStackTrace();
            return "Error: Failed to create temp directory - " + e.getMessage();
        }

        try {
            // 生成包含输入处理的完整代码
            String inputProcessingCode = processInput(input);
            String fullCode = inputProcessingCode + code;

            System.out.println("=== 生成的完整代码 ===");
            System.out.println(fullCode);
            System.out.println("=== 代码结束 ===");

            // 将代码写入到一个临时Python文件（使用UTF-8编码）
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(codeFilePath), StandardCharsets.UTF_8))) {
                writer.write(fullCode);
            }

            // 执行Python程序（不需要命令行参数）
            String[] args = {getPythonPath(), codeFilePath};

            // 记录执行开始时间
            long startTime = System.nanoTime();

            // 执行Python程序
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.redirectErrorStream(true); // 合并标准输出和错误输出
            Process process = processBuilder.start();

            // 获取Python输出字符串
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = in.readLine()) != null) {
                lineCount++;
                System.out.println("读取第" + lineCount + "行输出: [" + line + "]");
                outputBuilder.append(line).append("\n");
            }
            in.close();

            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("进程退出码: " + exitCode);

            // 获取程序输出并拆分
            String result = outputBuilder.toString().trim();
            System.out.println("完整输出: [" + result + "]");
            System.out.println("输出行数: " + lineCount);

            // 清理临时文件
            cleanupTempFile(codeFilePath);

            return result;
        }
        catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            // 清理临时文件
            cleanupTempFile(codeFilePath);
            return "Error: " + e.getMessage();
        }
    }

    // 清理临时文件
    private void cleanupTempFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Cleaned up temp file: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Failed to cleanup temp file: " + filePath + ", error: " + e.getMessage());
        }
    }

    /**
     * 改进的输出比较逻辑
     * 支持多行输出和更智能的比较
     */
    private boolean compareOutputs(String actual, String expected) {
        if (actual == null) actual = "";
        if (expected == null) expected = "";

        // 处理转义字符（支持\n表示换行）
        expected = expected.replace("\\n", "\n").replace("\\t", "\t");

        // 去除首尾空白
        actual = actual.trim();
        expected = expected.trim();

        // 1. 精确匹配
        if (actual.equals(expected)) {
            return true;
        }

        // 2. 忽略行尾空格的比较
        String[] actualLines = actual.split("\n");
        String[] expectedLines = expected.split("\n");

        if (actualLines.length != expectedLines.length) {
            return false;
        }

        for (int i = 0; i < actualLines.length; i++) {
            if (!actualLines[i].trim().equals(expectedLines[i].trim())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 改进的输入处理
     * 智能处理单行和多行输入
     */
    private String processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "import sys\ndef input(): return ''\n";
        }

        // 处理转义字符
        input = input.replace("\\n", "\n").replace("\\t", "\t");

        // 分割输入为行
        String[] lines = input.split("\n");
        StringBuilder code = new StringBuilder();
        code.append("import sys\n");
        code.append("_input_lines = [\n");

        for (String line : lines) {
            code.append("    \"").append(line.replace("\"", "\\\"")).append("\",\n");
        }

        code.append("]\n");
        code.append("_line_index = 0\n");
        code.append("def input():\n");
        code.append("    global _line_index\n");
        code.append("    if _line_index < len(_input_lines):\n");
        code.append("        result = _input_lines[_line_index]\n");
        code.append("        _line_index += 1\n");
        code.append("        return result\n");
        code.append("    return ''\n\n");

        return code.toString();
    }

    /**
     * 修复代码缩进问题
     * 处理URL编码导致的缩进问题
     */
    private String fixCodeIndentation(String code) {
        if (code == null) return "";

        // 处理URL编码后的换行符和其他转义字符
        code = code.replace("%0A", "\n")
                   .replace("%0D", "\r")
                   .replace("%20", " ")
                   .replace("%09", "\t");

        // 简单的缩进修复：将连续的空格转换为4个空格的倍数
        String[] lines = code.split("\n");
        StringBuilder fixedCode = new StringBuilder();

        for (String line : lines) {
            // 计算行首空格数量
            int leadingSpaces = 0;
            for (char c : line.toCharArray()) {
                if (c == ' ') {
                    leadingSpaces++;
                } else {
                    break;
                }
            }

            // 获取去除前导空格的内容
            String content = line.substring(leadingSpaces);

            // 如果有缩进，转换为4的倍数
            if (leadingSpaces > 0) {
                int indentLevel = (leadingSpaces + 3) / 4; // 向上取整
                StringBuilder indent = new StringBuilder();
                for (int i = 0; i < indentLevel * 4; i++) {
                    indent.append(" ");
                }
                fixedCode.append(indent.toString()).append(content);
            } else {
                fixedCode.append(content);
            }

            fixedCode.append("\n");
        }

        return fixedCode.toString().trim();
    }
}


