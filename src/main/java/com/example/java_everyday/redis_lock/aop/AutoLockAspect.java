package com.example.java_everyday.redis_lock.aop;

import com.example.java_everyday.redis_lock.annotation.AutoLock;
import com.example.java_everyday.redis_lock.annotation.LockField;
import com.example.java_everyday.redis_lock.utils.ReflectionUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 自动锁切面
 * 处理加锁解锁逻辑
 */
@Aspect
@Slf4j
@Component
public class AutoLockAspect {

    @Resource
    private RedissonClient redissonClient;

    private static final String REDIS_LOCK_PREFIX = "nicolasLock";

    private static final String SEPARATOR = ":";

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.example.java_everyday.redis_lock.annotation.AutoLock)")
    public void lockPointcut() {
    }

    /**
     * 定义拦截处理方式
     */
    @Around("lockPointcut()")
    public Object doLock(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取需要加锁的方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 获取锁注解
        AutoLock autoLock = method.getAnnotation(AutoLock.class);
        // 获取锁前缀
        String prefix = autoLock.prefix();
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        StringBuilder lockKeyStr = new StringBuilder(prefix);
        Object[] args = joinPoint.getArgs();
        // 遍历参数
        int index = -1;
        LockField lockField;
        // 构建key
        for (Parameter parameter : parameters) {
            Object arg = args[++index];
            lockField = parameter.getAnnotation(LockField.class);
            if (lockField == null) {
                continue;
            }
            String[] fieldNames = lockField.fieldNames();
            if (fieldNames == null || fieldNames.length == 0) {
                lockKeyStr.append(SEPARATOR).append(arg);
            } else {
                List<Object> filedValues = ReflectionUtil.getFiledValues(parameter.getType(), arg, fieldNames);
                for (Object value : filedValues) {
                    lockKeyStr.append(SEPARATOR).append(value);
                }
            }
        }
        String lockKey = REDIS_LOCK_PREFIX + SEPARATOR + lockKeyStr;
        RLock lock = redissonClient.getLock(lockKey);
        // 加锁标志位
        boolean lockFlag = false;
        try {
            long lockTime = autoLock.lockTime();
            long waitTime = autoLock.waitTime();
            TimeUnit timeUnit = autoLock.timeUnit();
            boolean tryLock = autoLock.tryLock();
            try {
                if (tryLock) {
                    lockFlag = lock.tryLock(waitTime, lockTime, timeUnit);
                } else {
                    lock.lock(lockTime, timeUnit);
                    lockFlag = true;
                }
            }catch (Exception e){
                log.error("加锁失败！，错误信息", e);
                throw new RuntimeException("加锁失败！");
            }
            if (!lockFlag) {
                throw new RuntimeException("加锁失败！");
            }
            // 执行业务
            return joinPoint.proceed();
        } finally {
            // 释放锁
            if (lockFlag) {
                lock.unlock();
                log.info("释放锁完成，key：{}",lockKey);
            }
        }
    }


}
