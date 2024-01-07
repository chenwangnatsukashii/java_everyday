package com.example.java_everyday.dynamic_cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class CronTaskLoader implements ApplicationRunner {
    private final SchedulingConfiguration schedulingConfiguration;
    private final AtomicBoolean appStarted = new AtomicBoolean(false);
    private final AtomicBoolean initializing = new AtomicBoolean(false);

    public CronTaskLoader(SchedulingConfiguration schedulingConfiguration) {
        this.schedulingConfiguration = schedulingConfiguration;
    }

    /**
     * 定时任务配置刷新
     */
    @Scheduled(fixedDelay = 5000)
    public void cronTaskConfigRefresh() {
        if (appStarted.get() && initializing.compareAndSet(false, true)) {
            log.info("定时调度任务动态加载开始>>>>>>");
            try {
                schedulingConfiguration.refresh();
            } finally {
                initializing.set(false);
            }
            log.info("定时调度任务动态加载结束<<<<<<");
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        if (appStarted.compareAndSet(false, true)) {
            cronTaskConfigRefresh();
        }
    }

}
