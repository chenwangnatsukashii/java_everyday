package com.example.java_everyday.redis_lock.utils;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtil {

    public static List<Object> getFiledValues(Class<?> type, Object target, String[] fieldNames) throws IllegalAccessException {
        List<Field> fields = getFields(type, fieldNames);
        List<Object> valueList = new ArrayList<>();

        for (Field field : fields) {
            if (!field.canAccess(null)) {
                field.setAccessible(true);
            }

            Object value = field.get(target);
            valueList.add(value);
        }

        return valueList;
    }


    public static List<Field> getFields(Class<?> clazz, String[] fieldNames) {
        var matchFieldList = new ArrayList<Field>();

        if (fieldNames != null && fieldNames.length != 0) {
            var needFieldList = Arrays.asList(fieldNames);

            List<Field> fields = getAllField(clazz);
            for (Field field : fields) {
                if (needFieldList.contains(field.getName())) {
                    matchFieldList.add(field);
                }
            }
        }

        return matchFieldList;
    }

    public static List<Field> getAllField(Class<?> claszz) {
        var list = new ArrayList<Field>();

        if (claszz == null) {
            return list;
        }

        do {
            Field[] array = claszz.getDeclaredFields();
            list.addAll(Arrays.asList(array));
            claszz = claszz.getSuperclass();
        } while (claszz != null && claszz != Object.class);

        return list;
    }
}
