package com.example.java_everyday.redis_lock.config;

import com.example.java_everyday.redis_lock.aop.AutoLockAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockAutoConfig {

    @Bean
    public AutoLockAspect autoLockAspect(){
        return new AutoLockAspect();
    }

}
