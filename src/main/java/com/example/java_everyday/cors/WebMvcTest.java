package com.example.java_everyday.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcTest implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")     //允许的路径
                .allowedMethods("*")     //允许的方法
                .allowedHeaders("*")     //允许的请求头
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .maxAge(3600);
    }
}
