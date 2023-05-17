package com.example.java_everyday.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CaffeineTest {

    @Resource
    private Cache<String, Object> caffeineCache;

    public void getTest() {
        caffeineCache.put("1", "张三");
        //张三
        System.out.println(caffeineCache.getIfPresent("1"));
        //存储的是默认值
        System.out.println(caffeineCache.get("2", o -> "默认值"));
    }

    /**
     * value：缓存key的前缀。
     * key：缓存key的后缀。
     * sync：设置如果缓存过期是不是只放一个请求去请求数据库，其他请求阻塞，默认是false（根据个人需求）。
     * unless：不缓存空值,这里不使用，会报错
     * 查询用户信息类
     * 如果需要加自定义字符串，需要用单引号
     * 如果查询为null，也会被缓存
     */
    @Cacheable(value = "GET:USER", key = "'user'+#userId", sync = true)
    public String getUserByUserId(Integer userId) {
        System.out.println("查询了数据库");
        return userId.toString();
    }


    public static void main(String[] args) throws InterruptedException {

        // 自动加载
        LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
                //创建缓存或者最近一次更新缓存后经过指定时间间隔，刷新缓存；refreshAfterWrite仅支持LoadingCache
                .refreshAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .maximumSize(10)
                //根据key查询数据库里面的值，这里是个lamba表达式
                .build(key -> new Date().toString());

        // 2、驱逐策略 常用的有基于容量的驱逐和基于时间的驱逐。


    }
}
