package com.example.java_everyday.responsibility_chain;

import lombok.Setter;

@Setter
public abstract class GatewayHandler {
    protected GatewayHandler next;

    public abstract int handler(int score);

}
