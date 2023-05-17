package com.example.java_everyday.responsibility_chain;

import com.example.java_everyday.responsibility_chain.factory.GatewayHandlerEnumFactory;

public class GatewayClient {

    public static void main(String[] args) {
        GatewayHandler firstGatewayHandler = GatewayHandlerEnumFactory.getFirstGatewayHandler();
        assert firstGatewayHandler != null;
        firstGatewayHandler.handler(22);
    }

}
