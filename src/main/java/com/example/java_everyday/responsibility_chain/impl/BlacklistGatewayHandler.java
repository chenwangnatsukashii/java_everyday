package com.example.java_everyday.responsibility_chain.impl;

import com.example.java_everyday.responsibility_chain.GatewayHandler;

public class BlacklistGatewayHandler extends GatewayHandler {

    @Override
    public int handler(int score) {
        System.out.println("黑名单检测中");
        if (score >= 90) {
            if (this.next != null) {
                return this.next.handler(score);
            }
        }
        System.out.println("黑名单，退出");
        return score;
    }
}
