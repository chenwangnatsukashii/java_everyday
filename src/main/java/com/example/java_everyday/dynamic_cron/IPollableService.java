package com.example.java_everyday.dynamic_cron;

public interface IPollableService {
    /**
     * 执行方法
     */
    void poll();

    /**
     * 获取周期表达式
     *
     * @return
     */
    default String getCronExpression() {
        return null;
    }

    /**
     * 获取任务名称
     *
     * @return
     */
    default String getTaskName() {
        return this.getClass().getSimpleName();
    }
}
