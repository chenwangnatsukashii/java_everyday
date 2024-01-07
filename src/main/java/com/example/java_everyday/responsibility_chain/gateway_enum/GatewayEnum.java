package com.example.java_everyday.responsibility_chain.gateway_enum;

import com.example.java_everyday.responsibility_chain.entity.GatewayEntity;
import lombok.Getter;

@Getter
public enum GatewayEnum {

    // handlerId, 拦截者名称，全限定类名，preHandlerId，nextHandlerId
    API_HANDLER(new GatewayEntity(1, "api接口限流", "com.example.java_everyday.responsibility_chain.impl.ApiLimitGatewayHandler", null, 2)),
    BLACKLIST_HANDLER(new GatewayEntity(2, "黑名单拦截", "com.example.java_everyday.responsibility_chain.impl.BlacklistGatewayHandler", 1, 3)),
    SESSION_HANDLER(new GatewayEntity(3, "用户会话拦截", "com.example.java_everyday.responsibility_chain.impl.SessionGatewayHandler", 2, null));

    final GatewayEntity gatewayEntity;

    GatewayEnum(GatewayEntity gatewayEntity) {
        this.gatewayEntity = gatewayEntity;
    }

}


