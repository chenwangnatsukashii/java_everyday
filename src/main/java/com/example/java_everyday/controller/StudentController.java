package com.example.java_everyday.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StudentController {

    //属性注入
    @Autowired
    private Student student;

    //初始化
    @PostConstruct
    public void init() {
        student.setName("周杰伦");
    }
}
