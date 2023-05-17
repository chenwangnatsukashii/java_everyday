package com.example.java_everyday.responsibility_chain.impl;

import com.example.java_everyday.responsibility_chain.GatewayHandler;

public class SessionGatewayHandler extends GatewayHandler {
    @Override
    public int handler(int score) {
        System.out.println("用户会话拦截中");
        if (score >= 95) {
            if (this.next != null) {
                return this.next.handler(score);
            }
        }
        System.out.println("用户会话拦截，退出");
        return score;
    }
}
