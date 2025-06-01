package com.tongji.codejourneycolab.codejourneycolabbackend.security;

import com.tongji.codejourneycolab.codejourneycolabbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserMapper userMapper;

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(CsrfConfigurer::disable).cors(cors -> cors.configurationSource(request -> {
            var corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOriginPatterns(List.of("*")); // 使用 setAllowedOriginPatterns 而不是 setAllowedOrigins
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true); // 允许携带凭证
            return corsConfiguration;
        })).authorizeHttpRequests(authorize -> authorize.requestMatchers(authorizedRoutes().toArray(new String[0])).authenticated().anyRequest().permitAll()).formLogin(FormLoginConfigurer::disable).httpBasic(HttpBasicConfigurer::disable).addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new CJUserDetails(userMapper.selectByUsername(username));
    }

    // *在这里定义需要认证的路由，可以用ant风格的路径
    @Bean
    public List<String> authorizedRoutes() {
        return List.of("/helloauthorized","/hellowithparam", "/account/getinfo", "/account/editinfo","/account/logout",
                "/question/getListByUser","/question/getAttemptedQuestionList","/question/getSubmissionList","/question/getSubmission","/question/run",
                "/document/**","/class/getclasslist","/class/join","/class/create","/class/createNotice","/class/getHomeworkList","/class/createAssignment"

        );
    }

    // 使用in memory实现的token失效管理器
    @Bean
    public InvalidTokenManager invalidTokenManager() {
        return new InMemoryInvalidTokenManager();
    }
}
