package com.example.java_everyday.redis_lock.annotation;

import java.lang.annotation.*;

/**
 * 构建锁的业务数据
 */
@Target({ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LockField {
    String[] fieldNames() default {};

}
