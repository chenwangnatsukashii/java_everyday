package com.example.java_everyday.responsibility_chain.impl;

import com.example.java_everyday.responsibility_chain.GatewayHandler;

public class ApiLimitGatewayHandler extends GatewayHandler {

    @Override
    public int handler(int score) {
        System.out.println("接口限流中");
        if (score >= 80) {
            if (this.next != null) {
                return this.next.handler(score);
            }
        }
        System.out.println("接口限流，退出");
        return score;
    }
}
