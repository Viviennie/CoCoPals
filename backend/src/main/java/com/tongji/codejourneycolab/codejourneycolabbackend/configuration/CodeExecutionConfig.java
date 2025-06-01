package com.tongji.codejourneycolab.codejourneycolabbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "code-execution")
public class CodeExecutionConfig {
    
    private Python python = new Python();
    
    public Python getPython() {
        return python;
    }
    
    public void setPython(Python python) {
        this.python = python;
    }
    
    public static class Python {
        private String path;
        private String tempDir;
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public String getTempDir() {
            return tempDir;
        }
        
        public void setTempDir(String tempDir) {
            this.tempDir = tempDir;
        }
    }
}
