package com.example.java_everyday.responsibility_chain.dao;


import com.example.java_everyday.responsibility_chain.entity.GatewayEntity;

public interface GatewayDao {
    /**
     * 根据 handlerId 获取配置项
     * @return GatewayEntity
     */
    GatewayEntity getGatewayEntity(Integer handlerId);

    /**
     * 获取第一个处理者
     * @return GatewayEntity
     */
    GatewayEntity getFirstGatewayEntity();
}
