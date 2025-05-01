package com.tongji.codejourneycolab.codejourneycolabbackend.service.codeExecution;

import com.tongji.codejourneycolab.codejourneycolabbackend.entity.SubmissionDetail;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.TestCase;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.*;

@Service
public class PythonExecutionService {

    // python程序的绝对路径，在windows中用"\\"分隔，在Linux中用"/"分隔
    String pyPath = "D:\\APP\\PYTHON\\python.exe";  // Python解释器路径

    // 临时文件路径，用来存储代码
    String codeFilePath = "D:\\cjcl.py";

    public SubmissionDetail executePythonCode(String code, List<TestCase> testCases) {
        String updatedCode = "import sys\n" + code;
        int inputCount = 1;

        Pattern pattern = Pattern.compile("input\\(\\)");
        Matcher matcher = pattern.matcher(updatedCode);
        StringBuffer modifiedCode = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(modifiedCode, "sys.argv[" + inputCount++ + "]");
        }
        matcher.appendTail(modifiedCode);
        System.out.println("Modified Code:");
        System.out.println(modifiedCode);

        int attemptNum = 1;  // 任意整数
        LocalDateTime submitTime = LocalDateTime.now();
        String language = "Python";
        int state = 1;  // 初始设为“通过”
        int passCount = 0;
        double maxTime = 0; // 记录最大执行时间
        String firstFailureOutput = null;

        // 将代码写入到一个临时Python文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(codeFilePath))) {
            writer.write(String.valueOf(modifiedCode));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            state = 2;  // 编译错误
        }

        // 遍历所有测试用例并执行
        for (TestCase testCase : testCases) {
            String input = testCase.getInput();
            String expectedOutput = testCase.getOutput();

            try {
                // 构建传递给 Python 程序的参数
                String[] args;
                if (input != null) {
                    // 将输入拆分为多个部分（通过空格分隔）
                    String[] inputArgs = input.split("\\s+");
                    // 将输入拆分后的每个部分作为命令行参数传递给 Python 程序
                    args = new String[inputArgs.length + 2];
                    args[0] = pyPath;
                    args[1] = codeFilePath;
                    System.arraycopy(inputArgs, 0, args, 2, inputArgs.length); // 将所有输入参数复制到 args 中
                } else {
                    // 如果没有输入，则只传递 Python 解释器和代码文件路径
                    args = new String[]{pyPath, codeFilePath};
                }

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

                // 获取程序输出并拆分
                String actualOutput = outputBuilder.toString().trim();
                System.out.println("Captured output: " + actualOutput);

                // 将实际输出按空格拆分为单个部分，进行比较
                String[] actualOutputParts = actualOutput.split("\\s+");
                String[] expectedOutputParts = expectedOutput.split("\\s+");

                // 比较输出是否正确
                if (actualOutputParts.length != expectedOutputParts.length) {
                    state = 3;  // 结果错误（输出长度不一致）
                    firstFailureOutput = actualOutput;  // 记录第一个失败的输出
                    break;
                }

                // 比较每一部分
                for (int i = 0; i < actualOutputParts.length; i++) {
                    if (!actualOutputParts[i].equals(expectedOutputParts[i])) {
                        state = 3;  // 结果错误
                        firstFailureOutput = actualOutput;  // 记录第一个失败的输出
                        break;
                    }
                }

                if (state == 3) {
                    break;
                } else {
                    passCount++;
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

        return new SubmissionDetail(
                attemptNum, submitTime, language, state, passCount, maxTime, code, firstFailureOutput);
    }

    public String executeWithInput(String code, String input) {
        String updatedCode = "import sys\n" + code;
        int inputCount = 1;

        Pattern pattern = Pattern.compile("input\\(\\)");
        Matcher matcher = pattern.matcher(updatedCode);
        StringBuffer modifiedCode = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(modifiedCode, "sys.argv[" + inputCount++ + "]");
        }
        matcher.appendTail(modifiedCode);
        System.out.println("Modified Code:");
        System.out.println(modifiedCode);

        int attemptNum = 1;  // 任意整数
        LocalDateTime submitTime = LocalDateTime.now();
        String language = "Python";
        int state = 1;  // 初始设为“通过”
        int passCount = 0;
        double maxTime = 0; // 记录最大执行时间
        String firstFailureOutput = null;

        // 将代码写入到一个临时Python文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(codeFilePath))) {
            writer.write(String.valueOf(modifiedCode));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            state = 2;  // 编译错误
        }
        try {
            // 构建传递给 Python 程序的参数
            String[] args;
            if (input != null) {
                // 将输入拆分为多个部分（通过空格分隔）
                String[] inputArgs = input.split("\\s+");
                // 将输入拆分后的每个部分作为命令行参数传递给 Python 程序
                args = new String[inputArgs.length + 2];
                args[0] = pyPath;
                args[1] = codeFilePath;
                System.arraycopy(inputArgs, 0, args, 2, inputArgs.length); // 将所有输入参数复制到 args 中
            } else {
                // 如果没有输入，则只传递 Python 解释器和代码文件路径
                args = new String[]{pyPath, codeFilePath};
            }

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

            // 获取程序输出并拆分
            return outputBuilder.toString().trim();
        }
        catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            state = 2;  // 编译错误
        }
        return null;
    }
}


