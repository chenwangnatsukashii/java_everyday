package com.example.java_everyday.responsibility_chain.factory;

import com.example.java_everyday.responsibility_chain.GatewayHandler;
import com.example.java_everyday.responsibility_chain.dao.GatewayDao;
import com.example.java_everyday.responsibility_chain.dao.GatewayImpl;
import com.example.java_everyday.responsibility_chain.entity.GatewayEntity;

import java.lang.reflect.InvocationTargetException;

public class GatewayHandlerEnumFactory {

    private static final GatewayDao gatewayDao = new GatewayImpl();

    // 提供静态方法，获取第一个handler
    public static GatewayHandler getFirstGatewayHandler() {

        GatewayEntity firstGatewayEntity = gatewayDao.getFirstGatewayEntity();
        GatewayHandler firstGatewayHandler = newGatewayHandler(firstGatewayEntity);
        if (firstGatewayHandler == null) {
            return null;
        }

        GatewayEntity tempGatewayEntity = firstGatewayEntity;
        Integer nextHandlerId;
        GatewayHandler tempGatewayHandler = firstGatewayHandler;

        // 迭代遍历所有handler，以及将它们链接起来
        while ((nextHandlerId = tempGatewayEntity.getNextHandlerId()) != null) {
            GatewayEntity gatewayEntity = gatewayDao.getGatewayEntity(nextHandlerId);
            GatewayHandler gatewayHandler = newGatewayHandler(gatewayEntity);
            assert tempGatewayHandler != null;
            tempGatewayHandler.setNext(gatewayHandler);
            tempGatewayHandler = gatewayHandler;
            tempGatewayEntity = gatewayEntity;
        }
        // 返回第一个handler
        return firstGatewayHandler;
    }

    /**
     * 反射实体化具体的处理者
     *
     * @return GatewayHandler
     */
    private static GatewayHandler newGatewayHandler(GatewayEntity firstGatewayEntity) {
        // 获取全限定类名
        String className = firstGatewayEntity.getConference();
        try {
            // 根据全限定类名，加载并初始化该类，即会初始化该类的静态段
            Class<?> clazz = Class.forName(className);
            return (GatewayHandler) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
