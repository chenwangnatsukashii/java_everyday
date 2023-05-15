package com.example.java_everyday.guava;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GuavaTest {

    public static void main(String[] args) {
        Table<String, String, Integer> table = HashBasedTable.create();
        //存放元素
        table.put("Hydra", "Jan", 20);
        table.put("Hydra", "Feb", 28);

        table.put("Trunks", "Jan", 28);
        table.put("Trunks", "Feb", 16);

        //取出元素
        System.out.println(table.get("Hydra", "Feb"));

        // 1.获得key或value的集合
        //rowKey或columnKey的集合
        Set<String> rowKeys = table.rowKeySet();
        Set<String> columnKeys = table.columnKeySet();

        //value集合
        Collection<Integer> values = table.values();

        // 2.计算key对应的所有value的和
        for (String key : table.rowKeySet()) {
            Set<Map.Entry<String, Integer>> rows = table.row(key).entrySet();
            int total = 0;
            for (Map.Entry<String, Integer> row : rows) {
                total += row.getValue();
            }
            System.out.println(key + ": " + total);
        }

    }
}
