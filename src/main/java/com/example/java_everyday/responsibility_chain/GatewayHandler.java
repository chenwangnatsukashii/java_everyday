package com.example.java_everyday.responsibility_chain;

public abstract class GatewayHandler {
    protected GatewayHandler next;

    public void setNext(GatewayHandler next) {
        this.next = next;
    }

    public abstract int handler(int score);

}
