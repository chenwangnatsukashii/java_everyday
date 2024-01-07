package com.example.java_everyday.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS) // 设置最后一次写入或访问后经过固定时间过期
                .initialCapacity(100) // 初始的缓存空间大小
                .maximumSize(1000) // 缓存的最大条数
                .removalListener((key, val, removalCause) -> System.out.println("key: " + key + "被移除"))
                .evictionListener((key, val, evictionCause) -> System.out.println("key: " + key + "被回收"))
                .recordStats() // 记录命中
                .build();
    }

}
