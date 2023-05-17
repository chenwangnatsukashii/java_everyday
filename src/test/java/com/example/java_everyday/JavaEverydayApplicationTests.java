package com.example.java_everyday;

import com.example.java_everyday.caffeine.CaffeineTest;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JavaEverydayApplicationTests {

    @Resource
    private CaffeineTest caffeineTest;

    @Test
    void contextLoads() {
        System.out.println(caffeineTest.getUserByUserId(1));
        System.out.println(caffeineTest.getUserByUserId(1));
    }

}
