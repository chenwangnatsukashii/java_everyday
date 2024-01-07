package com.example.java_everyday.dynamic_cron;

public class CronTaskBar implements IPollableService {
    @Override
    public void poll() {
        System.out.println("Say Bar");
    }

    @Override
    public String getCronExpression() {
        return "0/1 * * * * ?";
    }
}
